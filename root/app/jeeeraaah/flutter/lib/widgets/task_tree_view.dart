import 'package:flutter/material.dart';

import '../models/task.dart';

/// Recursive tree view for tasks – mirrors the `TreeView` / `TreeItem`-based
/// hierarchy views (`TaskHierarchyPredecessors`, `TaskHierarchySuperSubTasks`,
/// `TaskHierarchySuccessors`) in the JavaFX frontend.
///
/// [roots] is the list of root-level tasks to display.
/// [allTasks] is the full flat list used to resolve child IDs.
/// [childIdSelector] determines which IDs of [task] count as children in this
///   particular hierarchy (sub-tasks, predecessors or successors).
class TaskTreeView extends StatelessWidget {
  const TaskTreeView({
    super.key,
    required this.roots,
    required this.allTasks,
    required this.childIdSelector,
  });

  final List<Task> roots;
  final List<Task> allTasks;
  final List<int> Function(Task task) childIdSelector;

  @override
  Widget build(BuildContext context) {
    return ListView(
      children: roots
          .map(
            (t) => _TaskTreeItem(
              task: t,
              allTasks: allTasks,
              childIdSelector: childIdSelector,
              depth: 0,
            ),
          )
          .toList(),
    );
  }
}

// ---------------------------------------------------------------------------
// Private recursive tile
// ---------------------------------------------------------------------------

class _TaskTreeItem extends StatefulWidget {
  const _TaskTreeItem({
    required this.task,
    required this.allTasks,
    required this.childIdSelector,
    required this.depth,
  });

  final Task task;
  final List<Task> allTasks;
  final List<int> Function(Task task) childIdSelector;
  final int depth;

  @override
  State<_TaskTreeItem> createState() => _TaskTreeItemState();
}

class _TaskTreeItemState extends State<_TaskTreeItem> {
  bool _expanded = false;

  List<Task> get _children {
    final ids = widget.childIdSelector(widget.task);
    return widget.allTasks.where((t) => ids.contains(t.id)).toList();
  }

  @override
  Widget build(BuildContext context) {
    final children = _children;
    final hasChildren = children.isNotEmpty;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        InkWell(
          onTap: hasChildren
              ? () => setState(() => _expanded = !_expanded)
              : null,
          child: Padding(
            padding: EdgeInsets.only(
              left: 16.0 + widget.depth * 16,
              top: 4,
              bottom: 4,
              right: 8,
            ),
            child: Row(
              children: [
                if (hasChildren)
                  Icon(
                    _expanded
                        ? Icons.expand_more
                        : Icons.chevron_right,
                    size: 18,
                  )
                else
                  const SizedBox(width: 18),
                const SizedBox(width: 4),
                Icon(
                  widget.task.closed
                      ? Icons.check_circle
                      : Icons.radio_button_unchecked,
                  size: 16,
                  color: widget.task.closed ? Colors.green : null,
                ),
                const SizedBox(width: 6),
                Expanded(child: Text(widget.task.name)),
              ],
            ),
          ),
        ),
        if (_expanded)
          ...children.map(
            (child) => _TaskTreeItem(
              task: child,
              allTasks: widget.allTasks,
              childIdSelector: widget.childIdSelector,
              depth: widget.depth + 1,
            ),
          ),
      ],
    );
  }
}
