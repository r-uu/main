package de.ruu.app.jeeeraaah.common.api.ws.rs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * Data Transfer Object für die Erstellung einer neuen Task.
 * Verwendet Lombok @NonNull für Null-Checks und normale Getter.
 * Jackson deserialisiert über @JsonCreator Constructor.
 */
@Getter
@ToString
public class TaskCreationData
{
	@NonNull
	private final Long taskGroupId;

	@NonNull
	private final TaskDTOLazy task;

	@JsonCreator
	public TaskCreationData(
			@JsonProperty(value = "taskGroupId", required = true) @NonNull Long taskGroupId,
			@JsonProperty(value = "task", required = true) @NonNull TaskDTOLazy task)
	{
		this.taskGroupId = taskGroupId;
		this.task = task;
	}
}