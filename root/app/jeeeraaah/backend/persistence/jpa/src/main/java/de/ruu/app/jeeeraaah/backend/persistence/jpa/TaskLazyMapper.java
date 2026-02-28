package de.ruu.app.jeeeraaah.backend.persistence.jpa;

import de.ruu.app.jeeeraaah.backend.persistence.jpa.entity.TaskGroupJPA;
import de.ruu.app.jeeeraaah.backend.persistence.jpa.entity.TaskJPA;
import de.ruu.app.jeeeraaah.common.api.domain.lazy.TaskLazy;
import lombok.NonNull;

/**
 * Functional interface for mapping TaskLazy to TaskJPA.
 * Allows injection of mapper implementation without circular dependency.
 */
@FunctionalInterface
public interface TaskLazyMapper
{
	/**
	 * Maps a TaskLazy (lazy DTO) to TaskJPA entity within the context of a task group.
	 *
	 * @param taskGroup the task group to which the task belongs
	 * @param taskLazy  the lazy task data to map
	 * @return the mapped TaskJPA entity
	 */
	@NonNull TaskJPA map(@NonNull TaskGroupJPA taskGroup, @NonNull TaskLazy taskLazy);
}
