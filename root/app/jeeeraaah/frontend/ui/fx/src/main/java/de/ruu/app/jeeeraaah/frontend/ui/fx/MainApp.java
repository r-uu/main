package de.ruu.app.jeeeraaah.frontend.ui.fx;

import de.ruu.app.jeeeraaah.frontend.api.client.ws.rs.auth.KeycloakAuthService;
import de.ruu.app.jeeeraaah.frontend.ui.fx.auth.LoginDialog;
import de.ruu.lib.fx.comp.FXCApp;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

/**
 * Main JavaFX application for Jeeeraaah.
 * 
 * <p>This application requires Keycloak authentication before the main UI is displayed.</p>
 * 
 * <h2>Startup Flow:</h2>
 * <ol>
 *   <li>Application starts and {@link #start(Stage)} is called</li>
 *   <li>Login dialog is displayed if user not authenticated</li>
 *   <li>User authenticates with Keycloak credentials</li>
 *   <li>On success: Main UI loads</li>
 *   <li>On cancel: Application exits</li>
 * </ol>
 * 
 * @see de.ruu.app.jeeeraaah.frontend.ui.fx.auth.LoginDialog
 * @see KeycloakAuthService
 */
@Slf4j
public class MainApp extends FXCApp
{
	/**
	 * Keycloak authentication service for token management.
	 */
	@Inject
	private KeycloakAuthService authService;
	
	/**
	 * Starts the application with Keycloak authentication.
	 * 
	 * @param primaryStage the primary stage for this application
	 * @throws ExceptionInInitializerError if UI initialization fails
	 */
	@Override
	public void start(Stage primaryStage) throws ExceptionInInitializerError
	{
		log.info("Starting Main application");
		
		// Check authentication and show login dialog if needed
		if (!authService.isLoggedIn())
		{
			log.info("User not authenticated - showing login dialog");
			LoginDialog loginDialog = CDI.current().select(LoginDialog.class).get();
			boolean loginSuccessful = loginDialog.showAndWait(primaryStage);
			
			if (!loginSuccessful)
			{
				log.info("User cancelled login - exiting application");
				Platform.exit();
				return;
			}
			
			log.info("User authenticated successfully");
		}
		
		// User authenticated - proceed with UI initialization
		super.start(primaryStage);
		log.info("Main UI initialized successfully");
	}
	
	/**
	 * Called when application stops - performs cleanup.
	 */
	@Override
	public void stop() throws Exception
	{
		log.info("Stopping Main application");
		if (authService.isLoggedIn())
		{
			authService.logout();
		}
		super.stop();
	}
}