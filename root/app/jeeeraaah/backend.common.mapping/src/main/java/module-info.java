module de.ruu.app.jeeeraaah.backend.common.mapping
{
	exports de.ruu.app.jeeeraaah.backend.common.mapping;
	exports de.ruu.app.jeeeraaah.backend.common.mapping.dto.jpa;
	exports de.ruu.app.jeeeraaah.backend.common.mapping.jpa.dto to org.mapstruct;
	exports de.ruu.app.jeeeraaah.backend.common.mapping.lazy.jpa;
	exports de.ruu.app.jeeeraaah.backend.common.mapping.jpa.lazy;

	// Open packages for CDI bean discovery (OpenLiberty/Weld at runtime)
	// Required for @ApplicationScoped beans like TaskLazyMapperCDI
	// Weld modules are provided by the application server, not compile-time dependencies
	opens de.ruu.app.jeeeraaah.backend.common.mapping;
	opens de.ruu.app.jeeeraaah.backend.common.mapping.lazy.jpa;

	requires de.ruu.app.jeeeraaah.backend.persistence.jpa;
	requires de.ruu.app.jeeeraaah.common.api.ws.rs;
	requires de.ruu.lib.mapstruct;

	requires static lombok;
	requires static org.slf4j;
	requires de.ruu.lib.jpa.core;
	requires de.ruu.app.jeeeraaah.common.api.domain;
	requires jakarta.persistence;
	requires jakarta.cdi;
}