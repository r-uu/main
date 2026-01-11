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

	// Open for Lombok's annotation processing (still used by other classes)
	// Open for Jackson serialization/deserialization
	opens de.ruu.app.jeeeraaah.common.api.domain to lombok, com.fasterxml.jackson.databind;
}
