package de.ruu.app.jeeeraaah.common.api.domain.exception;

import lombok.Getter;

/**
 * Base exception for all domain-related errors.
 * Provides a common ancestor for all business logic exceptions.
 */
@Getter
public class DomainException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DomainException(String message) {
		super(message);
	}

	public DomainException(String message, Throwable cause) {
		super(message, cause);
	}

	public DomainException(Throwable cause) {
		super(cause);
	}
}

