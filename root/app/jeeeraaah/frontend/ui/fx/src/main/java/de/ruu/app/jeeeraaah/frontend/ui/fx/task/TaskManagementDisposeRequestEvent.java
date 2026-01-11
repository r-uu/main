package de.ruu.app.jeeeraaah.frontend.ui.fx.task;

import de.ruu.app.jeeeraaah.frontend.ui.fx.taskgroup.TaskGroupManagementService;
import de.ruu.lib.util.AbstractEvent;

/** Event that can be thrown to indicate that a {@link TaskGroupManagementService} has requested to be stopped */
public class TaskManagementDisposeRequestEvent extends AbstractEvent<TaskManagementService, Object>
{
	public TaskManagementDisposeRequestEvent(final TaskManagementService source) {
		super(source);
	}
}