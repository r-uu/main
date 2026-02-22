import 'package:flutter/material.dart';

import '../models/task_group.dart';

/// Drop-down selector for task groups – mirrors the `TaskGroupSelector`
/// JavaFX component embedded in `DashController`.
class TaskGroupSelector extends StatelessWidget {
  const TaskGroupSelector({
    super.key,
    required this.groups,
    required this.selected,
    required this.onSelected,
  });

  final List<TaskGroup> groups;
  final TaskGroup? selected;
  final ValueChanged<TaskGroup> onSelected;

  @override
  Widget build(BuildContext context) {
    if (groups.isEmpty) {
      return const Text('No task groups available');
    }
    return DropdownButtonFormField<TaskGroup>(
      value: selected,
      hint: const Text('Select task group…'),
      decoration: const InputDecoration(
        border: OutlineInputBorder(),
        isDense: true,
        contentPadding: EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      ),
      items: groups
          .map(
            (g) => DropdownMenuItem<TaskGroup>(
              value: g,
              child: Text(g.name),
            ),
          )
          .toList(),
      onChanged: (g) {
        if (g != null) onSelected(g);
      },
    );
  }
}
