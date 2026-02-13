/**
 * Frontend UI JavaFX Model module containing JavaFX-specific data model classes.
 * <p>
 * This module provides FXBean implementations with JavaFX observable properties for:
 * <ul>
 *   <li>TaskFXBean - observable task representation for UI binding</li>
 *   <li>TaskGroupFXBean - observable task group for UI binding</li>
 * </ul>
 * <p>
 * These beans enable automatic UI updates through JavaFX property change notifications,
 * essential for reactive user interfaces.
 *
 * @since 0.0.1
 */
module de.ruu.app.jeeeraaah.frontend.ui.fx.model
{
	exports de.ruu.app.jeeeraaah.frontend.ui.fx.model;

	requires javafx.base;
	requires jakarta.annotation;
	requires de.ruu.lib.jpa.core;
	requires de.ruu.lib.util;
	requires de.ruu.app.jeeeraaah.common.api.domain;

	requires static lombok;
}
