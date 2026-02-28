package de.ruu.app.jeeeraaah.common.api.domain.exception;
import lombok.Getter;
/**
 * Thrown when trying to establish an invalid relationship between entities.
 * For example, adding a task to a non-existent task group.
 */
@Getter
public class InvalidRelationshipException extends DomainException {
private static final long serialVersionUID = 1L;
private final String relationshipType;
private final Object sourceId;
private final Object targetId;
public InvalidRelationshipException(String relationshipType, Object sourceId, Object targetId) {
super(String.format("Invalid %s relationship between ID %s and ID %s", 
relationshipType, sourceId, targetId));
this.relationshipType = relationshipType;
this.sourceId = sourceId;
this.targetId = targetId;
}
public InvalidRelationshipException(String message, String relationshipType, Object sourceId, Object targetId) {
super(message);
this.relationshipType = relationshipType;
this.sourceId = sourceId;
this.targetId = targetId;
}
}
