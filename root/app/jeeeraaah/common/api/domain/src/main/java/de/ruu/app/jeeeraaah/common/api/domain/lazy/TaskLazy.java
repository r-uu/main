package de.ruu.app.jeeeraaah.common.api.domain.lazy;

import de.ruu.app.jeeeraaah.common.api.domain.flat.TaskFlat;
import jakarta.annotation.Nullable;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public interface TaskLazy extends TaskFlat
{
	@Override @NonNull TaskLazy name       (@NonNull String     name);
	@Override @NonNull TaskLazy description(@Nullable String    description);
	@Override @NonNull TaskLazy start      (@Nullable LocalDate startEstimated);
	@Override @NonNull TaskLazy end        (@Nullable LocalDate finishEstimated);
	@Override @NonNull TaskLazy closed     (@NonNull  Boolean   closed);

	@NonNull  Long taskGroupId();
	// superTaskId() is inherited from TaskFlat as Optional<Long>

	@NonNull Set<Long> subTaskIds     = new HashSet<>();
	@NonNull Set<Long> predecessorIds = new HashSet<>();
	@NonNull Set<Long> successorIds   = new HashSet<>();
}