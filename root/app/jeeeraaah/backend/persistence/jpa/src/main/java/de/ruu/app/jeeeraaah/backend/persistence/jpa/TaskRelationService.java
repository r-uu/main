package de.ruu.app.jeeeraaah.backend.persistence.jpa;

import de.ruu.app.jeeeraaah.common.api.domain.TaskRelationException;
import lombok.NonNull;

/**
 * Service interface for managing task relationships using IDs.
 * This interface provides ID-based methods for adding and removing
 * task relationships, complementing the entity-based methods in TaskService.
 * <p>
 * This is the public interface for task relation operations.
 * External modules should inject this interface, not the internal implementation.
 */
public interface TaskRelationService
{
	/**
	 * Adds a subtask to a parent task.
	 *
	 * @param idTask the ID of the parent task
	 * @param idSubTask the ID of the subtask to add
	 * @throws TaskRelationException if adding the subtask creates a circular reference or violates constraints
	 */
	void addSubTask(@NonNull Long idTask, @NonNull Long idSubTask) throws TaskRelationException;

	/**
	 * Adds a predecessor to a task.
	 *
	 * @param idTask the ID of the task
	 * @param idPredecessor the ID of the predecessor task to add
	 * @throws TaskRelationException if adding the predecessor creates a circular reference or violates constraints
	 */
	void addPredecessor(@NonNull Long idTask, @NonNull Long idPredecessor) throws TaskRelationException;

	/**
	 * Adds a successor to a task.
	 *
	 * @param idTask the ID of the task
	 * @param idSuccessor the ID of the successor task to add
	 * @throws TaskRelationException if adding the successor creates a circular reference or violates constraints
	 */
	void addSuccessor(@NonNull Long idTask, @NonNull Long idSuccessor) throws TaskRelationException;

	/**
	 * Removes a subtask from a parent task.
	 *
	 * @param idTask the ID of the parent task
	 * @param idSubTask the ID of the subtask to remove
	 * @throws TaskRelationException if the relationship does not exist or cannot be removed
	 */
	void removeSubTask(@NonNull Long idTask, @NonNull Long idSubTask) throws TaskRelationException;

	/**
	 * Removes a predecessor from a task.
	 *
	 * @param idTask the ID of the task
	 * @param idPredecessor the ID of the predecessor task to remove
	 * @throws TaskRelationException if the relationship does not exist or cannot be removed
	 */
	void removePredecessor(@NonNull Long idTask, @NonNull Long idPredecessor) throws TaskRelationException;

	/**
	 * Removes a successor from a task.
	 *
	 * @param idTask the ID of the task
	 * @param idSuccessor the ID of the successor task to remove
	 * @throws TaskRelationException if the relationship does not exist or cannot be removed
	 */
	void removeSuccessor(@NonNull Long idTask, @NonNull Long idSuccessor) throws TaskRelationException;
}
