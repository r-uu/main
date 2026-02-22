import '../models/task_group.dart';
import 'api_client.dart';

/// REST client for task-group operations.
///
/// Mirrors `TaskGroupServiceClient` from the Java module `api.client/ws.rs`.
/// Endpoint base path: `/taskgroup`
class TaskGroupService {
  const TaskGroupService(this._client);

  final ApiClient _client;

  // ---------------------------------------------------------------------------
  // CRUD
  // ---------------------------------------------------------------------------

  /// `POST /taskgroup` – create a new task group.
  Future<TaskGroup> create(TaskGroup group) async {
    final json = await _client.post('/taskgroup', group.toJson());
    return TaskGroup.fromJson(json as Map<String, dynamic>);
  }

  /// `GET /taskgroup/{id}` – read a single task group.
  Future<TaskGroup> read(int id) async {
    final json = await _client.get('/taskgroup/$id');
    return TaskGroup.fromJson(json as Map<String, dynamic>);
  }

  /// `PUT /taskgroup` – update an existing task group.
  Future<TaskGroup> update(TaskGroup group) async {
    final json = await _client.put('/taskgroup', group.toJson());
    return TaskGroup.fromJson(json as Map<String, dynamic>);
  }

  /// `DELETE /taskgroup/{id}` – delete a task group.
  Future<void> delete(int id) => _client.delete('/taskgroup/$id');

  // ---------------------------------------------------------------------------
  // Query endpoints
  // ---------------------------------------------------------------------------

  /// `GET /taskgroup/all-flat` – all task groups without their task lists.
  /// This is the lightweight endpoint used to populate the group selector
  /// (mirrors `TaskGroupFlat` / `TaskGroupDTOFlat` in the Java codebase).
  Future<List<TaskGroup>> findAllFlat() async {
    final json = await _client.get('/taskgroup/all-flat');
    return (json as List<dynamic>)
        .map((e) => TaskGroup.fromJsonFlat(e as Map<String, dynamic>))
        .toList();
  }

  /// `GET /taskgroup/{id}/with-tasks` – task group including its task list.
  Future<TaskGroup> readWithTasks(int id) async {
    final json = await _client.get('/taskgroup/$id/with-tasks');
    return TaskGroup.fromJson(json as Map<String, dynamic>);
  }

  /// `GET /taskgroup/{id}/with-tasks-and-direct-neighbours`
  Future<TaskGroup> readWithTasksAndDirectNeighbours(int id) async {
    final json = await _client
        .get('/taskgroup/$id/with-tasks-and-direct-neighbours');
    return TaskGroup.fromJson(json as Map<String, dynamic>);
  }

  /// `DELETE /taskgroup/{id}/remove-task/{taskId}`
  Future<void> removeTask(int groupId, int taskId) =>
      _client.delete('/taskgroup/$groupId/remove-task/$taskId');
}
