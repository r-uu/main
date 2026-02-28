package de.ruu.app.jeeeraaah.common.api.domain.exception;

import lombok.Getter;

/**
 * Thrown when a circular reference is detected in task relationships.
 * For example, when trying to add a task as its own subtask or creating a cycle.
 */
@Getter
public class CircularReferenceException extends DomainException {
	private static final long serialVersionUID = 1L;

	private final Object sourceId;
	private final Object targetId;

	public CircularReferenceException(Object sourceId, Object targetId) {
		super(String.format("Circular reference detected between ID %s and ID %s",
			sourceId, targetId));
		this.sourceId = sourceId;
		this.targetId = targetId;
	}

	public CircularReferenceException(String message, Object sourceId, Object targetId) {
		super(message);
		this.sourceId = sourceId;
		this.targetId = targetId;
	}
}