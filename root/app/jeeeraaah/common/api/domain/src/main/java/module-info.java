/**
 * Common API domain module containing core domain model interfaces and base classes.
 * <p>
 * This module provides the foundation for the Jeeeraaah task management system by defining:
 * <ul>
 *   <li>Core domain entities and their contracts ({@link de.ruu.app.jeeeraaah.common.api.domain.TaskEntity},
 *       {@link de.ruu.app.jeeeraaah.common.api.domain.TaskGroupEntity})</li>
 *   <li>Lazy-loading variants for performance optimization</li>
 *   <li>Flat representations for simplified data transfer</li>
 *   <li>Inter-task relationship configurations</li>
 * </ul>
 * <p>
 * The module is designed to be transitively required by both frontend and backend layers,
 * ensuring a consistent domain model across all application tiers.
 *
 * @since 0.0.1
 */
module de.ruu.app.jeeeraaah.common.api.domain
{
	exports de.ruu.app.jeeeraaah.common.api.domain;

	requires transitive de.ruu.lib.jpa.core;
	requires static transitive lombok;
	requires transitive jakarta.annotation;
	requires transitive java.desktop;

	requires de.ruu.lib.mapstruct;
	requires static com.fasterxml.jackson.annotation;
	requires static com.fasterxml.jackson.databind;
	requires de.ruu.lib.util;

	// Open for reflection-based frameworks (minimal, targeted access):
	// - Lombok: @AllArgsConstructor, @NoArgsConstructor, @Getter processing
	// - Jackson: JSON serialization/deserialization via @JsonProperty
	opens de.ruu.app.jeeeraaah.common.api.domain to lombok, com.fasterxml.jackson.databind;
}
