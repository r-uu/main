package de.ruu.app.jeeeraaah.frontend.ui.fx.task;

import org.eclipse.microprofile.config.ConfigProvider;

import de.ruu.app.jeeeraaah.frontend.api.client.ws.rs.auth.KeycloakAuthService;
import de.ruu.app.jeeeraaah.frontend.ui.fx.auth.LoginDialog;
import de.ruu.lib.fx.comp.FXCApp;
import jakarta.enterprise.inject.spi.CDI;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

/**
 * Task Management JavaFX application for Jeeeraaah.
 *
 * <p>This application requires Keycloak authentication before the task management UI is displayed.</p>
 *
 * <h2>Startup Flow:</h2>
 * <ol>
 *   <li>Application starts and {@link #start(Stage)} is called by JavaFX</li>
 *   <li>Configuration health check validates all required properties</li>
 *   <li>Docker environment health check runs with auto-fix capabilities</li>
 *   <li>Authentication: Testing mode auto-login OR login dialog displayed</li>
 *   <li>User authenticates with Keycloak credentials</li>
 *   <li>On successful login: Task management UI loads</li>
 *   <li>On cancelled login: Application exits</li>
 * </ol>
 *
 * @see LoginDialog
 * @see KeycloakAuthService
 */
@Slf4j
public class TaskManagementApp extends FXCApp
{
	private KeycloakAuthService authService;

	@Override
	public void start(Stage primaryStage) throws ExceptionInInitializerError
	{
		log.info("Starting Task Management application");

		// Configuration Health Check
		log.info("Validating configuration properties...");
		de.ruu.lib.util.config.mp.ConfigHealthCheck configCheck = new de.ruu.lib.util.config.mp.ConfigHealthCheck();
		de.ruu.lib.util.config.mp.ConfigHealthCheck.Result configResult = configCheck.validate();

		if (!configResult.isHealthy())
		{
			log.error("❌ Configuration validation failed!");
			configResult.getErrors().forEach(error -> log.error("  {}", error));
			Platform.exit();
			return;
		}
		log.info("✅ Configuration properties validated successfully");

		// Docker Environment Health Check with Auto-Fix
		log.info("Performing Docker environment health check...");
		de.ruu.lib.docker.health.HealthCheckRunner healthCheckRunner =
			de.ruu.lib.docker.health.HealthCheckProfiles.fullEnvironment();

		de.ruu.lib.docker.health.fix.AutoFixRunner autoFix = new de.ruu.lib.docker.health.fix.AutoFixRunner(healthCheckRunner);
		autoFix.registerStrategy(new de.ruu.lib.docker.health.fix.DockerContainerStartStrategy());
		autoFix.registerStrategy(new de.ruu.lib.docker.health.fix.KeycloakRealmSetupStrategy());

		if (!autoFix.runWithAutoFix())
		{
			log.error("❌ Docker environment is not ready and auto-fix failed!");
			showHealthCheckErrorDialog(healthCheckRunner);
			Platform.exit();
			return;
		}
		log.info("✅ Docker environment health check passed");

		// Authentication Setup
		authService = CDI.current().select(KeycloakAuthService.class).get();

		boolean testingMode = ConfigProvider.getConfig()
				.getOptionalValue("testing", Boolean.class)
				.orElse(false);

		if (testingMode && !authService.isLoggedIn())
		{
			log.info("=== Testing mode enabled - attempting automatic login ===");
			String testUsername = ConfigProvider.getConfig()
					.getOptionalValue("keycloak.test.user", String.class)
					.orElse(null);
			String testPassword = ConfigProvider.getConfig()
					.getOptionalValue("keycloak.test.password", String.class)
					.orElse(null);

			if (testUsername != null && testPassword != null)
			{
				try
				{
					authService.login(testUsername, testPassword);
					log.info("  ✅ Automatic login successful");
				}
				catch (Exception e)
				{
					log.error("  ❌ Automatic login failed: {}", e.getMessage(), e);
					Platform.exit();
					return;
				}
			}
			else
			{
				log.error("  Testing mode enabled but credentials missing");
				Platform.exit();
				return;
			}
		}

		while (!authService.isLoggedIn())
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

			if (!authService.isLoggedIn())
			{
				log.error("Login dialog returned success but user is not logged in - retrying");
			}
		}

		log.info("=== Authentication complete - starting UI initialization ===");
		super.start(primaryStage);
		log.info("Task Management UI initialized successfully");
	}

	private void showHealthCheckErrorDialog(de.ruu.lib.docker.health.HealthCheckRunner healthCheckRunner)
	{
		javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
			javafx.scene.control.Alert.AlertType.ERROR
		);
		alert.setTitle("Docker Environment Not Ready");
		alert.setHeaderText("Required services are not available");

		StringBuilder content = new StringBuilder();
		content.append("The following services are not ready:\n\n");

		for (de.ruu.lib.docker.health.HealthCheckResult result : healthCheckRunner.getFailures())
		{
			content.append("• ").append(result.getService()).append("\n");
			content.append("  Problem: ").append(result.getProblem()).append("\n");
			content.append("  Fix: ").append(result.getAlias()).append("\n\n");
		}

		content.append("\nAuto-fix failed. Please fix manually and restart the application.");
		alert.setContentText(content.toString());
		alert.showAndWait();
	}

	@Override
	public void stop() throws Exception
	{
		log.info("Stopping Task Management application");

		if (authService != null && authService.isLoggedIn())
		{
			log.info("Logging out user");
			authService.logout();
		}

		super.stop();
		log.info("Application stopped");
	}
}