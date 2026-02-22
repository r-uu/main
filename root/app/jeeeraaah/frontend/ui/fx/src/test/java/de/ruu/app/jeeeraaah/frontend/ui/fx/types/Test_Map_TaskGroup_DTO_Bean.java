package de.ruu.app.jeeeraaah.frontend.ui.fx.types;

import de.ruu.app.jeeeraaah.common.api.bean.TaskGroupBean;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskDTO;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskGroupDTO;
import de.ruu.lib.mapstruct.ReferenceCycleTracking;
import org.junit.jupiter.api.Test;

import static de.ruu.app.jeeeraaah.common.api.mapping.Mappings.toBean;
import static de.ruu.app.jeeeraaah.common.api.mapping.Mappings.toDTO;
import static org.assertj.core.api.Assertions.assertThat;

class Test_Map_TaskGroup_DTO_Bean
{
	@Test void standalone()
	{
		String       name  = "name";
		TaskGroupDTO group = createGroup(name);

		assertThat(group.name()             ).isEqualTo(name);
		assertThat(group.tasks().isPresent()).isEqualTo(false);
	}

	@Test void standaloneMapped()
	{
		ReferenceCycleTracking context = new ReferenceCycleTracking();

		TaskGroupDTO  group  = createGroup("name");
		TaskGroupBean mapped = toBean(group,context);

		assertIs(mapped, group);
	}

	@Test void standaloneReMapped()
	{
		ReferenceCycleTracking context  = new ReferenceCycleTracking();

		TaskGroupDTO     group    = createGroup("name");
		TaskGroupBean    mapped   = toBean(group , context);
		TaskGroupDTO     reMapped = toDTO(mapped, context);

		assertIs(mapped, reMapped);
	}

	@Test void withSubTasks()
	{
		String name  = "name";
		int    count = 3;

		TaskGroupDTO group = createGroup(name);
		createTasks(group, 3);

		assertThat(group.tasks().isPresent() ).isEqualTo(true);
		assertThat(group.tasks().get().size()).isEqualTo(count);
	}

	void assertIs(TaskGroupBean bean, TaskGroupDTO dto)
	{
		assertThat(bean.id               ()).isEqualTo(dto.id               ());
		assertThat(bean.version          ()).isEqualTo(dto.version          ());
		assertThat(bean.name             ()).isEqualTo(dto.name             ());
		assertThat(bean.description      ()).isEqualTo(dto.description      ());
		assertThat(bean.tasks().isPresent()).isEqualTo(dto.tasks().isPresent());

		bean.tasks().ifPresent(ts -> assertThat(ts.size()).isEqualTo(dto.tasks().get().size()));
	}

	private TaskGroupDTO createGroup(                    String name) { return new TaskGroupDTO(name);   }
	private void         createTask (TaskGroupDTO group, String name) {        new TaskDTO(group, name); }
	private void         createTasks(TaskGroupDTO group, int count  )
	{
		for (int i = 0; i < count; i++) createTask(group, "task " + i);
	}
}