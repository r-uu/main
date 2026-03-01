package de.ruu.app.jeeeraaah.backend.api.ws.rs.service;

import de.ruu.app.jeeeraaah.backend.common.mapping.Mappings;
import de.ruu.app.jeeeraaah.backend.persistence.jpa.TaskGroupDTOService;
import de.ruu.app.jeeeraaah.backend.persistence.jpa.entity.TaskGroupJPA;
import de.ruu.app.jeeeraaah.backend.persistence.jpa.entity.TaskJPA;
import de.ruu.app.jeeeraaah.common.api.domain.TaskGroupEntityService;
import de.ruu.app.jeeeraaah.common.api.domain.exception.EntityNotFoundException;
import de.ruu.app.jeeeraaah.common.api.domain.flat.TaskGroupFlat;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskGroupDTO;
import de.ruu.lib.mapstruct.ReferenceCycleTracking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.Set;

/**
 * CDI bean implementing DTO-based task group service.
 * <p>
 * This service provides a DTO-only API to the REST layer, hiding all JPA entity
 * types and handling mapping internally. This ensures proper JPMS encapsulation
 * where JPA entities are not exported from this module.
 * <p>
 * Delegates to injected JPA service and adds DTO mapping layer.
 */
@ApplicationScoped
@Slf4j
public class TaskGroupDTOServiceImpl implements TaskGroupDTOService
{
	@Inject private TaskGroupEntityService<TaskGroupJPA, TaskJPA> entityService;

	@Override
	public @NonNull TaskGroupDTO create(@NonNull TaskGroupDTO dto)
	{
		ReferenceCycleTracking context = new ReferenceCycleTracking();
		TaskGroupJPA entity = entityService.create(Mappings.toJPA(dto, context));
		return Mappings.toDTO(entity, context);
	}

	@Override
	public @NonNull Optional<TaskGroupDTO> read(@NonNull Long id)
	{
		return entityService.read(id)
				.map(entity -> Mappings.toDTO(entity, new ReferenceCycleTracking()));
	}

	@Override
	public @NonNull TaskGroupDTO update(@NonNull TaskGroupDTO dto)
	{
		ReferenceCycleTracking context = new ReferenceCycleTracking();
		TaskGroupJPA entity = entityService.update(Mappings.toJPA(dto, context));
		return Mappings.toDTO(entity, context);
	}

	@Override
	public void delete(@NonNull Long id) throws EntityNotFoundException
	{
		entityService.delete(id);
	}

	@Override
	public @NonNull Set<TaskGroupFlat> findAllFlat()
	{
		return entityService.findAllFlat();
	}

	@Override
	public @NonNull Optional<TaskGroupDTO> findWithTasks(@NonNull Long id)
	{
		return entityService.findWithTasks(id)
				.map(entity -> Mappings.toDTO(entity, new ReferenceCycleTracking()));
	}

	@Override
	public @NonNull Optional<TaskGroupDTO> findWithTasksAndDirectNeighbours(@NonNull Long id)
	{
		return entityService.findWithTasksAndDirectNeighbours(id)
				.map(entity -> Mappings.toDTO(entity, new ReferenceCycleTracking()));
	}
}
