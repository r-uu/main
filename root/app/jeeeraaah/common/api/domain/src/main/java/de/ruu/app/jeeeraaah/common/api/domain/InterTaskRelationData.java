package de.ruu.app.jeeeraaah.common.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@AllArgsConstructor @NoArgsConstructor @Getter @Accessors(fluent = true) @ToString
public class InterTaskRelationData
{
	@JsonProperty(required = true)
	private Long id;
	@JsonProperty(required = true)
	private Long idRelated;
}