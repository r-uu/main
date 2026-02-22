package de.ruu.app.jeeeraaah.frontend.ui.fx.types;

import de.ruu.app.jeeeraaah.common.api.bean.TaskBean;
import de.ruu.app.jeeeraaah.common.api.bean.TaskGroupBean;
import de.ruu.app.jeeeraaah.frontend.ui.fx.model.TaskGroupFXBean;
import de.ruu.lib.mapstruct.ReferenceCycleTracking;
import org.junit.jupiter.api.Test;

import static de.ruu.app.jeeeraaah.frontend.common.mapping.Mappings.toBean;
import static de.ruu.app.jeeeraaah.frontend.common.mapping.Mappings.toFXBean;
import static org.assertj.core.api.Assertions.assertThat;

class Test_Map_TaskGroup_Bean_FXBean
{
	@Test void standalone()
	{
		String       name  = "name";
		TaskGroupBean group = createBean(name);

		assertThat(group.name()             ).isEqualTo(name);
		assertThat(group.tasks().isPresent()).isEqualTo(false);
	}

	@Test void standaloneMapped()
	{
		ReferenceCycleTracking context = new ReferenceCycleTracking();

		TaskGroupBean   group  = createBean("name");
		TaskGroupFXBean mapped = toFXBean(group, context);

		assertIs(group, mapped);
	}

	@Test void standaloneReMapped()
	{
		ReferenceCycleTracking context = new ReferenceCycleTracking();

		TaskGroupBean   group    = createBean("name");
		TaskGroupFXBean   mapped = toFXBean(group , context);
		TaskGroupBean   reMapped = toBean  (mapped, context);

		assertIs(reMapped, mapped);
	}

	@Test void withSubTasks()
	{
		String name  = "name";
		int    count = 3;

		TaskGroupBean group = createBean(name);
		createTasks(group, 3);

		assertThat(group.tasks().isPresent() ).isEqualTo(true);
		assertThat(group.tasks().get().size()).isEqualTo(count);
	}

	void assertIs(TaskGroupBean bean, TaskGroupFXBean fxBean)
	{
		assertThat(bean.id               ()).isEqualTo(fxBean.id               ());
		assertThat(bean.version          ()).isEqualTo(fxBean.version          ());
		assertThat(bean.name             ()).isEqualTo(fxBean.name             ());
		assertThat(bean.description      ()).isEqualTo(fxBean.description      ());
		assertThat(bean.tasks().isPresent()).isEqualTo(fxBean.tasks().isPresent());

		bean.tasks().ifPresent(ts -> assertThat(ts.size()).isEqualTo(fxBean.tasks().get().size()));
	}

	private TaskGroupBean createBean(String name)                     { return new TaskGroupBean("name"); }
	private void          createTask (TaskGroupBean group, String name) { new TaskBean(group, name); }
	private void          createTasks(TaskGroupBean group, int count)
	{
		for (int i = 0; i < count; i++) createTask(group, "task " + i);
	}
}