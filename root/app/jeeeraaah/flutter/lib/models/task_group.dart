// Mirrors TaskGroupDTO / TaskGroupBean from the Java backend.
// Fields map 1-to-1 with the JSON produced by TaskGroupDTO (Jackson serialisation).

class TaskGroup {
  final int? id;
  final int? version;
  final String name;
  final String? description;

  /// IDs of tasks that belong to this group (may be empty when using "flat" endpoint).
  final List<int> taskIds;

  const TaskGroup({
    this.id,
    this.version,
    required this.name,
    this.description,
    this.taskIds = const [],
  });

  TaskGroup copyWith({
    int? id,
    int? version,
    String? name,
    String? description,
    List<int>? taskIds,
  }) {
    return TaskGroup(
      id: id ?? this.id,
      version: version ?? this.version,
      name: name ?? this.name,
      description: description ?? this.description,
      taskIds: taskIds ?? this.taskIds,
    );
  }

  /// Deserialise from `GET /taskgroup/all-flat` response item
  /// (TaskGroupDTOFlat – contains only id, name, description).
  factory TaskGroup.fromJsonFlat(Map<String, dynamic> json) {
    return TaskGroup(
      id: json['id'] as int?,
      version: json['version'] as int?,
      name: json['name'] as String,
      description: json['description'] as String?,
    );
  }

  /// Deserialise from `GET /taskgroup/{id}` / `GET /taskgroup/{id}/with-tasks`
  /// response (TaskGroupDTO – contains full task list).
  factory TaskGroup.fromJson(Map<String, dynamic> json) {
    return TaskGroup(
      id: json['id'] as int?,
      version: json['version'] as int?,
      name: json['name'] as String,
      description: json['description'] as String?,
      taskIds: ((json['tasks'] as List<dynamic>?) ?? [])
          .map((t) => (t as Map<String, dynamic>)['id'] as int)
          .toList(),
    );
  }

  Map<String, dynamic> toJson() => {
        if (id != null) 'id': id,
        if (version != null) 'version': version,
        'name': name,
        if (description != null) 'description': description,
      };

  @override
  String toString() => 'TaskGroup(id: $id, name: $name)';
}
