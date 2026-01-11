package de.ruu.app.jeeeraaah.common.api.domain;

import lombok.NonNull;

import java.util.Set;

/** Lazy representation of a {@link TaskGroupFlat} entity that additionally provides the ids of related tasks. */
public interface TaskGroupLazy extends TaskGroupFlat
{
	@NonNull Set<Long> taskIds();
}