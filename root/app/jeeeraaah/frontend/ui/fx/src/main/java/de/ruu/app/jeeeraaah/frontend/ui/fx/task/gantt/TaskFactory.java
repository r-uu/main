package de.ruu.app.jeeeraaah.frontend.ui.fx.task.gantt;

import de.ruu.app.jeeeraaah.common.api.bean.TaskBean;
import de.ruu.app.jeeeraaah.common.api.bean.TaskGroupBean;
import de.ruu.app.jeeeraaah.frontend.api.client.ws.rs.TaskGroupServiceClient;
import de.ruu.app.jeeeraaah.frontend.ui.fx.util.ServiceOperationExecutor;
import de.ruu.lib.fx.control.dialog.ExceptionDialog;
import de.ruu.lib.ws.rs.NonTechnicalException;
import de.ruu.lib.ws.rs.TechnicalException;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Dependent
@Slf4j
class TaskFactory
{
	// inject service clients that are used to fetch data from the server
	@Inject private TaskGroupServiceClient taskGroupServiceClient;

	// inject the centralized executor for service operations with session retry handling
	@Inject private ServiceOperationExecutor executor;

	@NonNull Set<TaskBean> rootTasks
			(@NonNull final TaskGroupBean taskGroupBean, @NonNull LocalDate start, @NonNull LocalDate end)
	{
		Set<TaskBean> result = new HashSet<>();

		// fetch the task group with its tasks and their neighbour tasks from the server
		try
		{
			Optional<TaskGroupBean> optional =
					executor.execute
					(
							() -> taskGroupServiceClient.findWithTasksAndDirectNeighbours(requireNonNull(taskGroupBean.id())),
							"fetching task group details for id: " + taskGroupBean.id(),
							"Failed to load task group details",
							"Load failed after re-login"
					);

			if (optional.isPresent())
			{
				TaskGroupBean taskGroupBeanWithTasksAndDirectNeighbours = optional.get();

				// collect all of group's tasks together with their directly related tasks from the server
				if (taskGroupBean.tasks().isPresent())
						result =
								taskGroupBean
										.tasks()
										.get().stream().filter(taskBean -> taskBean.superTask().isEmpty()).collect(Collectors.toSet());
			}
			else log.debug("no lazy group could be retrieved");
		}
		catch (TechnicalException | NonTechnicalException e)
		{
			ExceptionDialog.showAndWait("failure fetching task group from backend", e);
		}

		return result;
	}

	@NonNull Set<TaskBean> rootTasks(@NonNull LocalDate start, @NonNull LocalDate end)
	{
		Set<TaskBean> result = new HashSet<>();

		TaskGroupBean group1 = new TaskGroupBean("group 1");

		TaskBean root  = new TaskBean(group1, "root 1-31");
		root.start(start);
		root.end(end);

		TaskBean child;

		child = new TaskBean(group1, "child 1: 1-3");
		child.start(start);
		child.end(start.plusDays(2));
		root.addSubTask(child);

		child = new TaskBean(group1, "child 2: 4-6");
		child.start(start.plusDays(3));
		child.end(start.plusDays(5));
		root.addSubTask(child);

		child = new TaskBean(group1, "child 3:  -6");
		child.end(start.plusDays(5));
		root.addSubTask(child);

		child = new TaskBean(group1, "child 4: 6-");
		child.start(start.plusDays(5));
		root.addSubTask(child);

		result.add(root);

		return result;
	}
}