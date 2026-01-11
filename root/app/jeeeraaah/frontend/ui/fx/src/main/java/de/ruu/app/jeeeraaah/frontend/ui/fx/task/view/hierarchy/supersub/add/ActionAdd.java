package de.ruu.app.jeeeraaah.frontend.ui.fx.task.view.hierarchy.supersub.add;

import de.ruu.app.jeeeraaah.common.api.bean.TaskBean;
import de.ruu.app.jeeeraaah.common.api.bean.TaskGroupBean;
import de.ruu.app.jeeeraaah.frontend.ui.fx.task.edit.TaskEditor;
import de.ruu.app.jeeeraaah.frontend.api.client.ws.rs.TaskServiceClient;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * Call {@link #execute()} to add a new main task to the task group in {@link #context} or a new subtask to the selected
 * task in {@link #context}'s tree view.
 */
@Slf4j
public class ActionAdd
{
	/** context with all necessary dependencies for {@link ActionAdd} */
	@Getter @Accessors(fluent = true)
	public static class Context
	{
		private TreeView<TaskBean> treeView;          // use tree view to determine selected task, if any
		private TaskGroupBean      taskGroup;         // use task group to associate new task with
		private TaskEditor         taskEditor;        // use editor to configure details for new task
		private TaskServiceClient  taskServiceClient; // use task service client to create new task in backend

		public Context
		(
				@NonNull TreeView<TaskBean> treeView,
				@NonNull TaskGroupBean      taskGroup,
				@NonNull TaskEditor         taskEditor,
				@NonNull TaskServiceClient  taskServiceClient
		)
		{
			this.treeView          = treeView;
			this.taskGroup         = taskGroup;
			this.taskEditor        = taskEditor;
			this.taskServiceClient = taskServiceClient;
		}
	}

	private final Context context;

	public ActionAdd(@NonNull Context context) { this.context = context; }

	public void execute()
	{
		TreeItem<TaskBean> selectedItem = context.treeView().getSelectionModel().getSelectedItem();

		if (selectedItem == null) new ActionAddMainTask     (context).execute();
		else                      new ActionAddMainOrSubTask(context).execute();
	}
}
