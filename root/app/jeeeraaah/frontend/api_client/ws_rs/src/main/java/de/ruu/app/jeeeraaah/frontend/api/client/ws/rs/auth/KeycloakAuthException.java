package de.ruu.app.jeeeraaah.frontend.api.client.ws.rs.auth;

/**
 * Exception thrown when Keycloak authentication or authorization fails.
 * 
 * <p>This includes scenarios like:
 * <ul>
 *   <li>Invalid credentials during login</li>
 *   <li>Expired refresh token</li>
 *   <li>Network errors communicating with Keycloak</li>
 *   <li>Invalid token format or signature</li>
 * </ul>
 */
public class KeycloakAuthException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public KeycloakAuthException(String message)
	{
		super(message);
	}

	public KeycloakAuthException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
