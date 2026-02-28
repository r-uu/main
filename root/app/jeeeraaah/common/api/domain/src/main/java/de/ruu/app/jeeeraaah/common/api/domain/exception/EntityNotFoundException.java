package de.ruu.app.jeeeraaah.common.api.domain.exception;

import lombok.Getter;

/**
 * Thrown when a requested entity is not found in the persistence layer.
 * Contains information about the entity type and ID for better debugging.
 */
@Getter
public class EntityNotFoundException extends DomainException {
	private static final long serialVersionUID = 1L;

	private final Class<?> entityType;
	private final Object entityId;

	public EntityNotFoundException(Class<?> entityType, Object entityId) {
		super(String.format("Entity of type %s with ID %s not found",
			entityType.getSimpleName(), entityId));
		this.entityType = entityType;
		this.entityId = entityId;
	}

	public EntityNotFoundException(Class<?> entityType, Object entityId, Throwable cause) {
		super(String.format("Entity of type %s with ID %s not found",
			entityType.getSimpleName(), entityId), cause);
		this.entityType = entityType;
		this.entityId = entityId;
	}
}

