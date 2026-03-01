package de.ruu.app.jeeeraaah.backend.common.mapping.jpa.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.ruu.app.jeeeraaah.backend.persistence.jpa.entity.TaskGroupJPA;
import de.ruu.app.jeeeraaah.backend.persistence.jpa.entity.TaskJPA;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskDTO;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskGroupDTO;
import de.ruu.lib.mapstruct.ReferenceCycleTracking;

class Map_TaskGroup_JPA_DTO_Test
{
	@Test void map_usesObjectFactory_andCopiesBasicFields()
	{
		// arrange
		String       name  = "group A";
		TaskGroupJPA group = new TaskGroupJPA(name);

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// act
		TaskGroupDTO dto = Map_TaskGroup_JPA_DTO.INSTANCE.map(group, context);

		// assert
		assertThat(dto          ).isNotNull();
		assertThat(dto.id()     ).isNull();
		assertThat(dto.version()).isNull();
		assertThat(dto.name()   ).isEqualTo(name);
		assertThat(dto.tasks().isPresent()).as("tasks should not be initialized by default").isFalse();
	}

	@Test void afterMapping_mapsRelatedTasks_intoContext_andAddsToDTO_whenNotAlreadyMapped()
	{
		// arrange
		TaskGroupJPA group = new TaskGroupJPA("group B");
		TaskJPA      task1 = new TaskJPA(group, "task 1");
		TaskJPA      task2 = new TaskJPA(group, "task 2");

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// precondition
		assertThat(group.tasks().isPresent()).isTrue();

		// act
		TaskGroupDTO dto = Map_TaskGroup_JPA_DTO.INSTANCE.map(group, context);

		// assert: tasks should be in context
		assertThat(dto).isNotNull();
		TaskDTO mappedT1 = context.get(task1, TaskDTO.class);
		TaskDTO mappedT2 = context.get(task2, TaskDTO.class);
		assertThat(mappedT1).isNotNull();
		assertThat(mappedT2).isNotNull();

		// assert: tasks are automatically added to DTO via TaskDTO constructor
		assertThat(dto.tasks().isPresent()).as("tasks should be in DTO via constructor").isTrue();
		assertThat(dto.tasks().get().size()).isEqualTo(2);
		assertThat(dto.tasks().get().contains(mappedT1)).isTrue();
		assertThat(dto.tasks().get().contains(mappedT2)).isTrue();
	}

	@Test void afterMapping_skipsRemapping_whenTaskAlreadyInContext()
	{
		// arrange
		TaskGroupJPA group = new TaskGroupJPA("group C");
		TaskJPA      task  = new TaskJPA(group, "task 3");

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// pre-map task into context
		TaskDTO preMapped = Map_Task_JPA_DTO.INSTANCE.map(task, context);
		assertThat(preMapped).isNotNull();
		assertThat(context.get(task, TaskDTO.class)).isEqualTo(preMapped);

		// act: mapping the group should not override the existing mapping for task
		TaskGroupDTO dto = Map_TaskGroup_JPA_DTO.INSTANCE.map(group, context);

		// assert: mapping still the same instance in context
		TaskDTO post = context.get(task, TaskDTO.class);
		assertThat(post).isEqualTo(preMapped);

		// assert: task is in DTO because it was added via TaskDTO constructor when first mapped
		assertThat(dto.tasks().isPresent()              ).isTrue();
		assertThat(dto.tasks().get().size()             ).isEqualTo(1);
		assertThat(dto.tasks().get().contains(preMapped)).isTrue();
	}

	@Test void map_handlesDescription_whenPresent()
	{
		// arrange
		String       description = "A detailed description";
		TaskGroupJPA group       = new TaskGroupJPA("group with description");
		group.description(description);

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// act
		TaskGroupDTO dto = Map_TaskGroup_JPA_DTO.INSTANCE.map(group, context);

		// assert
		assertThat(dto.description()).isEqualTo(Optional.of(description));
	}

	@Test void map_handlesDescription_whenNull()
	{
		// arrange
		TaskGroupJPA group = new TaskGroupJPA("group without description");
		group.description(null);

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// act
		TaskGroupDTO dto = Map_TaskGroup_JPA_DTO.INSTANCE.map(group, context);

		// assert
		assertThat(dto.description()).isEqualTo(Optional.empty());
	}

	@Test void map_addsGroupToContext()
	{
		// arrange
		TaskGroupJPA group = new TaskGroupJPA("group for context test");

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// act
		TaskGroupDTO dto = Map_TaskGroup_JPA_DTO.INSTANCE.map(group, context);

		// assert: group should be registered in context after mapping
		TaskGroupDTO fromContext = context.get(group, TaskGroupDTO.class);
		assertThat(fromContext).isNotNull();
		assertThat(fromContext).isEqualTo(dto);
	}

	@Test void map_handlesEmptyTasksSet()
	{
		// arrange
		TaskGroupJPA group = new TaskGroupJPA("group with no tasks");
		// TaskGroupJPA constructor doesn't initialize tasks - they remain null (lazy loading)
		assertThat(group.tasks().isPresent()).isFalse();

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// act
		TaskGroupDTO dto = Map_TaskGroup_JPA_DTO.INSTANCE.map(group, context);

		// assert: tasks should remain uninitialized in DTO when source tasks are null
		assertThat(dto).isNotNull();
		assertThat(dto.tasks().isPresent()).as("null source tasks should not initialize DTO tasks").isFalse();
	}

	@Test void map_handlesMultipleTasks()
	{
		// arrange
		TaskGroupJPA group = new TaskGroupJPA("group with multiple tasks");
		TaskJPA      task1 = new TaskJPA(group, "task 1");
		TaskJPA      task2 = new TaskJPA(group, "task 2");
		TaskJPA      task3 = new TaskJPA(group, "task 3");

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// act
		TaskGroupDTO dto = Map_TaskGroup_JPA_DTO.INSTANCE.map(group, context);

		// assert: all tasks should be mapped into context
		assertThat(context.get(task1, TaskDTO.class)).isNotNull();
		assertThat(context.get(task2, TaskDTO.class)).isNotNull();
		assertThat(context.get(task3, TaskDTO.class)).isNotNull();

		// assert: all tasks are in DTO (added automatically via TaskDTO constructor)
		assertThat(dto.tasks().isPresent()).isTrue();
		assertThat(dto.tasks().get().size()).isEqualTo(3);
	}

	@Test void objectFactory_createsCorrectDTOType()
	{
		// arrange
		TaskGroupJPA group = new TaskGroupJPA("factory test");

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		// act
		TaskGroupDTO dto = Map_TaskGroup_JPA_DTO.INSTANCE.map(group, context);

		// assert: verify correct DTO class is created
		assertThat(dto                                                               ).isNotNull();
		assertThat(dto instanceof TaskGroupDTO).as("should create TaskGroupDTO instance").isTrue();
	}
}
