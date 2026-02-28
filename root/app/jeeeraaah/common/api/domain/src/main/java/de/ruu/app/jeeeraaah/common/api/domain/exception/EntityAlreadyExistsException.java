package de.ruu.app.jeeeraaah.common.api.domain.exception;

import lombok.Getter;

/**
 * Thrown when trying to create an entity that already exists.
 */
@Getter
public class EntityAlreadyExistsException extends DomainException {
	private static final long serialVersionUID = 1L;

	private final Class<?> entityType;
	private final Object entityId;

	public EntityAlreadyExistsException(Class<?> entityType, Object entityId) {
		super(String.format("Entity of type %s with ID %s already exists",
			entityType.getSimpleName(), entityId));
		this.entityType = entityType;
		this.entityId = entityId;
	}

	public EntityAlreadyExistsException(Class<?> entityType, Object entityId, Throwable cause) {
		super(String.format("Entity of type %s with ID %s already exists",
			entityType.getSimpleName(), entityId), cause);
		this.entityType = entityType;
		this.entityId = entityId;
	}
}

