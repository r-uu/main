/**
 * Common API REST module containing Data Transfer Objects (DTOs) for REST communication.
 * <p>
 * This module defines DTOs used for REST API communication between frontend and backend:
 * <ul>
 *   <li>TaskDTO, TaskGroupDTO - primary data transfer objects</li>
 *   <li>TaskCreationData - specialized DTOs for create operations</li>
 *   <li>JSON serialization/deserialization support via Jackson</li>
 * </ul>
 * <p>
 * These DTOs are designed to be serializable and optimized for network transfer.
 *
 * @since 0.0.1
 */
module de.ruu.app.jeeeraaah.common.api.ws.rs
{
    exports de.ruu.app.jeeeraaah.common.api.ws.rs;

    requires transitive de.ruu.app.jeeeraaah.common.api.domain;
    requires de.ruu.lib.util;
    requires com.fasterxml.jackson.annotation;
    requires jakarta.annotation;

    requires static lombok;

    // Open for reflection-based frameworks (minimal, targeted access):
    // - Jackson: JSON serialization/deserialization
    // - Lombok: @Data, @AllArgsConstructor processing
    opens de.ruu.app.jeeeraaah.common.api.ws.rs to com.fasterxml.jackson.databind, lombok;
}