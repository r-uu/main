package de.ruu.app.jeeeraaah.frontend.ui.fx.taskgroup;

import de.ruu.lib.cdi.se.EventDispatcher;
import de.ruu.lib.util.AbstractEvent;
import jakarta.enterprise.context.ApplicationScoped;

/** Event that can be thrown to indicate that a {@link TaskGroupManagementService} has requested to be stopped */
public class TaskGroupManagementDisposeRequestEvent extends AbstractEvent<TaskGroupManagementService, Object>
{
	public TaskGroupManagementDisposeRequestEvent(final TaskGroupManagementService source) {
		super(source);
	}

	/** programmatically specify command line vm option {@code --add-reads de.ruu.app.jeeeraaah.client.fx=ALL-UNNAMED} */
	public static void addReadsUnnamedModule()
	{
		TaskGroupManagementDisposeRequestEvent.class.getModule()
				.addReads(TaskGroupManagementDisposeRequestEvent.class.getClassLoader().getUnnamedModule());
	}

	@ApplicationScoped
	public static class TaskGroupManagerDisposeRequestEventDispatcher
			extends EventDispatcher<TaskGroupManagementDisposeRequestEvent>
	{
	}
}