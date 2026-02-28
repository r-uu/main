package de.ruu.app.jeeeraaah.common.api.domain;

import de.ruu.app.jeeeraaah.common.api.domain.exception.*;
import de.ruu.app.jeeeraaah.common.api.domain.flat.TaskGroupFlat;
import lombok.NonNull;

import java.util.Optional;
import java.util.Set;

/**
 * Generic, technology (JPA, JSONB, JAXB, MapStruct, ...) agnostic service interface for task groups.
 * Uses specific domain exceptions for better error handling and API clarity.
 *
 * @param <TG> TaskGroup implementation type
 * @param <T>  Task implementation type belonging to the TaskGroup
 */
public interface TaskGroupService<TG extends TaskGroup<T>, T extends Task<?, ?>>
{
	/**
	 * Creates a new task group in the backend.
	 *
	 * @param taskGroup the task group to create
	 * @return the created task group
	 * @throws EntityValidationException if the task group data is invalid
	 * @throws EntityAlreadyExistsException if a task group with the same ID already exists
	 * @throws PersistenceException if the persistence operation fails
	 */
	@NonNull TG create(@NonNull TG taskGroup);

	/**
	 * Reads the task group with the given id from the backend.
	 *
	 * @param id the id of the task group to read
	 * @return the task group with the given id, or empty if not found
	 * @throws PersistenceException if the persistence operation fails
	 */
	Optional<? extends TG> read(@NonNull Long id);

	/**
	 * Updates the task group if it is persistent (has an id) and can be found in the backend.
	 * If it is persistent but cannot be found, it throws an EntityNotFoundException.
	 * If it is not persistent, it creates a new task group in the backend.
	 *
	 * @param taskGroup the task group to update or create
	 * @return the updated or newly created task group
	 * @throws EntityNotFoundException if the task group with the given id does not exist (for updates)
	 * @throws EntityValidationException if the task group data is invalid
	 * @throws PersistenceException if the persistence operation fails
	 */
	@NonNull TG update(@NonNull TG taskGroup);

	/**
	 * Deletes the task group with the given id from the backend.
	 *
	 * @param id the id of the task group to delete
	 * @throws EntityNotFoundException if the task group with the given id does not exist
	 * @throws PersistenceException if the persistence operation fails
	 */
	void delete(@NonNull Long id);

	/**
	 * Finds all task groups in the backend. Task groups are flat which means they do not contain
	 * information about related tasks.
	 *
	 * @return a set of all task groups in the backend
	 * @throws PersistenceException if the persistence operation fails
	 */
	@NonNull Set<TaskGroupFlat> findAllFlat();

	/**
	 * Finds the task group with the given id and returns it with all its tasks.
	 *
	 * @param id the id of the task group to find
	 * @return the task group with the given id and all its tasks, or empty if not found
	 * @throws PersistenceException if the persistence operation fails
	 */
	Optional<? extends TG> findWithTasks(@NonNull Long id);

	/**
	 * Finds the task group with the given id and returns it with all its tasks and their neighbours.
	 *
	 * @param id the id of the task group to find
	 * @return the task group with the given id and all its tasks and their neighbours, or empty if not found
	 * @throws PersistenceException if the persistence operation fails
	 */
	Optional<? extends TG> findWithTasksAndDirectNeighbours(@NonNull Long id);

	/**
	 * Removes a task from a task group.
	 *
	 * @param idGroup the id of the task group
	 * @param idTask the id of the task to remove
	 * @throws EntityNotFoundException if the task group or task does not exist
	 * @throws PersistenceException if the persistence operation fails
	 */
	void removeTaskFromGroup(@NonNull Long idGroup, @NonNull Long idTask);
}

