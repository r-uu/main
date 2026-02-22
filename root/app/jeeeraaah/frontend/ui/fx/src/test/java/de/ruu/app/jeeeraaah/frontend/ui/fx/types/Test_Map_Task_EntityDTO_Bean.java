package de.ruu.app.jeeeraaah.frontend.ui.fx.types;

import de.ruu.app.jeeeraaah.common.api.bean.TaskBean;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskDTO;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskGroupDTO;
import de.ruu.lib.mapstruct.ReferenceCycleTracking;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static de.ruu.app.jeeeraaah.common.api.mapping.Mappings.toBean;
import static de.ruu.app.jeeeraaah.common.api.mapping.Mappings.toDTO;
import static org.assertj.core.api.Assertions.assertThat;

class Test_Map_Task_DTO_Bean
{
	@Test void standalone()
	{
		String  name = "name";
		TaskDTO task = createTaskEntity(createTaskGroupEntity(), name);

		assertThat(task.name     ()            ).isEqualTo(name );
		assertThat(task.superTask().isPresent()).isEqualTo(false);
		assertThat(task.subTasks ().isPresent()).isEqualTo(false);
	}

	@Test void standaloneMapped()
	{
		TaskDTO  task   = createTaskEntity(createTaskGroupEntity(), "name");
		TaskBean mapped = toBean(task, new ReferenceCycleTracking());

		assertIs(task, mapped);
	}

	@Test void standaloneReMapped()
	{
		ReferenceCycleTracking context = new ReferenceCycleTracking();
		TaskDTO  task     = createTaskEntity(createTaskGroupEntity(), "name");
		TaskBean mapped = toBean(task, new ReferenceCycleTracking());
		TaskDTO  remapped = toDTO(mapped, context);

		assertIs(remapped, mapped);
	}

	@Test void withTasks()
	{
		int    count = 3;

		TaskGroupDTO group = createTaskGroupEntity();
		createTasks(group, 3);

		assertThat(group.tasks().isPresent() ).isEqualTo(true );
		assertThat(group.tasks().get().size()).isEqualTo(count);

		ReferenceCycleTracking context = new ReferenceCycleTracking();
		for (TaskDTO dto : group.tasks().get())
		{
			TaskBean mapped   = toBean(dto, context);
			TaskDTO  remapped = toDTO(mapped, context);

			assertIs(dto     , mapped);
			assertIs(remapped, mapped);
		}
	}

	static void assertIs(TaskDTO dto, TaskBean bean)
	{
		assertThat(dto.id         ()).as("unexpected id"         ).isEqualTo(bean.id         ());
		assertThat(dto.version    ()).as("unexpected version"    ).isEqualTo(bean.version    ());
		assertThat(dto.name       ()).as("unexpected name"       ).isEqualTo(bean.name       ());
		assertThat(dto.description()).as("unexpected description").isEqualTo(bean.description());
		assertThat(dto.closed     ()).as("unexpected closed"     ).isEqualTo(bean.closed     ());

		assertThat(dto.superTask().isPresent()).as("unexpected parent"         ).isEqualTo(bean.superTask().isPresent());
		assertThat(dto.subTasks().isPresent()).as("unexpected children"        ).isEqualTo(bean.subTasks().isPresent());
		assertThat(dto.predecessors().isPresent()).as("unexpected predecessors").isEqualTo(bean.predecessors().isPresent());
		assertThat(dto.successors().isPresent()).as("unexpected successors"    ).isEqualTo(bean.successors().isPresent());

		dto.subTasks    ().ifPresent(ts -> assertThat(ts.size()).as("unexpected size children").isEqualTo(bean.subTasks().get().size()));
		dto.predecessors().ifPresent(ts -> assertThat(ts.size()).as("unexpected size predecessors").isEqualTo(bean.predecessors().get().size()));
		dto.successors  ().ifPresent(ts -> assertThat(ts.size()).as("unexpected size successors").isEqualTo(bean.successors().get().size()));

		// assert parent
		if (dto.superTask().isPresent())
		{
			assertThat(bean.superTask().isPresent()).isEqualTo(true);
			Test_Map_Task_DTO_Bean.assertIs(dto.superTask().get(), bean.superTask().get());
		}
		else { assertThat(bean.superTask().isPresent()).isEqualTo(false); }

		// assert children
		if (dto.subTasks().isPresent())
		{
			assertThat(bean.subTasks().isPresent()).isEqualTo(true);
			for (TaskDTO task : dto.subTasks().get())
			{
				assertThat(bean.subTasks().get().contains(task)).isEqualTo(true);
			}
		}
	}

	private TaskGroupDTO createTaskGroupEntity() { return new TaskGroupDTO("name"); }
	private TaskDTO      createTaskEntity(TaskGroupDTO group, String name)
	{
		TaskDTO result = new TaskDTO(group, name);
		result
				.description("description")
				.start      (LocalDate.now())
				.end        (LocalDate.now())
				.closed     (true)
				;
		return result;
	}

	private void createTask (TaskGroupDTO group, String name) { new TaskDTO(group, name); }
	private void createTasks(TaskGroupDTO group, int count)
	{
		for (int i = 0; i < count; i++) createTask(group, "task " + i);
	}
}