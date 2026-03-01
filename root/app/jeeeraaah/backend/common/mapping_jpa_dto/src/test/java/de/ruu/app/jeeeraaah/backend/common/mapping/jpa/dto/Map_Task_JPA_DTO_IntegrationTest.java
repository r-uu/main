package de.ruu.app.jeeeraaah.backend.common.mapping.jpa.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.ruu.app.jeeeraaah.backend.persistence.jpa.entity.TaskGroupJPA;
import de.ruu.app.jeeeraaah.backend.persistence.jpa.entity.TaskJPA;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskDTO;
import de.ruu.lib.junit.DisabledOnServerNotListening;
import de.ruu.lib.mapstruct.ReferenceCycleTracking;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceUtil;

/**
 * Integration tests for Task mapping with JPA EntityManager. These tests verify that relational mappings work correctly
 * with lazy loading. Tests are disabled if PostgreSQL is not available.
 */
@DisabledOnServerNotListening(propertyNameHost = "database.host", propertyNamePort = "database.port")
public class Map_Task_JPA_DTO_IntegrationTest extends AbstractJPATest
{
	@Test
	void afterMapping_mapsSuperTask_whenLoadedAndPresent()
	{
		// arrange: persist entities first
		TaskGroupJPA group = persistAndFlush(new TaskGroupJPA("test group"));
		TaskJPA superTask = new TaskJPA(group, "super task");
		TaskJPA subTask = new TaskJPA(group, "sub task");
		subTask.superTask(superTask);
		persistAndFlush(superTask);
		persistAndFlush(subTask);

		// Clear persistence context to ensure lazy loading
		clearPersistenceContext();

		// Reload subTask with superTask eagerly loaded
		TaskJPA reloadedSubTask = find(TaskJPA.class, subTask.id());
		// Force loading of superTask
		reloadedSubTask.superTask().ifPresent(st -> st.name());

		// Verify superTask is loaded
		PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();
		assertThat(persistenceUtil.isLoaded(reloadedSubTask, "superTask")).as("superTask should be loaded").isTrue();

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// act
		TaskDTO dto = Map_Task_JPA_DTO.INSTANCE.map(reloadedSubTask, context);

		// assert: superTask should be mapped
		assertThat(dto.superTask().isPresent()).isTrue();
		TaskDTO mappedSuperTask = dto.superTask().get();
		assertThat(mappedSuperTask).isNotNull();
		assertThat(mappedSuperTask.name()).isEqualTo("super task");

		// assert: superTask should be in context
		TaskDTO superFromContext = context.get(superTask, TaskDTO.class);
		assertThat(superFromContext).isNotNull();
		assertThat(superFromContext).isEqualTo(mappedSuperTask);
	}

	@Test
	void afterMapping_mapsSubTasks_whenLoadedAndPresent()
	{
		// arrange: persist entities
		TaskGroupJPA group = persistAndFlush(new TaskGroupJPA("test group"));
		TaskJPA superTask = new TaskJPA(group, "super task");
		TaskJPA subTask1 = new TaskJPA(group, "sub task 1");
		TaskJPA subTask2 = new TaskJPA(group, "sub task 2");
		superTask.addSubTask(subTask1);
		superTask.addSubTask(subTask2);
		persistAndFlush(superTask);
		persistAndFlush(subTask1);
		persistAndFlush(subTask2);

		// Clear and reload with subTasks loaded
		clearPersistenceContext();
		TaskJPA reloadedSuperTask = find(TaskJPA.class, superTask.id());
		// Force loading of subTasks
		reloadedSuperTask.subTasks().ifPresent(st -> st.size());

		// Verify subTasks are loaded
		PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();
		assertThat(persistenceUtil.isLoaded(reloadedSuperTask, "subTasks")).as("subTasks should be loaded").isTrue();

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// act
		TaskDTO dto = Map_Task_JPA_DTO.INSTANCE.map(reloadedSuperTask, context);

		// assert: subTasks should be mapped
		assertThat(dto.subTasks().isPresent()).isTrue();
		assertThat(dto.subTasks().get().size()).isEqualTo(2);

		// assert: subTasks should be in context
		TaskDTO sub1FromContext = context.get(subTask1, TaskDTO.class);
		TaskDTO sub2FromContext = context.get(subTask2, TaskDTO.class);
		assertThat(sub1FromContext).isNotNull();
		assertThat(sub2FromContext).isNotNull();
	}

	@Test
	void afterMapping_mapsPredecessors_whenLoadedAndPresent()
	{
		// arrange: persist entities
		TaskGroupJPA group = persistAndFlush(new TaskGroupJPA("test group"));
		TaskJPA predecessor1 = new TaskJPA(group, "predecessor 1");
		TaskJPA predecessor2 = new TaskJPA(group, "predecessor 2");
		TaskJPA task = new TaskJPA(group, "task");
		task.addPredecessor(predecessor1);
		task.addPredecessor(predecessor2);
		persistAndFlush(predecessor1);
		persistAndFlush(predecessor2);
		persistAndFlush(task);

		// Clear and reload with predecessors loaded
		clearPersistenceContext();
		TaskJPA reloadedTask = find(TaskJPA.class, task.id());
		// Force loading of predecessors
		reloadedTask.predecessors().ifPresent(p -> p.size());

		// Verify predecessors are loaded
		PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();
		assertThat(persistenceUtil.isLoaded(reloadedTask, "predecessors")).as("predecessors should be loaded").isTrue();

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// act
		TaskDTO dto = Map_Task_JPA_DTO.INSTANCE.map(reloadedTask, context);

		// assert: predecessors should be mapped
		assertThat(dto.predecessors().isPresent()).isTrue();
		assertThat(dto.predecessors().get().size()).isEqualTo(2);

		// assert: predecessors should be in context
		TaskDTO pred1FromContext = context.get(predecessor1, TaskDTO.class);
		TaskDTO pred2FromContext = context.get(predecessor2, TaskDTO.class);
		assertThat(pred1FromContext).isNotNull();
		assertThat(pred2FromContext).isNotNull();
	}

	@Test
	void afterMapping_mapsSuccessors_whenLoadedAndPresent()
	{
		// arrange: persist entities
		TaskGroupJPA group = persistAndFlush(new TaskGroupJPA("test group"));
		TaskJPA successor1 = new TaskJPA(group, "successor 1");
		TaskJPA successor2 = new TaskJPA(group, "successor 2");
		TaskJPA task = new TaskJPA(group, "task");
		task.addSuccessor(successor1);
		task.addSuccessor(successor2);
		persistAndFlush(successor1);
		persistAndFlush(successor2);
		persistAndFlush(task);

		// Clear and reload with successors loaded
		clearPersistenceContext();
		TaskJPA reloadedTask = find(TaskJPA.class, task.id());
		// Force loading of successors
		reloadedTask.successors().ifPresent(s -> s.size());

		// Verify successors are loaded
		PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();
		assertThat(persistenceUtil.isLoaded(reloadedTask, "successors")).as("successors should be loaded").isTrue();

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// act
		TaskDTO dto = Map_Task_JPA_DTO.INSTANCE.map(reloadedTask, context);

		// assert: successors should be mapped
		assertThat(dto.successors().isPresent()).isTrue();
		assertThat(dto.successors().get().size()).isEqualTo(2);

		// assert: successors should be in context
		TaskDTO succ1FromContext = context.get(successor1, TaskDTO.class);
		TaskDTO succ2FromContext = context.get(successor2, TaskDTO.class);
		assertThat(succ1FromContext).isNotNull();
		assertThat(succ2FromContext).isNotNull();
	}

	@Test
	void afterMapping_skipsUnloadedCollections()
	{
		// arrange: persist entities
		TaskGroupJPA group = persistAndFlush(new TaskGroupJPA("test group"));
		TaskJPA superTask = new TaskJPA(group, "super task");
		TaskJPA subTask = new TaskJPA(group, "sub task");
		superTask.addSubTask(subTask);
		persistAndFlush(superTask);
		persistAndFlush(subTask);

		// Clear persistence context - collections will be lazy/unloaded
		clearPersistenceContext();
		TaskJPA reloadedSuperTask = find(TaskJPA.class, superTask.id());

		// Do NOT force loading of subTasks - they should remain unloaded
		PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();
		assertThat(persistenceUtil.isLoaded(reloadedSuperTask, "subTasks")).as("subTasks should NOT be loaded").isFalse();

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// act
		TaskDTO dto = Map_Task_JPA_DTO.INSTANCE.map(reloadedSuperTask, context);

		// assert: subTasks should NOT be mapped (they were not loaded)
		assertThat(dto.subTasks().isPresent()).as("unloaded subTasks should not be mapped").isFalse();

		// assert: subTask should NOT be in context (was not mapped because collection was not loaded)
		TaskDTO subFromContext = context.get(subTask, TaskDTO.class);
		assertThat(subFromContext).as("unloaded subTask should not be in context").isNull();
	}

	@Test
	void afterMapping_skipsRemapping_whenRelatedTaskAlreadyInContext()
	{
		// arrange: persist entities
		TaskGroupJPA group = persistAndFlush(new TaskGroupJPA("test group"));
		TaskJPA superTask = new TaskJPA(group, "super task");
		TaskJPA subTask = new TaskJPA(group, "sub task");
		subTask.superTask(superTask);
		persistAndFlush(superTask);
		persistAndFlush(subTask);

		// Clear and reload
		clearPersistenceContext();
		TaskJPA reloadedSuperTask = find(TaskJPA.class, superTask.id());
		TaskJPA reloadedSubTask = find(TaskJPA.class, subTask.id());

		// Force load superTask reference
		reloadedSubTask.superTask().ifPresent(st -> st.name());

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// pre-map superTask into context
		TaskDTO preMappedSuper = Map_Task_JPA_DTO.INSTANCE.map(reloadedSuperTask, context);
		assertThat(preMappedSuper).isNotNull();

		// act: mapping the subTask should reuse the existing superTask mapping
		TaskDTO dto = Map_Task_JPA_DTO.INSTANCE.map(reloadedSubTask, context);

		// assert: superTask mapping still the same instance in context
		TaskDTO superFromContext = context.get(superTask, TaskDTO.class);
		assertThat(superFromContext).isEqualTo(preMappedSuper);
		assertThat(dto.superTask().get()).isEqualTo(preMappedSuper);
	}
}
