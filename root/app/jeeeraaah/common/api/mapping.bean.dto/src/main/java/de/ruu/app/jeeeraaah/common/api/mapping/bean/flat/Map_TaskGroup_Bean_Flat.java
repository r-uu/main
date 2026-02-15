package de.ruu.app.jeeeraaah.common.api.mapping.bean.flat;

import de.ruu.app.jeeeraaah.common.api.bean.TaskGroupBean;
import de.ruu.app.jeeeraaah.common.api.domain.TaskGroupFlat;
import de.ruu.app.jeeeraaah.common.api.domain.TaskGroupFlat.TaskGroupFlatSimple;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskGroupDTOFlat;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import org.mapstruct.factory.Mappers;

/** {@link TaskGroupBean} -> {@link TaskGroupFlat} */
@Mapper
public interface Map_TaskGroup_Bean_Flat
{
	Map_TaskGroup_Bean_Flat INSTANCE = Mappers.getMapper(Map_TaskGroup_Bean_Flat.class);

	@NonNull TaskGroupFlat map(@NonNull TaskGroupBean in);

	/** mapstruct object factory - create concrete implementation */
	@ObjectFactory
	default @NonNull TaskGroupFlat create(@NonNull TaskGroupBean in)
	{
		// Use TaskGroupFlatSimple constructor that accepts TaskGroupEntity
		return new TaskGroupFlatSimple(in);
	}
}

