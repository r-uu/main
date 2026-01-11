package de.ruu.app.jeeeraaah.common.api.domain;

import static de.ruu.app.jeeeraaah.common.api.domain.PathsCommon.PATH_JEEERAAAH_ROOT;
import static de.ruu.app.jeeeraaah.common.api.domain.PathsCommon.TOKEN_BY_ID;

/**
 * Constants for task REST API paths.
 * <p>
 * <b>URL Structure:</b> {@code http://host:port/jeeeraaah/task/...}
 * <ul>
 *   <li>{@code /jeeeraaah} - Application root (from {@code @ApplicationPath("jeeeraaah")})</li>
 *   <li>{@code /task} - Resource path (TOKEN_DOMAIN)</li>
 *   <li>Additional path segments for specific operations</li>
 * </ul>
 */
public interface PathsTask
{
	/**
	 * Domain path token for task resources.
	 * Used in {@code @Path} annotations on resource classes.
	 */
	String TOKEN_DOMAIN = "/task";

	/**
	 * Full domain path for task resources.
	 * Used by clients to construct request URIs.
	 * Results in: {@code http://host:port/jeeeraaah/task}
	 */
	String PATH_DOMAIN = PATH_JEEERAAAH_ROOT + TOKEN_DOMAIN;

	/** Path token for retrieving all tasks. */
	String TOKEN_ALL = "/all";

	/**
	 * Full path for retrieving all tasks.
	 * Results in: {@code http://host:port/jeeeraaah/task/all}
	 */
	String PATH_ALL = PATH_DOMAIN + TOKEN_ALL;

	/**
	 * Path token for retrieving a task by ID with its related tasks.
	 */
	String TOKEN_BY_ID_WITH_RELATED = "/byIdWithRelated";

	/**
	 * Full path for retrieving a task by ID with its related tasks.
	 * Results in: {@code http://host:port/jeeeraaah/task/byIdWithRelated/{id}}
	 */
	String PATH_BY_ID_WITH_RELATED = PATH_DOMAIN + TOKEN_BY_ID_WITH_RELATED + TOKEN_BY_ID;

	/**
	 * Path token for adding a sub-task relation.
	 */
	String TOKEN_ADD_SUB = "/addSub";

	/**
	 * Full path for adding a sub-task relation.
	 * Results in: {@code http://host:port/jeeeraaah/task/addSub}
	 */
	String PATH_ADD_SUB = PATH_DOMAIN + TOKEN_ADD_SUB;

	/**
	 * Path token for adding a predecessor relation.
	 */
	String TOKEN_ADD_PREDECESSOR = "/addPredecessor";

	/**
	 * Full path for adding a predecessor relation.
	 * Results in: {@code http://host:port/jeeeraaah/task/addPredecessor}
	 */
	String PATH_ADD_PREDECESSOR = PATH_DOMAIN + TOKEN_ADD_PREDECESSOR;

	/**
	 * Path token for adding a successor relation.
	 */
	String TOKEN_ADD_SUCCESSOR = "/addSuccessor";

	/**
	 * Full path for adding a successor relation.
	 * Results in: {@code http://host:port/jeeeraaah/task/addSuccessor}
	 */
	String PATH_ADD_SUCCESSOR = PATH_DOMAIN + TOKEN_ADD_SUCCESSOR;

	/**
	 * Path token for removing a sub-task relation.
	 */
	String TOKEN_REMOVE_SUB = "/removeSub";

	/**
	 * Full path for removing a sub-task relation.
	 * Results in: {@code http://host:port/jeeeraaah/task/removeSub}
	 */
	String PATH_REMOVE_SUB = PATH_DOMAIN + TOKEN_REMOVE_SUB;

	/**
	 * Path token for removing a predecessor relation.
	 */
	String TOKEN_REMOVE_PREDECESSOR = "/removePredecessor";

	/**
	 * Full path for removing a predecessor relation.
	 * Results in: {@code http://host:port/jeeeraaah/task/removePredecessor}
	 */
	String PATH_REMOVE_PREDECESSOR = PATH_DOMAIN + TOKEN_REMOVE_PREDECESSOR;

	/**
	 * Path token for removing a successor relation.
	 */
	String TOKEN_REMOVE_SUCCESSOR = "/removeSuccessor";

	/**
	 * Full path for removing a successor relation.
	 * Results in: {@code http://host:port/jeeeraaah/task/removeSuccessor}
	 */
	String PATH_REMOVE_SUCCESSOR = PATH_DOMAIN + TOKEN_REMOVE_SUCCESSOR;

	/**
	 * Path token for removing all neighbour relations from a task.
	 */
	String TOKEN_REMOVE_NEIGHBOURS_FROM_TASK = "/removeNeighboursFromTask";

	/**
	 * Full path for removing all neighbour relations from a task.
	 * Results in: {@code http://host:port/jeeeraaah/task/removeNeighboursFromTask}
	 */
	String PATH_REMOVE_NEIGHBOURS_FROM_TASK = PATH_DOMAIN + TOKEN_REMOVE_NEIGHBOURS_FROM_TASK;

	/**
	 * Full path for accessing a task by ID.
	 * Results in: {@code http://host:port/jeeeraaah/task/{id}}
	 */
	String PATH_BY_ID = PATH_DOMAIN + TOKEN_BY_ID;
}