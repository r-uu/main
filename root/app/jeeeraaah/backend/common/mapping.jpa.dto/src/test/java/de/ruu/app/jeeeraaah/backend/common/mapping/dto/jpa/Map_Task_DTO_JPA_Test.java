package de.ruu.app.jeeeraaah.backend.common.mapping.dto.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.ruu.app.jeeeraaah.backend.persistence.jpa.TaskGroupJPA;
import de.ruu.app.jeeeraaah.backend.persistence.jpa.TaskJPA;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskDTO;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskGroupDTO;
import de.ruu.lib.mapstruct.ReferenceCycleTracking;

/**
 * Unit tests for {@link Map_Task_DTO_JPA} mapper. These tests verify basic mapping functionality without database.
 */
public class Map_Task_DTO_JPA_Test
{
	@Test
	void map_usesObjectFactory_andCopiesBasicFields()
	{
		// arrange
		TaskGroupJPA group = new TaskGroupJPA("test group");
		TaskGroupDTO groupDTO = new TaskGroupDTO(group);
		TaskDTO dto = new TaskDTO(groupDTO, "test task");
		dto.description("test description");
		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// act
		TaskJPA jpa = Map_Task_DTO_JPA.INSTANCE.map(dto, context);

		// assert: basic fields are copied
		assertThat(jpa).isNotNull();
		assertThat(jpa.name()).isEqualTo("test task");
		assertThat(jpa.description().isPresent()).isTrue();
		assertThat(jpa.description().get()).isEqualTo("test description");

		// assert: task is added to context
		TaskJPA jpaFromContext = context.get(dto, TaskJPA.class);
		assertThat(jpaFromContext).isNotNull();
	}

	@Test
	void afterMapping_mapsSuperTask_whenPresent()
	{
		// arrange
		TaskGroupJPA group = new TaskGroupJPA("test group");
		TaskGroupDTO groupDTO = new TaskGroupDTO(group);
		TaskDTO superTaskDTO = new TaskDTO(groupDTO, "super task");
		TaskDTO dto = new TaskDTO(groupDTO, "test task");
		dto.superTask(superTaskDTO);

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// act
		TaskJPA jpa = Map_Task_DTO_JPA.INSTANCE.map(dto, context);

		// assert: super task should be mapped and in context
		assertThat(jpa.superTask().isPresent()).isTrue();
		assertThat(jpa.superTask().get().name()).isEqualTo("super task");
		assertThat(context.get(superTaskDTO, TaskJPA.class)).isNotNull();
	}

	@Test
	void afterMapping_mapsSubTasks_whenPresent()
	{
		// arrange
		TaskGroupJPA group = new TaskGroupJPA("test group");
		TaskGroupDTO groupDTO = new TaskGroupDTO(group);
		TaskDTO dto = new TaskDTO(groupDTO, "test task");
		TaskDTO subTask1 = new TaskDTO(groupDTO, "sub task 1");
		TaskDTO subTask2 = new TaskDTO(groupDTO, "sub task 2");
		subTask1.superTask(dto);
		subTask2.superTask(dto);

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// act
		TaskJPA jpa = Map_Task_DTO_JPA.INSTANCE.map(dto, context);

		// assert: sub tasks should be mapped and in context
		assertThat(jpa.subTasks().isPresent()).isTrue();
		assertThat(jpa.subTasks().get().size()).isEqualTo(2);
		assertThat(context.get(subTask1, TaskJPA.class)).isNotNull();
		assertThat(context.get(subTask2, TaskJPA.class)).isNotNull();
	}

	@Test
	void afterMapping_skipsSuperTask_whenNull()
	{
		// arrange
		TaskGroupJPA group = new TaskGroupJPA("test group");
		TaskGroupDTO groupDTO = new TaskGroupDTO(group);
		TaskDTO dto = new TaskDTO(groupDTO, "test task");
		// no super task set

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// act
		TaskJPA jpa = Map_Task_DTO_JPA.INSTANCE.map(dto, context);

		// assert: no super task
		assertThat(jpa.superTask().isPresent()).isFalse();
	}

	@Test
	void afterMapping_skipsRemapping_whenRelatedTaskAlreadyInContext()
	{
		// arrange
		TaskGroupJPA group = new TaskGroupJPA("test group");
		TaskGroupDTO groupDTO = new TaskGroupDTO(group);
		TaskDTO superTaskDTO = new TaskDTO(groupDTO, "super task");
		TaskDTO dto = new TaskDTO(groupDTO, "test task");
		dto.superTask(superTaskDTO);

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// pre-map super task
		TaskJPA preMappedSuperTask = Map_Task_DTO_JPA.INSTANCE.map(superTaskDTO, context);

		// act: map main task, should reuse pre-mapped super task
		TaskJPA jpa = Map_Task_DTO_JPA.INSTANCE.map(dto, context);

		// assert: super task should be the same instance from context
		TaskJPA superTaskFromContext = context.get(superTaskDTO, TaskJPA.class);
		assertThat(superTaskFromContext).isEqualTo(preMappedSuperTask);
		assertThat(jpa.superTask().get()).isEqualTo(preMappedSuperTask);
	}

	@Test
	void map_handlesDescription_whenNull()
	{
		// arrange
		TaskGroupJPA group = new TaskGroupJPA("test group");
		TaskGroupDTO groupDTO = new TaskGroupDTO(group);
		TaskDTO dto = new TaskDTO(groupDTO, "test task");
		// no description set

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// act
		TaskJPA jpa = Map_Task_DTO_JPA.INSTANCE.map(dto, context);

		// assert
		assertThat(jpa.name()).isEqualTo("test task");
		assertThat(jpa.description().isPresent()).isFalse();
	}

	@Test
	void objectFactory_createsCorrectJPAType()
	{
		// arrange
		TaskGroupJPA group = new TaskGroupJPA("test group");
		TaskGroupDTO groupDTO = new TaskGroupDTO(group);
		TaskDTO dto = new TaskDTO(groupDTO, "test task");
		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// act
		TaskJPA jpa = Map_Task_DTO_JPA.INSTANCE.map(dto, context);

		// assert: correct type is created
		assertThat(jpa).isNotNull();
		assertThat(jpa.getClass().getSimpleName()).isEqualTo("TaskJPA");
	}
}
