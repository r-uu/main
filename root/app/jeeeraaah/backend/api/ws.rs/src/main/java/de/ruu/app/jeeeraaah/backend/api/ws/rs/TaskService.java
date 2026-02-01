package de.ruu.app.jeeeraaah.backend.api.ws.rs;

import static de.ruu.app.jeeeraaah.backend.common.mapping.Mappings.toDTO;
import static de.ruu.app.jeeeraaah.backend.common.mapping.Mappings.toJPA;
import static de.ruu.app.jeeeraaah.common.api.domain.PathsCommon.TOKEN_BY_ID;
import static de.ruu.app.jeeeraaah.common.api.domain.PathsTask.TOKEN_ADD_PREDECESSOR;
import static de.ruu.app.jeeeraaah.common.api.domain.PathsTask.TOKEN_ADD_SUB;
import static de.ruu.app.jeeeraaah.common.api.domain.PathsTask.TOKEN_ADD_SUCCESSOR;
import static de.ruu.app.jeeeraaah.common.api.domain.PathsTask.TOKEN_ALL;
import static de.ruu.app.jeeeraaah.common.api.domain.PathsTask.TOKEN_BY_ID_WITH_RELATED;
import static de.ruu.app.jeeeraaah.common.api.domain.PathsTask.TOKEN_DOMAIN;
import static de.ruu.app.jeeeraaah.common.api.domain.PathsTask.TOKEN_REMOVE_NEIGHBOURS_FROM_TASK;
import static de.ruu.app.jeeeraaah.common.api.domain.PathsTask.TOKEN_REMOVE_PREDECESSOR;
import static de.ruu.app.jeeeraaah.common.api.domain.PathsTask.TOKEN_REMOVE_SUB;
import static de.ruu.app.jeeeraaah.common.api.domain.PathsTask.TOKEN_REMOVE_SUCCESSOR;
import static de.ruu.lib.util.BooleanFunctions.not;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.CONFLICT;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

import de.ruu.app.jeeeraaah.backend.persistence.jpa.TaskGroupServiceJPA;
import de.ruu.app.jeeeraaah.backend.persistence.jpa.TaskJPA;
import de.ruu.app.jeeeraaah.backend.persistence.jpa.TaskServiceJPA;
import de.ruu.app.jeeeraaah.common.api.domain.InterTaskRelationData;
import de.ruu.app.jeeeraaah.common.api.domain.RemoveNeighboursFromTaskConfig;
import de.ruu.app.jeeeraaah.common.api.domain.TaskRelationException;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskCreationData;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskDTO;
import de.ruu.lib.mapstruct.ReferenceCycleTracking;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller providing REST endpoints for task operations.
 * <p>
 * Methods accept DTO parameters, transform DTOs to entities, delegate to {@link #taskService} and transform entity
 * return values from {@link #taskService} back to DTOs. The transformations from entities to DTOs are intentionally
 * done here after transactions were committed in {@link #taskService}. This ensures that version attributes of DTOs are
 * respected with their new values after commit in returned DTOs.
 *
 * @author r-uu
 */
// TODO fix exception handling
@ApplicationScoped
@Path(TOKEN_DOMAIN)
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@OpenAPIDefinition(info = @Info(version = "a version", title = "a title"))
@Timed
@Slf4j
public class TaskService
{
	@Inject private TaskGroupServiceJPA taskGroupService;
	@Inject private TaskServiceJPA      taskService;

	@POST
	@RolesAllowed("task-create")
	public Response create(@Valid TaskCreationData data)
	{
		log.debug("Received TaskCreationData:");
		log.debug("  data: {}", data);
		log.debug("  taskGroupId: {}", data != null ? data.getTaskGroupId() : "data is null");
		log.debug("  task: {}", data != null ? data.getTask() : "data is null");

		// delegate to service which handles mapping and creation within transaction to
		// avoid lazy initialization issues
		TaskJPA taskJPAPersistent = taskService.createFromData(data);
		TaskDTO result = toDTO(taskJPAPersistent, new ReferenceCycleTracking());
		return Response.status(CREATED).entity(result).build();
	}

	@GET
	@Path(TOKEN_BY_ID)
	@Produces(APPLICATION_JSON)
	@RolesAllowed("task-read")
	public Response read(@PathParam("id") Long id)
	{
		Optional<? extends TaskJPA> optional = taskService.read(id);
		if (not(optional.isPresent())) return Response.status(NOT_FOUND).entity("task with id " + id + " not found").build();
		else
		{
			ReferenceCycleTracking context = new ReferenceCycleTracking();

			TaskDTO result = toDTO(optional.get(), context);
			log.debug("found task for id {}\n{}", id, result);
			return Response.ok(result).build();
		}
	}

	@PUT
	@RolesAllowed("task-update")
	public Response update(@NonNull TaskDTO dto)
	{
		ReferenceCycleTracking context = new ReferenceCycleTracking();

		TaskJPA entity = taskService.update(toJPA(dto, context));
		TaskDTO result = toDTO(entity, context);
		return Response.ok(result).build();
	}

	@DELETE
	@Path(TOKEN_BY_ID)
	@RolesAllowed("task-delete")
	public Response delete(@PathParam("id") Long id)
	{
		try { taskService.delete(id); }
		catch (Exception e) { return Response.status(CONFLICT).build(); }
		return Response.ok().build();
	}

	@GET
	@Path(TOKEN_ALL)
	@Produces(APPLICATION_JSON)
	@RolesAllowed("task-read")
	public Response findAll()
	{
		Set<TaskDTO> result = new HashSet<>();

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		for (TaskJPA taskJPA : taskService.findAll())
		{
			TaskDTO dto = toDTO(taskJPA, context);
			result.add(dto);
			log.debug("found task {}", dto);
		}

		return Response.ok(result).build();
	}

	@GET
	@Path(TOKEN_BY_ID_WITH_RELATED + TOKEN_BY_ID)
	@Produces(APPLICATION_JSON)
	@RolesAllowed("task-read")
	public Response findByIdWithRelated(@PathParam("id") Long id)
	{
		Optional<? extends TaskJPA> optional = taskService.findWithRelated(id);
		if (not(optional.isPresent()))
				return Response.status(NOT_FOUND).entity("task with id " + id + " not found").build();
		else
		{
			TaskDTO result = toDTO(optional.get(), new ReferenceCycleTracking());
			return Response.ok(result).build();
		}
	}

	// @POST // use POST for bulk operations
	// @Path(PATH_BY_IDS_LAZY)
	// @Produces(APPLICATION_JSON)
	// public Response findByIdsWithRelatedLazy(Set<Long> ids)
	// {
	// Set<TaskLazy> result = new HashSet<>();
	// taskService.findGroupTasksWithRelated(ids).forEach(t -> result.add(toLazy(t,
	// new ReferenceCycleTracking())));
	// return Response.ok(result).build();
	// }

	@PUT
	@Path(TOKEN_ADD_SUB)
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	@RolesAllowed("task-update")
	public Response addSubTask(@NonNull InterTaskRelationData data)
	{
		log.debug("id task: {}, id sub task: {}", data.id(), data.idRelated());
		taskService.addSubTask(data.id(), data.idRelated());
		return Response.ok().build();
	}

	@PUT
	@Path(TOKEN_ADD_PREDECESSOR)
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	@RolesAllowed("task-update")
	public Response addPredecessor(@NonNull InterTaskRelationData data)
	{
		log.debug("id task: {}, id predecessor task: {}", data.id(), data.idRelated());
		try { taskService.addPredecessor(data.id(), data.idRelated()); }
		catch (Throwable t)
		{
			log.debug("throwable caught: {}", t.getMessage());
			if   (t instanceof TaskRelationException) log.debug("it is a TaskRelationException");
			else log.debug("it is not a TaskRelationException");
			throw t;
		}
		// try { taskService.addPredecessor(data.id(), data.idRelated()); }
		// catch (NotFoundException e)
		// { return status(NOT_FOUND).entity(e.getMessage()).build(); }
		//// catch (TaskRelationException e) // do not catch here, let it be mapped by
		/// TaskRelationExceptionMapper { return
		/// status(BAD_REQUEST).entity(e.getMessage()).build(); }
		return Response.ok().build();
	}

	@PUT
	@Path(TOKEN_ADD_SUCCESSOR)
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	@RolesAllowed("task-update")
	public Response addSuccessor(@NonNull InterTaskRelationData data)
	{
		log.debug("id task: {}, id successor task: {}", data.id(), data.idRelated());
		taskService.addSuccessor(data.id(), data.idRelated());
		// try { taskService.addSuccessor(data.id(), data.idRelated()); }
		// catch (NotFoundException e) { return
		// status(NOT_FOUND).entity(e.getMessage()).build(); }
		//// catch (TaskRelationException e) // do not catch here, let it be mapped by
		/// TaskRelationExceptionMapper { return
		/// status(BAD_REQUEST).entity(e.getMessage()).build(); }
		return Response.ok().build();
	}

	@PUT
	@Path(TOKEN_REMOVE_SUB)
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	@RolesAllowed("task-update")
	// public Response removeSubTask(@PathParam("idTask") Long idTask,
	// @PathParam("idSubTask") Long idSubTask)
	public Response removeSubTask(@NonNull InterTaskRelationData data)
	{
		log.debug("id task: {}, id sub task: {}", data.id(), data.idRelated());
		try { taskService.removeSubTask(data.id(), data.idRelated()); }
		catch (NotFoundException e) { return Response.status(NOT_FOUND).entity(e.getMessage()).build(); }
		return Response.ok().build();
	}

	@PUT
	@Path(TOKEN_REMOVE_PREDECESSOR)
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	@RolesAllowed("task-update")
	// public Response removePredecessor(@PathParam("idTask") Long idTask,
	// @PathParam("idPredecessor") Long idPredecessor)
	public Response removePredecessor(@NonNull InterTaskRelationData data)
	{
		log.debug("id task: {}, id predecessor task: {}", data.id(), data.idRelated());
		try { taskService.removePredecessor(data.id(), data.idRelated()); }
		catch (NotFoundException e) { return Response.status(NOT_FOUND).entity(e.getMessage()).build(); }
		return Response.ok().build();
	}

	@PUT
	@Path(TOKEN_REMOVE_SUCCESSOR)
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	@RolesAllowed("task-update")
	public Response removeSuccessor(@NonNull InterTaskRelationData data)
	{
		log.debug("id task: {}, id successor task: {}", data.id(), data.idRelated());
		try { taskService.removeSuccessor(data.id(), data.idRelated()); }
		catch (NotFoundException e) { return Response.status(NOT_FOUND).entity(e.getMessage()).build(); }
		return Response.ok().build();
	}

	@PUT
	@Path(TOKEN_REMOVE_NEIGHBOURS_FROM_TASK)
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	@RolesAllowed("task-update")
	public Response removeNeighboursFromTask(@NonNull RemoveNeighboursFromTaskConfig data)
	{
		log.debug("removing from neighbours from task with id: {}", data.idTask());
		try { taskService.removeNeighboursFromTask(data); }
		catch (NotFoundException e) { return Response.status(NOT_FOUND).entity(e.getMessage()).build(); }
		return Response.ok().build();
	}
}
