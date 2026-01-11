module de.ruu.app.jeeeraaah.common.api.mapping
{
	exports de.ruu.app.jeeeraaah.common.api.mapping;
	exports de.ruu.app.jeeeraaah.common.api.mapping.bean.dto;
	exports de.ruu.app.jeeeraaah.common.api.mapping.dto.bean;
	exports de.ruu.app.jeeeraaah.common.api.mapping.lazy.bean;
	exports de.ruu.app.jeeeraaah.common.api.mapping.bean.lazy;
	exports de.ruu.app.jeeeraaah.common.api.mapping.flat.bean;

	requires de.ruu.app.jeeeraaah.common.api.domain;
	requires de.ruu.app.jeeeraaah.common.api.bean;
	requires de.ruu.app.jeeeraaah.common.api.ws.rs;
	requires de.ruu.lib.jpa.core;
	requires de.ruu.lib.mapstruct;
	requires jakarta.annotation;
	requires static lombok;
	requires static java.compiler; // needed for MapStruct generated code
}