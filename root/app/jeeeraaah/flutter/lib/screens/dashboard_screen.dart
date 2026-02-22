import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';

import '../models/task.dart';
import '../models/task_group.dart';
import '../services/auth_service.dart';
import '../services/task_group_service.dart';
import '../services/task_service.dart';
import '../widgets/task_group_selector.dart';
import '../widgets/task_tree_view.dart';

/// Dashboard screen – mirrors `DashApp` / `DashController` from the JavaFX
/// frontend.
///
/// Layout (mirrors the JavaFX SplitPane + AnchorPanes):
///   ┌─────────────────────────────────────────────────┐
///   │  TaskGroup selector  [ + Add ] [ ✏ Edit ] [🗑]  │
///   ├──────────────┬──────────────────┬───────────────┤
///   │ Predecessors │  Super/Sub tasks │   Successors  │
///   └──────────────┴──────────────────┴───────────────┘
class DashboardScreen extends StatefulWidget {
  const DashboardScreen({super.key});

  @override
  State<DashboardScreen> createState() => _DashboardScreenState();
}

class _DashboardScreenState extends State<DashboardScreen> {
  List<TaskGroup> _groups = [];
  TaskGroup? _selectedGroup;
  List<Task> _tasks = [];
  bool _loadingGroups = false;
  bool _loadingTasks = false;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadGroups();
  }

  // ---------------------------------------------------------------------------
  // Data loading
  // ---------------------------------------------------------------------------

  Future<void> _loadGroups() async {
    setState(() {
      _loadingGroups = true;
      _error = null;
    });
    try {
      final groups =
          await context.read<TaskGroupService>().findAllFlat();
      setState(() => _groups = groups);
    } catch (e) {
      setState(() => _error = e.toString());
    } finally {
      if (mounted) setState(() => _loadingGroups = false);
    }
  }

  Future<void> _onGroupSelected(TaskGroup group) async {
    setState(() {
      _selectedGroup = group;
      _loadingTasks = true;
      _error = null;
    });
    try {
      final fullGroup = await context
          .read<TaskGroupService>()
          .readWithTasksAndDirectNeighbours(group.id!);
      final tasks = await Future.wait(
        fullGroup.taskIds.map((id) =>
            context.read<TaskService>().readWithRelated(id)),
      );
      setState(() => _tasks = tasks);
    } catch (e) {
      setState(() => _error = e.toString());
    } finally {
      if (mounted) setState(() => _loadingTasks = false);
    }
  }

  // ---------------------------------------------------------------------------
  // Task group CRUD helpers
  // ---------------------------------------------------------------------------

  Future<void> _addGroup() async {
    final name = await _promptName(context, title: 'New Task Group');
    if (name == null) return;
    try {
      await context
          .read<TaskGroupService>()
          .create(TaskGroup(name: name));
      await _loadGroups();
    } catch (e) {
      if (mounted) _showError(e.toString());
    }
  }

  Future<void> _editGroup() async {
    if (_selectedGroup == null) return;
    final name = await _promptName(
      context,
      title: 'Rename Task Group',
      initial: _selectedGroup!.name,
    );
    if (name == null) return;
    try {
      await context
          .read<TaskGroupService>()
          .update(_selectedGroup!.copyWith(name: name));
      await _loadGroups();
    } catch (e) {
      if (mounted) _showError(e.toString());
    }
  }

  Future<void> _deleteGroup() async {
    if (_selectedGroup == null) return;
    final confirmed = await _confirm(
      context,
      message:
          'Delete task group "${_selectedGroup!.name}"?',
    );
    if (!confirmed) return;
    try {
      await context
          .read<TaskGroupService>()
          .delete(_selectedGroup!.id!);
      setState(() {
        _groups.remove(_selectedGroup);
        _selectedGroup = null;
        _tasks = [];
      });
    } catch (e) {
      if (mounted) _showError(e.toString());
    }
  }

  // ---------------------------------------------------------------------------
  // UI helpers
  // ---------------------------------------------------------------------------

  void _showError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: Theme.of(context).colorScheme.error,
      ),
    );
  }

  Future<void> _logout() async {
    await context.read<AuthService>().logout();
    if (mounted) context.go('/login');
  }

  // ---------------------------------------------------------------------------
  // Build
  // ---------------------------------------------------------------------------

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('jeeeraaah – Dashboard'),
        actions: [
          TextButton.icon(
            onPressed: () => context.go('/tasks'),
            icon: const Icon(Icons.task_alt),
            label: const Text('Tasks'),
          ),
          TextButton.icon(
            onPressed: () => context.go('/taskgroups'),
            icon: const Icon(Icons.folder_open),
            label: const Text('Task Groups'),
          ),
          IconButton(
            tooltip: 'Logout',
            icon: const Icon(Icons.logout),
            onPressed: _logout,
          ),
        ],
      ),
      body: Column(
        children: [
          // ── Group selector toolbar ──────────────────────────────────────
          Padding(
            padding: const EdgeInsets.all(8),
            child: Row(
              children: [
                Expanded(
                  child: _loadingGroups
                      ? const LinearProgressIndicator()
                      : TaskGroupSelector(
                          groups: _groups,
                          selected: _selectedGroup,
                          onSelected: _onGroupSelected,
                        ),
                ),
                const SizedBox(width: 8),
                IconButton(
                  tooltip: 'Add task group',
                  icon: const Icon(Icons.add),
                  onPressed: _addGroup,
                ),
                IconButton(
                  tooltip: 'Edit task group',
                  icon: const Icon(Icons.edit),
                  onPressed:
                      _selectedGroup != null ? _editGroup : null,
                ),
                IconButton(
                  tooltip: 'Delete task group',
                  icon: const Icon(Icons.delete_outline),
                  onPressed:
                      _selectedGroup != null ? _deleteGroup : null,
                ),
              ],
            ),
          ),
          if (_error != null)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 8),
              child: Text(_error!,
                  style: TextStyle(
                      color: Theme.of(context).colorScheme.error)),
            ),
          // ── Three-column hierarchy view ─────────────────────────────────
          Expanded(
            child: _loadingTasks
                ? const Center(child: CircularProgressIndicator())
                : _selectedGroup == null
                    ? const Center(
                        child: Text('Select a task group to view tasks.'))
                    : Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Expanded(
                            child: _HierarchyPanel(
                              title: 'Predecessors',
                              tasks: _tasks
                                  .where((t) =>
                                      t.predecessorIds.isNotEmpty)
                                  .toList(),
                              allTasks: _tasks,
                              idSelector: (t) => t.predecessorIds,
                            ),
                          ),
                          const VerticalDivider(width: 1),
                          Expanded(
                            child: _HierarchyPanel(
                              title: 'Super / Sub tasks',
                              tasks: _tasks
                                  .where((t) =>
                                      t.superTaskId == null &&
                                      t.subTaskIds.isNotEmpty)
                                  .toList(),
                              allTasks: _tasks,
                              idSelector: (t) => t.subTaskIds,
                            ),
                          ),
                          const VerticalDivider(width: 1),
                          Expanded(
                            child: _HierarchyPanel(
                              title: 'Successors',
                              tasks: _tasks
                                  .where((t) =>
                                      t.successorIds.isNotEmpty)
                                  .toList(),
                              allTasks: _tasks,
                              idSelector: (t) => t.successorIds,
                            ),
                          ),
                        ],
                      ),
          ),
        ],
      ),
    );
  }
}

// ---------------------------------------------------------------------------
// Private helper widgets
// ---------------------------------------------------------------------------

class _HierarchyPanel extends StatelessWidget {
  const _HierarchyPanel({
    required this.title,
    required this.tasks,
    required this.allTasks,
    required this.idSelector,
  });

  final String title;
  final List<Task> tasks;
  final List<Task> allTasks;
  final List<int> Function(Task) idSelector;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.all(8),
          child: Text(title,
              style: Theme.of(context).textTheme.titleSmall),
        ),
        const Divider(height: 1),
        Expanded(
          child: tasks.isEmpty
              ? const Center(child: Text('–'))
              : TaskTreeView(
                  roots: tasks,
                  allTasks: allTasks,
                  childIdSelector: idSelector,
                ),
        ),
      ],
    );
  }
}

// ---------------------------------------------------------------------------
// Utility dialogs
// ---------------------------------------------------------------------------

Future<String?> _promptName(
  BuildContext context, {
  required String title,
  String? initial,
}) async {
  final controller = TextEditingController(text: initial);
  try {
    final result = await showDialog<String>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text(title),
        content: TextField(
          controller: controller,
          autofocus: true,
          decoration: const InputDecoration(labelText: 'Name'),
        ),
        actions: [
          TextButton(
              onPressed: () => Navigator.pop(ctx),
              child: const Text('Cancel')),
          FilledButton(
              onPressed: () => Navigator.pop(ctx, controller.text.trim()),
              child: const Text('OK')),
        ],
      ),
    );
    return (result != null && result.isNotEmpty) ? result : null;
  } finally {
    controller.dispose();
  }
}

Future<bool> _confirm(BuildContext context, {required String message}) async {
  final result = await showDialog<bool>(
    context: context,
    builder: (ctx) => AlertDialog(
      title: const Text('Confirm'),
      content: Text(message),
      actions: [
        TextButton(
            onPressed: () => Navigator.pop(ctx, false),
            child: const Text('Cancel')),
        FilledButton(
            onPressed: () => Navigator.pop(ctx, true),
            child: const Text('Delete')),
      ],
    ),
  );
  return result ?? false;
}
