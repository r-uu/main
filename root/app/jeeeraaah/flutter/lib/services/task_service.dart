import '../models/task.dart';
import 'api_client.dart';

/// REST client for task operations.
///
/// Mirrors `TaskServiceClient` from the Java module `api.client/ws.rs`.
/// Endpoint base path: `/task`
class TaskService {
  const TaskService(this._client);

  final ApiClient _client;

  // ---------------------------------------------------------------------------
  // CRUD
  // ---------------------------------------------------------------------------

  /// `POST /task` – create a new task.
  Future<Task> create(Task task) async {
    final json = await _client.post('/task', task.toJson());
    return Task.fromJson(json as Map<String, dynamic>);
  }

  /// `GET /task/{id}` – read a single task.
  Future<Task> read(int id) async {
    final json = await _client.get('/task/$id');
    return Task.fromJson(json as Map<String, dynamic>);
  }

  /// `PUT /task` – update an existing task.
  Future<Task> update(Task task) async {
    final json = await _client.put('/task', task.toJson());
    return Task.fromJson(json as Map<String, dynamic>);
  }

  /// `DELETE /task/{id}` – delete a task.
  Future<void> delete(int id) => _client.delete('/task/$id');

  /// `GET /task/all` – retrieve all tasks.
  Future<List<Task>> findAll() async {
    final json = await _client.get('/task/all');
    return (json as List<dynamic>)
        .map((e) => Task.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  // ---------------------------------------------------------------------------
  // Relationship management (mirrors TaskServiceClient relationship endpoints)
  // ---------------------------------------------------------------------------

  /// `POST /task/{id}/add-predecessor/{predecessorId}`
  Future<void> addPredecessor(int taskId, int predecessorId) =>
      _client.post('/task/$taskId/add-predecessor/$predecessorId', {});

  /// `DELETE /task/{id}/remove-predecessor/{predecessorId}`
  Future<void> removePredecessor(int taskId, int predecessorId) =>
      _client.delete('/task/$taskId/remove-predecessor/$predecessorId');

  /// `POST /task/{id}/add-successor/{successorId}`
  Future<void> addSuccessor(int taskId, int successorId) =>
      _client.post('/task/$taskId/add-successor/$successorId', {});

  /// `DELETE /task/{id}/remove-successor/{successorId}`
  Future<void> removeSuccessor(int taskId, int successorId) =>
      _client.delete('/task/$taskId/remove-successor/$successorId');

  /// `POST /task/{id}/add-sub/{subTaskId}`
  Future<void> addSubTask(int taskId, int subTaskId) =>
      _client.post('/task/$taskId/add-sub/$subTaskId', {});

  /// `DELETE /task/{id}/remove-sub/{subTaskId}`
  Future<void> removeSubTask(int taskId, int subTaskId) =>
      _client.delete('/task/$taskId/remove-sub/$subTaskId');

  /// `GET /task/{id}/with-related` – task including all related tasks.
  Future<Task> readWithRelated(int id) async {
    final json = await _client.get('/task/$id/with-related');
    return Task.fromJson(json as Map<String, dynamic>);
  }

  /// `DELETE /task/{id}/remove-neighbours` – remove all neighbour relations.
  Future<void> removeNeighbours(int id) =>
      _client.delete('/task/$id/remove-neighbours');
}
