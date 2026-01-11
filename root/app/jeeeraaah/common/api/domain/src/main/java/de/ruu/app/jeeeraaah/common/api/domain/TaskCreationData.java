package de.ruu.app.jeeeraaah.common.api.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

@AllArgsConstructor(onConstructor_ = @JsonCreator)
@NoArgsConstructor(force = true)
@Getter
@Accessors(fluent = true)
@ToString
public class TaskCreationData
{
	@JsonProperty("taskGroupId")
	@NonNull private final Long taskGroupId;
	
	@JsonProperty("task")
	@NonNull private final TaskLazy task;
}