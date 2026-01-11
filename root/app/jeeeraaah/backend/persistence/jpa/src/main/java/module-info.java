module de.ruu.app.jeeeraaah.backend.persistence.jpa
{
	exports de.ruu.app.jeeeraaah.backend.persistence.jpa;

	requires jakarta.annotation;
	requires jakarta.cdi;
	requires jakarta.inject;
	requires jakarta.persistence;
	requires static jakarta.transaction;
	requires jakarta.ws.rs;

	requires de.ruu.lib.jpa.core;
	requires de.ruu.lib.mapstruct;
	requires de.ruu.lib.util;
	requires de.ruu.app.jeeeraaah.common.api.mapping;
	requires de.ruu.app.jeeeraaah.common.api.domain;
	requires de.ruu.app.jeeeraaah.common.api.ws.rs;

	requires org.slf4j;
	requires static lombok;

	// Open packages for JPA entity scanning (Hibernate)
	opens de.ruu.app.jeeeraaah.backend.persistence.jpa;

	// Open ee package for CDI bean discovery and proxy generation (OpenLiberty/Weld)
	// Opens to all unnamed modules for Weld SE compatibility
	opens de.ruu.app.jeeeraaah.backend.persistence.jpa.ee;
}