package de.ruu.app.jeeeraaah.common.api.domain;

/**
 * Common constants for application backend REST API paths.
 * <p>
 * Constant prefixes have the following meanings:
 * <ul>
 *   <li> PATH_ prefixed constants define fully qualified REST endpoints for client calls</li>
 *   <li>TOKEN_ prefixed constants define path tokens for REST endpoints</li>
 * </ul>
 */
public interface PathsCommon
{
	/**
	 * Application root path token for jakarta-rs application class. It is used in {@code @ApplicationPath} annotation of
	 * {@code JeeeRaaah}. {@code @ApplicationPath} expects a path segment without a leading slash so do not use a leading
	 * slash here.
	 */
	String TOKEN_JEEERAAAH_ROOT = "jeeeraaah";

	/** Full application root path for construction of full REST API endpoint paths. */
	String  PATH_JEEERAAAH_ROOT = "/" + TOKEN_JEEERAAAH_ROOT;

	/** Path parameter token for resource IDs. Used in JAX-RS {@code @Path} annotations to capture ID parameters. */
	String TOKEN_BY_ID = "/{id}";
}