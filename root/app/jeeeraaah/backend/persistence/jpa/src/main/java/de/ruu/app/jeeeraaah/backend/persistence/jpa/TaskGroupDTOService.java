package de.ruu.app.jeeeraaah.backend.persistence.jpa;

import de.ruu.app.jeeeraaah.common.api.domain.exception.EntityNotFoundException;
import de.ruu.app.jeeeraaah.common.api.domain.flat.TaskGroupFlat;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskGroupDTO;
import lombok.NonNull;

import java.util.Optional;
import java.util.Set;

/**
 * DTO-based service interface for task group operations.
 * This service works exclusively with DTOs, hiding JPA implementation details
 * from the REST layer and ensuring proper JPMS encapsulation.
 * <p>
 * All mapping between JPA entities and DTOs happens internally within the
 * persistence module's service implementation.
 */
public interface TaskGroupDTOService
{
	/**
	 * Creates a new task group.
	 *
	 * @param dto the task group DTO
	 * @return the created task group DTO
	 */
	@NonNull TaskGroupDTO create(@NonNull TaskGroupDTO dto);

	/**
	 * Reads a task group by ID.
	 *
	 * @param id the task group ID
	 * @return the task group DTO if found
	 */
	@NonNull Optional<TaskGroupDTO> read(@NonNull Long id);

	/**
	 * Updates an existing task group.
	 *
	 * @param dto the task group DTO with updated data
	 * @return the updated task group DTO
	 * @throws EntityNotFoundException if the task group does not exist
	 */
	@NonNull TaskGroupDTO update(@NonNull TaskGroupDTO dto);

	/**
	 * Deletes a task group by ID.
	 *
	 * @param id the task group ID
	 * @throws EntityNotFoundException if the task group does not exist
	 */
	void delete(@NonNull Long id);

	/**
	 * Finds a task group with all its tasks loaded.
	 *
	 * @param id the task group ID
	 * @return the task group DTO with tasks if found
	 */
	@NonNull Optional<TaskGroupDTO> findWithTasks(@NonNull Long id);

	/**
	 * Finds a task group with all its tasks and their direct neighbours.
	 *
	 * @param id the task group ID
	 * @return the task group DTO with tasks and neighbours if found
	 */
	@NonNull Optional<TaskGroupDTO> findWithTasksAndDirectNeighbours(@NonNull Long id);

	/**
	 * Finds all task groups in flat representation.
	 *
	 * @return set of flat task group representations
	 */
	@NonNull Set<TaskGroupFlat> findAllFlat();
}
