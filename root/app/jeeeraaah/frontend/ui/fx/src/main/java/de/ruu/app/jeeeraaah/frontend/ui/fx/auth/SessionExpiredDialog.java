package de.ruu.app.jeeeraaah.frontend.ui.fx.auth;

import de.ruu.app.jeeeraaah.frontend.api.client.ws.rs.auth.KeycloakAuthService;
import jakarta.inject.Inject;
import java.io.IOException;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

/**
 * Dialog displayed when the user's authentication session has expired.
 *
 * <p>This dialog provides a user-friendly way to handle token expiration by allowing
 * the user to re-authenticate without restarting the application.</p>
 *
 * <h2>When to Use:</h2>
 * <ul>
 *   <li>Refresh token has expired (typically after 30 minutes of inactivity)</li>
 *   <li>Access token refresh fails with authentication error</li>
 *   <li>Backend returns 401 even after token refresh attempt</li>
 * </ul>
 *
 * <h2>User Experience:</h2>
 * <pre>
 * ┌─────────────────────────────────────┐
 * │  Session Expired                    │
 * ├─────────────────────────────────────┤
 * │  Your session has expired due to    │
 * │  inactivity. Please login again     │
 * │  to continue working.               │
 * │                                     │
 * │  Username: [________________]       │
 * │  Password: [________________]       │
 * │                                     │
 * │  [error message if login failed]    │
 * │                                     │
 * │         [Exit]  [Re-Login]          │
 * └─────────────────────────────────────┘
 * </pre>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // In service client when authentication fails:
 * catch (Exception e) {
 *     log.error("Token refresh failed", e);
 *     authService.logout();
 *
 *     // Show session expired dialog on JavaFX thread
 *     Platform.runLater(() -> {
 *         SessionExpiredDialog dialog = CDI.current().select(SessionExpiredDialog.class).get();
 *         boolean reLoginSuccessful = dialog.showAndWait();
 *
 *         if (!reLoginSuccessful) {
 *             // User chose to exit - close application
 *             Platform.exit();
 *         }
 *         // If successful, operation can be retried
 *     });
 *
 *     throw new TechnicalException("Authentication failed. Please login again.", e);
 * }
 * }</pre>
 *
 * @see KeycloakAuthService
 * @see LoginDialog
 */
@Slf4j
public class SessionExpiredDialog
{
	/**
	 * Keycloak authentication service for re-authentication.
	 */
	@Inject
	private KeycloakAuthService authService;

	private Dialog<ButtonType> dialog;
	private TextField usernameField;
	private PasswordField passwordField;
	private Label errorLabel;
	private Button reLoginButton;

	/**
	 * Displays the session expired dialog and waits for user action.
	 *
	 * <p>This method blocks until the user either successfully re-authenticates or chooses to exit.</p>
	 *
	 * @return true if re-authentication was successful, false if user chose to exit
	 */
	public boolean showAndWait()
	{
		return showAndWait(null);
	}

	/**
	 * Displays the session expired dialog with a parent stage and waits for user action.
	 *
	 * @param parentStage Optional parent stage for modal dialog positioning (can be null)
	 * @return true if re-authentication was successful, false if user chose to exit
	 */
	public boolean showAndWait(Stage parentStage)
	{
		log.info("Showing session expired dialog");

		// Create dialog
		dialog = new Dialog<>();
		dialog.setTitle("Session Expired");
		dialog.setHeaderText("Your session has expired");
		dialog.initModality(Modality.APPLICATION_MODAL);

		if (parentStage != null)
		{
			dialog.initOwner(parentStage);
		}

		// Create content layout
		VBox content = createDialogContent();

		// Set dialog content
		DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.setContent(content);

		// Create buttons
		ButtonType exitButtonType = new ButtonType("Exit Application", ButtonBar.ButtonData.CANCEL_CLOSE);
		ButtonType reLoginButtonType = new ButtonType("Re-Login", ButtonBar.ButtonData.OK_DONE);
		dialogPane.getButtonTypes().addAll(exitButtonType, reLoginButtonType);

		// Get login button reference for enabling/disabling
		reLoginButton = (Button) dialogPane.lookupButton(reLoginButtonType);

		// Set up Enter key handler on password field
		passwordField.setOnAction(event -> {
			if (reLoginButton.isDisabled() == false)
			{
				reLoginButton.fire();
			}
		});

		// Handle login button action
		reLoginButton.setOnAction(event -> {
			event.consume(); // Prevent dialog from closing
			handleReLogin();
		});

		// Request focus on username field after dialog is shown
		Platform.runLater(() -> usernameField.requestFocus());

		// Show dialog and wait for result
		return dialog.showAndWait()
				.map(response -> response == reLoginButtonType && authService.isLoggedIn())
				.orElse(false);
	}

	/**
	 * Creates the dialog content layout with explanation, input fields, and error label.
	 */
	private VBox createDialogContent()
	{
		VBox vbox = new VBox(15);
		vbox.setPadding(new Insets(20));

		// Explanation text
		Label explanationLabel = new Label(
				"Your session has expired due to inactivity.\n" +
				"Please login again to continue working.\n\n" +
				"Your unsaved work has been preserved."
		);
		explanationLabel.setWrapText(true);
		explanationLabel.setMaxWidth(350);

		// Input fields
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 0, 10, 0));

		Label usernameLabel = new Label("Username:");
		usernameField = new TextField();
		usernameField.setPromptText("Enter your username");
		usernameField.setPrefWidth(250);

		Label passwordLabel = new Label("Password:");
		passwordField = new PasswordField();
		passwordField.setPromptText("Enter your password");
		passwordField.setPrefWidth(250);

		grid.add(usernameLabel, 0, 0);
		grid.add(usernameField, 1, 0);
		grid.add(passwordLabel, 0, 1);
		grid.add(passwordField, 1, 1);

		// Error label (initially hidden)
		errorLabel = new Label();
		errorLabel.setTextFill(Color.RED);
		errorLabel.setWrapText(true);
		errorLabel.setMaxWidth(350);
		errorLabel.setVisible(false);
		errorLabel.setManaged(false); // Don't take up space when hidden

		vbox.getChildren().addAll(explanationLabel, grid, errorLabel);

		return vbox;
	}

	/**
	 * Handles the re-login attempt when user clicks the Re-Login button.
	 */
	private void handleReLogin()
	{
		String username = usernameField.getText().trim();
		String password = passwordField.getText();

		// Validate input
		if (username.isEmpty() || password.isEmpty())
		{
			showError("Please enter both username and password.");
			return;
		}

		// Disable button during login attempt
		reLoginButton.setDisable(true);
		errorLabel.setVisible(false);
		errorLabel.setManaged(false);

		// Perform login in background to keep UI responsive
		new Thread(() -> {
			try
			{
				log.info("Re-authentication attempt for user: {}", username);
				authService.login(username, password);
				log.info("Re-authentication successful");

				// Close dialog on success (on JavaFX thread)
				Platform.runLater(() -> {
					dialog.setResult(dialog.getDialogPane().getButtonTypes().get(1)); // Re-Login button
					dialog.close();
				});
			}
			catch (IOException | InterruptedException e)
			{
				log.error("Re-authentication failed", e);

				// Show error message (on JavaFX thread)
				Platform.runLater(() -> {
					String errorMessage = "Authentication failed: " + extractErrorMessage(e);
					showError(errorMessage);
					reLoginButton.setDisable(false);
					passwordField.clear();
					passwordField.requestFocus();
				});
			}
		}).start();
	}

	/**
	 * Extracts a user-friendly error message from the exception.
	 */
	private String extractErrorMessage(Exception e)
	{
		String message = e.getMessage();

		if (message == null)
		{
			return "Unknown error occurred.";
		}

		// Parse Keycloak error responses
		if (message.contains("401") || message.contains("invalid_grant") || message.contains("Invalid user credentials"))
		{
			return "Invalid username or password.";
		}
		else if (message.contains("ConnectException") || message.contains("Connection refused"))
		{
			return "Cannot connect to authentication server.\nPlease check if Keycloak is running.";
		}
		else if (message.contains("SocketTimeoutException"))
		{
			return "Connection timeout. Please check your network.";
		}
		else
		{
			return message;
		}
	}

	/**
	 * Displays an error message in the dialog.
	 */
	private void showError(String message)
	{
		errorLabel.setText(message);
		errorLabel.setVisible(true);
		errorLabel.setManaged(true);
	}
}
