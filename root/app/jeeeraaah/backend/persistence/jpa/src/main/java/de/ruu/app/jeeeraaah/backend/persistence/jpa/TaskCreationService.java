package de.ruu.app.jeeeraaah.backend.persistence.jpa;

import de.ruu.app.jeeeraaah.backend.persistence.jpa.entity.TaskJPA;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskCreationData;
import lombok.NonNull;

/**
 * Service capability for creating tasks from REST API data.
 * This interface allows creating tasks with proper transaction handling
 * and lazy-loading support within the persistence layer.
 */
public interface TaskCreationService
{
	/**
	 * Creates a new task from TaskCreationData within a transaction to avoid lazy
	 * initialization issues. This method performs the mapping inside the transaction
	 * boundary where the TaskGroupJPA is managed by the persistence context,
	 * allowing access to lazy-loaded collections.
	 *
	 * @param data task creation data containing task group ID and task lazy data
	 * @return the persisted task entity
	 * @throws de.ruu.app.jeeeraaah.common.api.domain.exception.EntityNotFoundException
	 *         if the task group with the given ID does not exist
	 */
	@NonNull TaskJPA createFromData(@NonNull TaskCreationData data);
}
