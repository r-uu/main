/**
 * Common API Bean module containing concrete Bean implementations.
 * <p>
 * This module provides mutable Bean implementations of the domain entities defined in
 * {@code common.api.domain}. These Beans are used for:
 * <ul>
 *   <li>Data transfer between layers (frontend ↔ backend)</li>
 *   <li>Business logic operations requiring mutable state</li>
 *   <li>Mapping between domain entities and DTOs</li>
 * </ul>
 * <p>
 * Key classes: {@code TaskBean}, {@code TaskGroupBean}
 *
 * @since 0.0.1
 */
module de.ruu.app.jeeeraaah.common.api.bean
{
	exports de.ruu.app.jeeeraaah.common.api.bean;

	requires de.ruu.app.jeeeraaah.common.api.domain;
	requires de.ruu.lib.jpa.core;
	requires de.ruu.lib.mapstruct;
	requires de.ruu.lib.util;

	requires jakarta.annotation;

	requires static lombok;
	requires org.slf4j;
}
