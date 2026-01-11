package de.ruu.app.jeeeraaah.common.api.domain;

import static de.ruu.app.jeeeraaah.common.api.domain.PathsCommon.PATH_JEEERAAAH_ROOT;
import static de.ruu.app.jeeeraaah.common.api.domain.PathsCommon.TOKEN_BY_ID;

/**
 * Constants for task group REST API paths.
 * <p>
 * <b>URL Structure:</b> {@code http://host:port/jeeeraaah/taskgroup/...}
 * <ul>
 *   <li>{@code /jeeeraaah} - Application root (from {@code @ApplicationPath("jeeeraaah")})</li>
 *   <li>{@code /taskgroup} - Resource path (TOKEN_DOMAIN)</li>
 *   <li>Additional path segments for specific operations</li>
 * </ul>
 */
public interface PathsTaskGroup
{
	/**
	 * Domain path token for task group resources.
	 * Used in {@code @Path} annotations on resource classes.
	 */
	String TOKEN_DOMAIN = "/taskgroup";

	/**
	 * Full domain path for task group resources.
	 * Used by clients to construct request URIs.
	 * Results in: {@code http://host:port/jeeeraaah/taskgroup}
	 */
	String PATH_DOMAIN = PATH_JEEERAAAH_ROOT + TOKEN_DOMAIN;

	/**
	 * Path token for retrieving all task groups in flat structure.
	 */
	String TOKEN_ALL_FLAT = "/allFlat";

	/**
	 * Full path for retrieving all task groups in flat structure.
	 * Results in: {@code http://host:port/jeeeraaah/taskgroup/allFlat}
	 */
	String PATH_ALL_FLAT = PATH_DOMAIN + TOKEN_ALL_FLAT;

	/**
	 * Path token for retrieving a single task group with its tasks.
	 */
	String TOKEN_WITH_TASKS = "/withTasks";

	/**
	 * Full path for retrieving a task group with its tasks by ID.
	 * Results in: {@code http://host:port/jeeeraaah/taskgroup/withTasks/{id}}
	 */
	String PATH_WITH_TASKS = PATH_DOMAIN + TOKEN_WITH_TASKS + TOKEN_BY_ID;

	/**
	 * Path token for retrieving a task group with its tasks and direct neighbours.
	 */
	String TOKEN_WITH_TASKS_AND_DIRECT_NEIGHBOURS = "/withTasksAndDirectNeighbours";

	/**
	 * Full path for retrieving a task group with its tasks and direct neighbours by ID.
	 * Results in: {@code http://host:port/jeeeraaah/taskgroup/withTasksAndDirectNeighbours/{id}}
	 */
	String PATH_WITH_TASKS_AND_DIRECT_NEIGHBOURS = PATH_DOMAIN + TOKEN_WITH_TASKS_AND_DIRECT_NEIGHBOURS + TOKEN_BY_ID;

	/**
	 * Path token for removing a task from a task group.
	 * Used in {@code @Path} annotation on the remove task endpoint.
	 */
	String TOKEN_REMOVE_TASK_FROM_GROUP = "/removeTask/{idGroup}/{idTask}";

	/**
	 * Full path for removing a task from a task group.
	 * Requires both group ID and task ID as path parameters.
	 * Results in: {@code http://host:port/jeeeraaah/taskgroup/removeTask/{idGroup}/{idTask}}
	 */
	String PATH_REMOVE_TASK_FROM_GROUP = PATH_DOMAIN + TOKEN_REMOVE_TASK_FROM_GROUP;

	/**
	 * Full path for accessing a task group by ID.
	 * Results in: {@code http://host:port/jeeeraaah/taskgroup/{id}}
	 */
	String PATH_BY_ID = PATH_DOMAIN + TOKEN_BY_ID;
}