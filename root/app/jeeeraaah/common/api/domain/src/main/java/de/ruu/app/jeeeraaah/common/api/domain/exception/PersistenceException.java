package de.ruu.app.jeeeraaah.common.api.domain.exception;
import lombok.Getter;
/**
 * Thrown when a persistence operation fails.
 */
@Getter
public class PersistenceException extends DomainException {
private static final long serialVersionUID = 1L;
private final String operation;
public PersistenceException(String operation, Throwable cause) {
super(String.format("Persistence operation '%s' failed", operation), cause);
this.operation = operation;
}
public PersistenceException(String message, String operation, Throwable cause) {
super(message, cause);
this.operation = operation;
}
}
