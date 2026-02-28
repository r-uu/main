package de.ruu.app.jeeeraaah.common.api.domain.exception;

import lombok.Getter;

/**
 * Thrown when entity validation fails.
 */
@Getter
public class EntityValidationException extends DomainException {
	private static final long serialVersionUID = 1L;

	private final Class<?> entityType;
	private final String validationMessage;

	public EntityValidationException(Class<?> entityType, String validationMessage) {
		super(String.format("Validation failed for %s: %s",
			entityType.getSimpleName(), validationMessage));
		this.entityType = entityType;
		this.validationMessage = validationMessage;
	}

	public EntityValidationException(Class<?> entityType, String validationMessage, Throwable cause) {
		super(String.format("Validation failed for %s: %s",
			entityType.getSimpleName(), validationMessage), cause);
		this.entityType = entityType;
		this.validationMessage = validationMessage;
	}
}

