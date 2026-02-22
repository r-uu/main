import 'package:flutter_test/flutter_test.dart';

import 'package:jeeeraaah/models/task.dart';
import 'package:jeeeraaah/models/task_group.dart';

void main() {
  group('Task model', () {
    test('fromJson round-trip preserves required fields', () {
      const groupId = 1;
      final json = {
        'id': 42,
        'version': 0,
        'name': 'Test task',
        'description': 'A description',
        'start': '2025-01-01',
        'end': '2025-12-31',
        'closed': false,
        'taskGroup': {'id': groupId},
        'subTasks': [],
        'predecessors': [],
        'successors': [],
      };

      final task = Task.fromJson(json);

      expect(task.id, 42);
      expect(task.name, 'Test task');
      expect(task.description, 'A description');
      expect(task.closed, isFalse);
      expect(task.taskGroupId, groupId);
      expect(task.start, DateTime(2025, 1, 1));
      expect(task.end, DateTime(2025, 12, 31));
    });

    test('toJson omits null fields', () {
      const task = Task(name: 'Minimal', taskGroupId: 5);
      final json = task.toJson();

      expect(json['name'], 'Minimal');
      expect(json['closed'], isFalse);
      expect(json.containsKey('id'), isFalse);
      expect(json.containsKey('description'), isFalse);
      expect(json.containsKey('start'), isFalse);
    });

    test('copyWith replaces individual fields', () {
      const original = Task(id: 1, name: 'Old', taskGroupId: 2);
      final updated = original.copyWith(name: 'New', closed: true);

      expect(updated.id, 1);
      expect(updated.name, 'New');
      expect(updated.closed, isTrue);
      expect(updated.taskGroupId, 2);
    });
  });

  group('TaskGroup model', () {
    test('fromJsonFlat round-trip', () {
      final json = {
        'id': 7,
        'version': 1,
        'name': 'Sprint 1',
        'description': 'First sprint',
      };

      final group = TaskGroup.fromJsonFlat(json);

      expect(group.id, 7);
      expect(group.name, 'Sprint 1');
      expect(group.description, 'First sprint');
      expect(group.taskIds, isEmpty);
    });

    test('fromJson includes task ids', () {
      final json = {
        'id': 3,
        'version': 0,
        'name': 'Backlog',
        'tasks': [
          {'id': 10},
          {'id': 11},
        ],
      };

      final group = TaskGroup.fromJson(json);

      expect(group.taskIds, containsAll([10, 11]));
    });

    test('toJson omits null fields', () {
      const group = TaskGroup(name: 'New Group');
      final json = group.toJson();

      expect(json['name'], 'New Group');
      expect(json.containsKey('id'), isFalse);
      expect(json.containsKey('description'), isFalse);
    });
  });
}
