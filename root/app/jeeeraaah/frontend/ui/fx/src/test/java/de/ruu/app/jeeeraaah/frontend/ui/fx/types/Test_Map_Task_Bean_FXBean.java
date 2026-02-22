package de.ruu.app.jeeeraaah.frontend.ui.fx.types;

import de.ruu.app.jeeeraaah.common.api.bean.TaskBean;
import de.ruu.app.jeeeraaah.common.api.bean.TaskGroupBean;
import de.ruu.app.jeeeraaah.frontend.ui.fx.model.TaskFXBean;
import de.ruu.app.jeeeraaah.frontend.ui.fx.model.TaskGroupFXBean;
import de.ruu.lib.mapstruct.ReferenceCycleTracking;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

import static de.ruu.app.jeeeraaah.frontend.common.mapping.Mappings.toBean;
import static de.ruu.app.jeeeraaah.frontend.common.mapping.Mappings.toFXBean;
import static org.assertj.core.api.Assertions.assertThat;

class Test_Map_Task_Bean_FXBean
{
	@Test void standalone()
	{
		String   name = "name";
		TaskBean task = createTaskBean(createTaskGroupBean(), name);

		assertThat(task.name  ()).isEqualTo(name);

		assertThat(task.closed      ()            ).isEqualTo(false);
		assertThat(task.description ().isPresent()).isEqualTo(false);
		assertThat(task.superTask   ().isPresent()).isEqualTo(false);
		assertThat(task.subTasks    ().isPresent()).isEqualTo(false);
		assertThat(task.predecessors().isPresent()).isEqualTo(false);
		assertThat(task.successors  ().isPresent()).isEqualTo(false);
	}

	@Test void standaloneMapped()
	{
		ReferenceCycleTracking context = new ReferenceCycleTracking();

		TaskBean   task   = createTaskBean(createTaskGroupBean(), "name");
		TaskFXBean mapped = toFXBean(task, context);

		assertIs(task, mapped);
	}

	@Test void standaloneReMapped()
	{
		ReferenceCycleTracking context = new ReferenceCycleTracking();

		TaskBean       task = createTaskBean(createTaskGroupBean(), "name");
		TaskFXBean   mapped = toFXBean(task  ,context);
		TaskBean   reMapped = toBean  (mapped, context);

		assertIs(reMapped, mapped);
	}

	@Test void withTasks()
	{
		int    count = 3;

		TaskGroupBean group = createTaskGroupBean();
		createTasks(group, 3);

		assertThat(group.tasks().isPresent() ).isEqualTo(true );
		assertThat(group.tasks().get().size()).isEqualTo(count);

		ReferenceCycleTracking context = new ReferenceCycleTracking();

		for (TaskBean bean : group.tasks().get())
		{
			TaskFXBean   mapped = toFXBean(  bean, context);
			TaskBean   remapped = toBean  (mapped, context);

			assertIs(bean     , mapped);
			assertIs(remapped, mapped);
		}
	}

	private void assertIs(TaskBean taskBean, TaskFXBean taskFXBean)
	{
		assertThat(taskFXBean.name       ()).as("unexpected name").isEqualTo(taskBean.name       ());
		assertThat(taskFXBean.description()).as("unexpected description").isEqualTo(taskBean.description());
		assertThat(taskFXBean.start      ()).as("unexpected start").isEqualTo(taskBean.start      ());
		assertThat(taskFXBean.end        ()).as("unexpected end").isEqualTo(taskBean.end        ());
		assertThat(taskFXBean.closed     ()).as("unexpected closed").isEqualTo(taskBean.closed     ());

		assertIs(taskFXBean.taskGroup(), taskBean.taskGroup());

		assertThat(taskFXBean.superTask().isPresent()).as("unexpected parent").isEqualTo(taskBean.superTask().isPresent());
		assertThat(taskFXBean.subTasks().isPresent()).as("unexpected children").isEqualTo(taskBean.subTasks().isPresent());
		assertThat(taskFXBean.predecessors().isPresent()).as("unexpected predecessors").isEqualTo(taskBean.predecessors().isPresent());
		assertThat(taskFXBean.successors().isPresent()).as("unexpected successors").isEqualTo(taskBean.successors().isPresent());

		taskFXBean.subTasks().ifPresent(ts -> assertThat(ts.size()).as("unexpected children size").isEqualTo(taskBean.subTasks().get().size()));
		taskFXBean.predecessors().ifPresent(ts -> assertThat(ts.size()).as("unexpected predecessors size").isEqualTo(taskBean.predecessors().get().size()));
		taskFXBean.successors().ifPresent(ts -> assertThat(ts.size()).as("unexpected successors size").isEqualTo(taskBean.successors().get().size()));
	}

	private void assertIs(@NonNull TaskGroupFXBean fxBean, @NonNull TaskGroupBean bean)
	{
		assertThat(fxBean.id         ()).isEqualTo(bean.id         ());
		assertThat(fxBean.version    ()).isEqualTo(bean.version    ());
		assertThat(fxBean.name       ()).isEqualTo(bean.name       ());
		assertThat(fxBean.description()).isEqualTo(bean.description());
	}

	private TaskGroupBean createTaskGroupBean() { return new TaskGroupBean("name"); }
	private TaskBean createTaskBean(TaskGroupBean group, String name) { return new TaskBean(group, name ); }
	private void createTasks(TaskGroupBean group, int count)
	{
		for (int i = 0; i < count; i++) createTaskBean(group, "task " + i);
	}
}