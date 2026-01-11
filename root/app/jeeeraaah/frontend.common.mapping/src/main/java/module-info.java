module de.ruu.app.jeeeraaah.frontend.common.mapping
{
	exports de.ruu.app.jeeeraaah.frontend.common.mapping;
	exports de.ruu.app.jeeeraaah.frontend.common.mapping.bean.flatbean;
	exports de.ruu.app.jeeeraaah.frontend.common.mapping.bean.fxbean;
	exports de.ruu.app.jeeeraaah.frontend.common.mapping.fxbean.bean;

	// MapStruct context helper used in mappers
	requires de.ruu.lib.mapstruct;

	// Reads common API bean types used by mappers
	requires de.ruu.app.jeeeraaah.common.api.bean;
	requires de.ruu.app.jeeeraaah.frontend.ui.fx.model;

	// Optional lombok at compile time only
	requires static lombok;
	requires de.ruu.app.jeeeraaah.common.api.domain;
}
