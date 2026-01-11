package de.ruu.app.jeeeraaah.frontend.ui.fx.auth;

import de.ruu.app.jeeeraaah.frontend.api.client.ws.rs.auth.KeycloakAuthService;
import de.ruu.lib.cdi.common.CDIUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Helper class for centralized authentication error handling in JavaFX applications.
 *
 * <p>This class provides utility methods to handle token expiration in a user-friendly way
 * across the entire application, ensuring consistent behavior when authentication fails.</p>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Centralized session expiration handling</li>
 *   <li>Prevents multiple simultaneous re-login dialogs</li>
 *   <li>Thread-safe operation for concurrent requests</li>
 *   <li>Automatic dialog display on JavaFX Application Thread</li>
 *   <li>Optional application exit on failed re-authentication</li>
 * </ul>
 *
 * <h2>Usage in Service Clients:</h2>
 * <pre>{@code
 * @Inject
 * private AuthenticationHelper authHelper;
 *
 * private Response executeWithAuth(WebTarget webTarget, RequestExecutor requestExecutor) {
 *     try {
 *         Response response = requestExecutor.execute(webTarget.request());
 *
 *         if (response.getStatus() == 401) {
 *             response.close();
 *
 *             try {
 *                 authService.refreshAccessToken();
 *                 response = requestExecutor.execute(webTarget.request());
 *
 *                 if (response.getStatus() == 401) {
 *                     // Refresh token also expired
 *                     authHelper.handleSessionExpired(true);
 *                     throw new TechnicalException("Authentication required");
 *                 }
 *             }
 *             catch (Exception e) {
 *                 authHelper.handleSessionExpired(true);
 *                 throw new TechnicalException("Authentication failed", e);
 *             }
 *         }
 *
 *         return response;
 *     }
 *     catch (ProcessingException e) {
 *         throw new TechnicalException("Communication error", e);
 *     }
 * }
 * }</pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>This class uses an {@link AtomicBoolean} flag to ensure that only one session expired dialog
 * is shown at a time, even if multiple concurrent API calls fail with 401 errors simultaneously.</p>
 *
 * @see SessionExpiredDialog
 * @see KeycloakAuthService
 */
@Slf4j
@ApplicationScoped
public class AuthenticationHelper
{
	@Inject
	private KeycloakAuthService authService;

	/**
	 * Flag to prevent multiple simultaneous session expired dialogs.
	 * Using AtomicBoolean for thread-safe check-and-set operation.
	 */
	private final AtomicBoolean sessionExpiredDialogShowing = new AtomicBoolean(false);

	/**
	 * Handles session expiration by showing a re-login dialog to the user.
	 *
	 * <p>This method ensures that only one session expired dialog is shown at a time,
	 * even if multiple concurrent API calls fail simultaneously.</p>
	 *
	 * <p><b>Behavior:</b></p>
	 * <ul>
	 *   <li>Clears current tokens via {@code authService.logout()}</li>
	 *   <li>Shows {@link SessionExpiredDialog} on JavaFX Application Thread</li>
	 *   <li>Waits for user to either re-authenticate or cancel</li>
	 *   <li>If {@code exitOnCancel} is true: exits application if user cancels</li>
	 *   <li>If {@code exitOnCancel} is false: returns to caller on cancel</li>
	 * </ul>
	 *
	 * <p><b>Thread Safety:</b> Safe to call from any thread. Dialog will always be shown
	 * on the JavaFX Application Thread.</p>
	 *
	 * @param exitOnCancel If true, application exits if user cancels re-login;
	 *                     if false, method returns and caller can handle cancellation
	 * @return true if re-authentication was successful, false if user cancelled
	 */
	public boolean handleSessionExpired(boolean exitOnCancel)
	{
		// Ensure user is logged out
		authService.logout();

		// Check if dialog is already showing
		if (!sessionExpiredDialogShowing.compareAndSet(false, true))
		{
			log.debug("Session expired dialog is already showing, skipping duplicate");
			return false;
		}

		log.warn("Session expired - showing re-authentication dialog");

		try
		{
			// Use CompletableFuture to wait for dialog result from non-JavaFX thread
			CompletableFuture<Boolean> future = new CompletableFuture<>();

			// Show dialog on JavaFX Application Thread
			Platform.runLater(() -> {
				try
				{
					SessionExpiredDialog dialog = CDIUtil.select(SessionExpiredDialog.class);
					boolean reLoginSuccessful = dialog.showAndWait();

					if (!reLoginSuccessful && exitOnCancel)
					{
						log.info("User cancelled re-login, exiting application");
						Platform.exit();
						System.exit(0);
					}

					future.complete(reLoginSuccessful);
				}
				catch (Exception e)
				{
					log.error("Error showing session expired dialog", e);
					future.completeExceptionally(e);
				}
			});

			// Wait for dialog to complete
			return future.get();
		}
		catch (Exception e)
		{
			log.error("Error handling session expiration", e);
			return false;
		}
		finally
		{
			// Reset flag so future expirations can show the dialog again
			sessionExpiredDialogShowing.set(false);
		}
	}

	/**
	 * Handles session expiration with default behavior (exit on cancel).
	 *
	 * <p>This is a convenience method that calls {@link #handleSessionExpired(boolean)}
	 * with {@code exitOnCancel = true}.</p>
	 *
	 * @return true if re-authentication was successful, false if user cancelled
	 */
	public boolean handleSessionExpired()
	{
		return handleSessionExpired(true);
	}

	/**
	 * Checks if a given exception is related to authentication failure.
	 *
	 * <p>This method inspects the exception message to determine if it's an authentication error
	 * that should trigger the session expired dialog.</p>
	 *
	 * @param exception The exception to check
	 * @return true if the exception indicates an authentication failure
	 */
	public boolean isAuthenticationException(Exception exception)
	{
		if (exception == null)
		{
			return false;
		}

		String message = exception.getMessage();
		if (message == null)
		{
			return false;
		}

		// Check for common authentication error patterns
		return message.contains("Authentication") ||
				message.contains("401") ||
				message.contains("Unauthorized") ||
				message.contains("Token") && message.contains("expired") ||
				message.contains("session expired") ||
				message.contains("login required") ||
				message.contains("Re-login required");
	}

	/**
	 * Determines if the current user is authenticated.
	 *
	 * @return true if user has a valid access token
	 */
	public boolean isAuthenticated()
	{
		return authService.isLoggedIn();
	}
}
