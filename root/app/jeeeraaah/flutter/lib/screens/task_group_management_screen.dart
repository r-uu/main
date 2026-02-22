import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../models/task_group.dart';
import '../services/task_group_service.dart';

/// Task-group management screen – mirrors `TaskGroupManagementApp` /
/// `TaskGroupManagementController` from the JavaFX frontend.
///
/// Shows a list of all task groups with create / edit / delete actions.
class TaskGroupManagementScreen extends StatefulWidget {
  const TaskGroupManagementScreen({super.key});

  @override
  State<TaskGroupManagementScreen> createState() =>
      _TaskGroupManagementScreenState();
}

class _TaskGroupManagementScreenState
    extends State<TaskGroupManagementScreen> {
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
      final groups =
          await context.read<TaskGroupService>().findAllFlat();
      setState(() => _groups = groups);
    } catch (e) {
      setState(() => _error = e.toString());
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  // ---------------------------------------------------------------------------
  // CRUD
  // ---------------------------------------------------------------------------

  Future<void> _create() async {
    final result = await showDialog<TaskGroup>(
      context: context,
      builder: (ctx) => const _TaskGroupEditorDialog(),
    );
    if (result == null) return;
    try {
      final created =
          await context.read<TaskGroupService>().create(result);
      setState(() => _groups.add(created));
    } catch (e) {
      if (mounted) _showError(e.toString());
    }
  }

  Future<void> _edit(TaskGroup group) async {
    final result = await showDialog<TaskGroup>(
      context: context,
      builder: (ctx) => _TaskGroupEditorDialog(initial: group),
    );
    if (result == null) return;
    try {
      final updated =
          await context.read<TaskGroupService>().update(result);
      setState(() {
        final idx = _groups.indexWhere((g) => g.id == updated.id);
        if (idx >= 0) _groups[idx] = updated;
      });
    } catch (e) {
      if (mounted) _showError(e.toString());
    }
  }

  Future<void> _delete(TaskGroup group) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Delete task group'),
        content: Text('Delete "${group.name}"?\nThis also removes all its tasks.'),
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
      await context.read<TaskGroupService>().delete(group.id!);
      setState(() => _groups.removeWhere((g) => g.id == group.id));
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
        title: const Text('Task Groups'),
        actions: [
          IconButton(
              tooltip: 'Refresh',
              icon: const Icon(Icons.refresh),
              onPressed: _load),
          IconButton(
              tooltip: 'New task group',
              icon: const Icon(Icons.add),
              onPressed: _create),
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
              : _groups.isEmpty
                  ? const Center(child: Text('No task groups found.'))
                  : ListView.builder(
                      itemCount: _groups.length,
                      itemBuilder: (ctx, i) {
                        final g = _groups[i];
                        return ListTile(
                          leading: const Icon(Icons.folder_open),
                          title: Text(g.name),
                          subtitle: g.description != null
                              ? Text(g.description!)
                              : null,
                          trailing: Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              IconButton(
                                icon:
                                    const Icon(Icons.edit, size: 18),
                                tooltip: 'Edit',
                                onPressed: () => _edit(g),
                              ),
                              IconButton(
                                icon: const Icon(
                                    Icons.delete_outline,
                                    size: 18),
                                tooltip: 'Delete',
                                onPressed: () => _delete(g),
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
// Editor dialog
// ---------------------------------------------------------------------------

class _TaskGroupEditorDialog extends StatefulWidget {
  const _TaskGroupEditorDialog({this.initial});

  final TaskGroup? initial;

  @override
  State<_TaskGroupEditorDialog> createState() =>
      _TaskGroupEditorDialogState();
}

class _TaskGroupEditorDialogState
    extends State<_TaskGroupEditorDialog> {
  final _formKey = GlobalKey<FormState>();
  late final TextEditingController _nameCtrl;
  late final TextEditingController _descCtrl;

  @override
  void initState() {
    super.initState();
    _nameCtrl = TextEditingController(text: widget.initial?.name);
    _descCtrl =
        TextEditingController(text: widget.initial?.description);
  }

  @override
  void dispose() {
    _nameCtrl.dispose();
    _descCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final title =
        widget.initial == null ? 'New Task Group' : 'Edit Task Group';
    return AlertDialog(
      title: Text(title),
      content: SizedBox(
        width: 400,
        child: Form(
          key: _formKey,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextFormField(
                controller: _nameCtrl,
                decoration:
                    const InputDecoration(labelText: 'Name *'),
                autofocus: true,
                validator: (v) =>
                    (v == null || v.trim().isEmpty) ? 'Required' : null,
              ),
              const SizedBox(height: 8),
              TextFormField(
                controller: _descCtrl,
                decoration:
                    const InputDecoration(labelText: 'Description'),
                maxLines: 3,
              ),
            ],
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
            final group = TaskGroup(
              id: widget.initial?.id,
              version: widget.initial?.version,
              name: _nameCtrl.text.trim(),
              description: _descCtrl.text.trim().isEmpty
                  ? null
                  : _descCtrl.text.trim(),
            );
            Navigator.pop(context, group);
          },
          child: const Text('Save'),
        ),
      ],
    );
  }
}
