import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';

import '../models/task.dart';
import '../models/task_group.dart';
import '../services/task_group_service.dart';
import '../services/task_service.dart';

/// Task management screen – mirrors `TaskManagementApp` / `TaskManagementController`
/// from the JavaFX frontend.
///
/// Shows a list/tree of tasks with create / edit / delete actions.
class TaskManagementScreen extends StatefulWidget {
  const TaskManagementScreen({super.key});

  @override
  State<TaskManagementScreen> createState() =>
      _TaskManagementScreenState();
}

class _TaskManagementScreenState extends State<TaskManagementScreen> {
  List<Task> _tasks = [];
  List<TaskGroup> _groups = [];
  bool _loading = false;
  String? _error;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() {
      _loading = true;
      _error = null;
    });
    try {
      final results = await Future.wait([
        context.read<TaskService>().findAll(),
        context.read<TaskGroupService>().findAllFlat(),
      ]);
      setState(() {
        _tasks = results[0] as List<Task>;
        _groups = results[1] as List<TaskGroup>;
      });
    } catch (e) {
      setState(() => _error = e.toString());
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  String _groupName(int id) =>
      _groups.firstWhere((g) => g.id == id,
          orElse: () => TaskGroup(name: 'Group $id')).name;

  // ---------------------------------------------------------------------------
  // CRUD
  // ---------------------------------------------------------------------------

  Future<void> _createTask() async {
    if (_groups.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
            content: Text('Create at least one task group first.')),
      );
      return;
    }
    final result = await showDialog<Task>(
      context: context,
      builder: (ctx) => _TaskEditorDialog(groups: _groups),
    );
    if (result == null) return;
    try {
      final created = await context.read<TaskService>().create(result);
      setState(() => _tasks.add(created));
    } catch (e) {
      if (mounted) _showError(e.toString());
    }
  }

  Future<void> _editTask(Task task) async {
    final result = await showDialog<Task>(
      context: context,
      builder: (ctx) =>
          _TaskEditorDialog(groups: _groups, initial: task),
    );
    if (result == null) return;
    try {
      final updated = await context.read<TaskService>().update(result);
      setState(() {
        final idx = _tasks.indexWhere((t) => t.id == updated.id);
        if (idx >= 0) _tasks[idx] = updated;
      });
    } catch (e) {
      if (mounted) _showError(e.toString());
    }
  }

  Future<void> _deleteTask(Task task) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Delete task'),
        content: Text('Delete "${task.name}"?'),
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
    if (confirmed != true) return;
    try {
      await context.read<TaskService>().delete(task.id!);
      setState(() => _tasks.removeWhere((t) => t.id == task.id));
    } catch (e) {
      if (mounted) _showError(e.toString());
    }
  }

  void _showError(String msg) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(msg),
        backgroundColor: Theme.of(context).colorScheme.error,
      ),
    );
  }

  // ---------------------------------------------------------------------------
  // Build
  // ---------------------------------------------------------------------------

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Tasks'),
        actions: [
          IconButton(
              tooltip: 'Refresh',
              icon: const Icon(Icons.refresh),
              onPressed: _load),
          IconButton(
              tooltip: 'New task',
              icon: const Icon(Icons.add),
              onPressed: _createTask),
        ],
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? Center(
                  child: Text(_error!,
                      style: TextStyle(
                          color:
                              Theme.of(context).colorScheme.error)))
              : _tasks.isEmpty
                  ? const Center(child: Text('No tasks found.'))
                  : ListView.builder(
                      itemCount: _tasks.length,
                      itemBuilder: (ctx, i) {
                        final t = _tasks[i];
                        return ListTile(
                          leading: Icon(
                            t.closed
                                ? Icons.check_circle
                                : Icons.radio_button_unchecked,
                            color: t.closed ? Colors.green : null,
                          ),
                          title: Text(t.name),
                          subtitle: Text(_groupName(t.taskGroupId)),
                          trailing: Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              if (t.start != null)
                                Text(
                                  DateFormat('yyyy-MM-dd')
                                      .format(t.start!),
                                  style: Theme.of(ctx)
                                      .textTheme
                                      .bodySmall,
                                ),
                              IconButton(
                                icon: const Icon(Icons.edit, size: 18),
                                tooltip: 'Edit',
                                onPressed: () => _editTask(t),
                              ),
                              IconButton(
                                icon: const Icon(Icons.delete_outline,
                                    size: 18),
                                tooltip: 'Delete',
                                onPressed: () => _deleteTask(t),
                              ),
                            ],
                          ),
                        );
                      },
                    ),
    );
  }
}

// ---------------------------------------------------------------------------
// Task editor dialog
// ---------------------------------------------------------------------------

class _TaskEditorDialog extends StatefulWidget {
  const _TaskEditorDialog({required this.groups, this.initial});

  final List<TaskGroup> groups;
  final Task? initial;

  @override
  State<_TaskEditorDialog> createState() => _TaskEditorDialogState();
}

class _TaskEditorDialogState extends State<_TaskEditorDialog> {
  final _formKey = GlobalKey<FormState>();
  late final TextEditingController _nameCtrl;
  late final TextEditingController _descCtrl;
  late int? _groupId;
  DateTime? _start;
  DateTime? _end;
  bool _closed = false;

  @override
  void initState() {
    super.initState();
    final t = widget.initial;
    _nameCtrl = TextEditingController(text: t?.name);
    _descCtrl = TextEditingController(text: t?.description);
    _groupId = t?.taskGroupId ?? widget.groups.first.id;
    _start = t?.start;
    _end = t?.end;
    _closed = t?.closed ?? false;
  }

  @override
  void dispose() {
    _nameCtrl.dispose();
    _descCtrl.dispose();
    super.dispose();
  }

  Future<void> _pickDate(
      BuildContext context, bool isStart) async {
    final initial = (isStart ? _start : _end) ?? DateTime.now();
    final picked = await showDatePicker(
      context: context,
      initialDate: initial,
      firstDate: DateTime(2000),
      lastDate: DateTime(2100),
    );
    if (picked == null) return;
    setState(() {
      if (isStart) {
        _start = picked;
      } else {
        _end = picked;
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final title = widget.initial == null ? 'New Task' : 'Edit Task';
    return AlertDialog(
      title: Text(title),
      content: SizedBox(
        width: 480,
        child: Form(
          key: _formKey,
          child: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                TextFormField(
                  controller: _nameCtrl,
                  decoration: const InputDecoration(labelText: 'Name *'),
                  autofocus: true,
                  validator: (v) => (v == null || v.trim().isEmpty)
                      ? 'Required'
                      : null,
                ),
                const SizedBox(height: 8),
                TextFormField(
                  controller: _descCtrl,
                  decoration:
                      const InputDecoration(labelText: 'Description'),
                  maxLines: 3,
                ),
                const SizedBox(height: 8),
                DropdownButtonFormField<int>(
                  value: _groupId,
                  decoration:
                      const InputDecoration(labelText: 'Task Group *'),
                  items: widget.groups
                      .map((g) => DropdownMenuItem(
                          value: g.id, child: Text(g.name)))
                      .toList(),
                  onChanged: (v) => setState(() => _groupId = v),
                  validator: (v) =>
                      v == null ? 'Required' : null,
                ),
                const SizedBox(height: 8),
                Row(
                  children: [
                    Expanded(
                      child: TextButton.icon(
                        icon: const Icon(Icons.calendar_today, size: 16),
                        label: Text(_start != null
                            ? DateFormat('yyyy-MM-dd').format(_start!)
                            : 'Start date'),
                        onPressed: () => _pickDate(context, true),
                      ),
                    ),
                    Expanded(
                      child: TextButton.icon(
                        icon: const Icon(Icons.event, size: 16),
                        label: Text(_end != null
                            ? DateFormat('yyyy-MM-dd').format(_end!)
                            : 'End date'),
                        onPressed: () => _pickDate(context, false),
                      ),
                    ),
                  ],
                ),
                CheckboxListTile(
                  title: const Text('Closed'),
                  value: _closed,
                  onChanged: (v) =>
                      setState(() => _closed = v ?? false),
                  contentPadding: EdgeInsets.zero,
                ),
              ],
            ),
          ),
        ),
      ),
      actions: [
        TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cancel')),
        FilledButton(
          onPressed: () {
            if (!_formKey.currentState!.validate()) return;
            final task = Task(
              id: widget.initial?.id,
              version: widget.initial?.version,
              name: _nameCtrl.text.trim(),
              description: _descCtrl.text.trim().isEmpty
                  ? null
                  : _descCtrl.text.trim(),
              taskGroupId: _groupId!,
              start: _start,
              end: _end,
              closed: _closed,
            );
            Navigator.pop(context, task);
          },
          child: const Text('Save'),
        ),
      ],
    );
  }
}
