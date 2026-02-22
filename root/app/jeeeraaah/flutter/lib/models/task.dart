// Mirrors TaskDTO / TaskBean from the Java backend.
// Fields map 1-to-1 with the JSON produced by TaskDTO (Jackson serialisation).

class Task {
  final int? id;
  final int? version;
  final String name;
  final String? description;
  final DateTime? start;
  final DateTime? end;
  final bool closed;

  /// DB id of the owning TaskGroup.
  final int taskGroupId;

  /// Optional super-task (hierarchical parent within the same TaskGroup).
  final int? superTaskId;

  /// Sub-tasks (hierarchical children within the same TaskGroup).
  final List<int> subTaskIds;

  /// Tasks that must finish before this task can start.
  final List<int> predecessorIds;

  /// Tasks that cannot start until this task is finished.
  final List<int> successorIds;

  const Task({
    this.id,
    this.version,
    required this.name,
    this.description,
    this.start,
    this.end,
    this.closed = false,
    required this.taskGroupId,
    this.superTaskId,
    this.subTaskIds = const [],
    this.predecessorIds = const [],
    this.successorIds = const [],
  });

  Task copyWith({
    int? id,
    int? version,
    String? name,
    String? description,
    DateTime? start,
    DateTime? end,
    bool? closed,
    int? taskGroupId,
    int? superTaskId,
    List<int>? subTaskIds,
    List<int>? predecessorIds,
    List<int>? successorIds,
  }) {
    return Task(
      id: id ?? this.id,
      version: version ?? this.version,
      name: name ?? this.name,
      description: description ?? this.description,
      start: start ?? this.start,
      end: end ?? this.end,
      closed: closed ?? this.closed,
      taskGroupId: taskGroupId ?? this.taskGroupId,
      superTaskId: superTaskId ?? this.superTaskId,
      subTaskIds: subTaskIds ?? this.subTaskIds,
      predecessorIds: predecessorIds ?? this.predecessorIds,
      successorIds: successorIds ?? this.successorIds,
    );
  }

  factory Task.fromJson(Map<String, dynamic> json) {
    return Task(
      id: json['id'] as int?,
      version: json['version'] as int?,
      name: json['name'] as String,
      description: json['description'] as String?,
      start: json['start'] != null
          ? DateTime.parse(json['start'] as String)
          : null,
      end: json['end'] != null ? DateTime.parse(json['end'] as String) : null,
      closed: (json['closed'] as bool?) ?? false,
      taskGroupId: (json['taskGroup'] as Map<String, dynamic>)['id'] as int,
      superTaskId:
          (json['superTask'] as Map<String, dynamic>?)?['id'] as int?,
      subTaskIds: ((json['subTasks'] as List<dynamic>?) ?? [])
          .map((t) => (t as Map<String, dynamic>)['id'] as int)
          .toList(),
      predecessorIds: ((json['predecessors'] as List<dynamic>?) ?? [])
          .map((t) => (t as Map<String, dynamic>)['id'] as int)
          .toList(),
      successorIds: ((json['successors'] as List<dynamic>?) ?? [])
          .map((t) => (t as Map<String, dynamic>)['id'] as int)
          .toList(),
    );
  }

  Map<String, dynamic> toJson() => {
        if (id != null) 'id': id,
        if (version != null) 'version': version,
        'name': name,
        if (description != null) 'description': description,
        if (start != null) 'start': start!.toIso8601String().substring(0, 10),
        if (end != null) 'end': end!.toIso8601String().substring(0, 10),
        'closed': closed,
        'taskGroup': {'id': taskGroupId},
        if (superTaskId != null) 'superTask': {'id': superTaskId},
      };

  @override
  String toString() => 'Task(id: $id, name: $name, closed: $closed)';
}
