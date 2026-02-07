package de.ruu.app.jeeeraaah.frontend.api.client.ws.rs.auth;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.eclipse.microprofile.config.ConfigProvider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for authenticating with Keycloak and managing OAuth2 tokens.
 * 
 * <p>This service handles:
 * <ul>
 *   <li>User login via Resource Owner Password Credentials Grant (for development/testing)</li>
 *   <li>Storage and retrieval of access tokens and refresh tokens</li>
 *   <li>Configuration from MicroProfile Config properties</li>
 * </ul>
 * 
 * <p><strong>Configuration Properties (via microprofile-config.properties or environment):</strong>
 * <ul>
 *   <li>{@code keycloak.auth.server.url} - Keycloak server base URL (default: http://localhost:8080)</li>
 *   <li>{@code keycloak.realm} - Keycloak realm name (default: jeeeraaah-realm)</li>
 *   <li>{@code keycloak.client-id} - Client ID for frontend (default: jeeeraaah-frontend)</li>
 * </ul>
 * 
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * @Inject
 * private KeycloakAuthService authService;
 * 
 * try {
 *     String token = authService.login("testuser", "password123");
 *     // Use token for authenticated API calls
 * } catch (IOException | InterruptedException e) {
 *     log.error("Login failed", e);
 * }
 * }</pre>
 * 
 * <p><strong>Security Note:</strong><br>
 * The Resource Owner Password Credentials Grant (grant_type=password) is used here for simplicity
 * in development/testing scenarios. For production frontends (especially browser-based SPAs), 
 * use Authorization Code Flow with PKCE instead.
 */
@Singleton
@Slf4j
public class KeycloakAuthService
{
	// Keycloak configuration from MicroProfile Config
	private String keycloakServerUrl;
	private String realm;
	private String clientId;
	private String tokenUrl;
	
	// OAuth2 tokens
	@Getter private String accessToken;
	@Getter private String refreshToken;
	
	// Jackson ObjectMapper for JSON parsing
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * Initializes Keycloak configuration from MicroProfile Config properties.
	 * 
	 * <p>Reads configuration properties and constructs the token endpoint URL.
	 */
	@PostConstruct
	private void init()
	{
		keycloakServerUrl = ConfigProvider.getConfig()
				.getOptionalValue("keycloak.auth.server.url", String.class)
				.orElse("http://localhost:8080");
		
		realm = ConfigProvider.getConfig()
				.getOptionalValue("keycloak.realm", String.class)
				.orElse("jeeeraaah-realm");
		
		clientId = ConfigProvider.getConfig()
				.getOptionalValue("keycloak.client-id", String.class)
				.orElse("jeeeraaah-frontend");
		
		// Construct token endpoint URL
		tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", keycloakServerUrl, realm);
		
		log.debug("Keycloak auth service initialized:");
		log.debug("  Server URL: {}", keycloakServerUrl);
		log.debug("  Realm: {}", realm);
		log.debug("  Client ID: {}", clientId);
		log.debug("  Token URL: {}", tokenUrl);
	}
	
	/**
	 * Authenticates a user with Keycloak using username and password.
	 * 
	 * <p><b>OAuth 2.0 Flow: Resource Owner Password Credentials Grant (Direct Access Grants)</b></p>
	 * 
	 * <p>This method sends the user's credentials directly to Keycloak's token endpoint
	 * to obtain access and refresh tokens. This is the "Direct Access Grants" flow in Keycloak.</p>
	 * 
	 * <p><b>How it works:</b></p>
	 * <ol>
	 *   <li>Build form data with grant_type=password, client_id, username, password</li>
	 *   <li>Send POST request to Keycloak token endpoint with application/x-www-form-urlencoded</li>
	 *   <li>Keycloak validates credentials against user database</li>
	 *   <li>On success: Keycloak returns access_token (JWT) and refresh_token</li>
	 *   <li>Tokens are stored in this service for subsequent API calls</li>
	 * </ol>
	 * 
	 * <p><b>⚠️ Keycloak Configuration Required:</b></p>
	 * <p>The Keycloak client MUST have "Direct access grants enabled" set to ON.
	 * Otherwise, this method will fail with HTTP 400 error:</p>
	 * <pre>
	 * {"error":"unauthorized_client",
	 *  "error_description":"Client not allowed for direct access grants"}
	 * </pre>
	 * 
	 * <p><b>To enable in Keycloak:</b></p>
	 * <ul>
	 *   <li>Keycloak Admin Console → Clients → jeeeraaah-frontend</li>
	 *   <li>Settings tab → "Direct access grants enabled" → ON</li>
	 *   <li>Save</li>
	 * </ul>
	 * 
	 * <p><b>Security Notes:</b></p>
	 * <ul>
	 *   <li>This flow is suitable for desktop/mobile apps (trusted clients)</li>
	 *   <li>NOT recommended for browser-based web apps (use Authorization Code + PKCE instead)</li>
	 *   <li>Credentials are sent over HTTPS in production (TLS encryption required)</li>
	 *   <li>For development: http://localhost is acceptable</li>
	 * </ul>
	 * 
	 * @param username the username for authentication
	 * @param password the password for authentication
	 * @return the access token (JWT) that can be used for API authentication
	 * @throws IOException if the HTTP request fails or response cannot be parsed
	 * @throws InterruptedException if the HTTP request is interrupted
	 * @throws KeycloakAuthException if authentication fails (invalid credentials, client not configured for direct access, etc.)
	 */
	public String login(String username, String password) throws IOException, InterruptedException
	{
		log.debug("Attempting login for user: {}", username);
		
		// Build URL-encoded form data for token request
		String formData = String.format(
				"grant_type=password&client_id=%s&username=%s&password=%s",
				URLEncoder.encode(clientId, StandardCharsets.UTF_8),
				URLEncoder.encode(username, StandardCharsets.UTF_8),
				URLEncoder.encode(password, StandardCharsets.UTF_8));
		
		// Create HTTP client and request
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(tokenUrl))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString(formData))
				.build();
		
		// Send request and get response
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		
		// Check if authentication was successful
		if (response.statusCode() != 200)
		{
			log.error("Keycloak authentication failed with status {}: {}", response.statusCode(), response.body());
			throw new KeycloakAuthException(
					"Authentication failed: " + response.statusCode() + " - " + response.body());
		}
		
		// Parse JSON response to extract tokens
		JsonNode json = objectMapper.readTree(response.body());
		accessToken = json.get("access_token").asText();
		refreshToken = json.get("refresh_token").asText();
		
		// Extract token expiration info for logging
		int expiresIn = json.get("expires_in").asInt();
		int refreshExpiresIn = json.get("refresh_expires_in").asInt();
		
		log.info("""
				Login successful for user: {}
				  Access token expires in: {} seconds ({} minutes)
				  Refresh token expires in: {} seconds ({} minutes)
				  Access token (first 50 chars): {}...
				  KeycloakAuthService instance ID: {}""",
				username,
				expiresIn, expiresIn / 60,
				refreshExpiresIn, refreshExpiresIn / 60,
				accessToken.substring(0, Math.min(50, accessToken.length())),
				System.identityHashCode(this));

		return accessToken;
	}
	
	/**
	 * Refreshes the access token using the stored refresh token.
	 * 
	 * <p>This should be called when the access token has expired (typically indicated by
	 * 401 Unauthorized responses from the API).
	 * 
	 * @return the new access token
	 * @throws IOException if the HTTP request fails or response cannot be parsed
	 * @throws InterruptedException if the HTTP request is interrupted
	 * @throws KeycloakAuthException if refresh fails (e.g., refresh token expired)
	 * @throws IllegalStateException if no refresh token is available (user not logged in)
	 */
	public String refreshAccessToken() throws IOException, InterruptedException
	{
		if (refreshToken == null)
		{
			throw new IllegalStateException("No refresh token available. User must login first.");
		}
		
		log.debug("Refreshing access token");
		
		// Build URL-encoded form data for token refresh request
		String formData = String.format(
				"grant_type=refresh_token&client_id=%s&refresh_token=%s",
				URLEncoder.encode(clientId, StandardCharsets.UTF_8),
				URLEncoder.encode(refreshToken, StandardCharsets.UTF_8));
		
		// Create HTTP client and request
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(tokenUrl))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString(formData))
				.build();
		
		// Send request and get response
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		
		// Check if refresh was successful
		if (response.statusCode() != 200)
		{
			log.error("Token refresh failed with status {}: {}", response.statusCode(), response.body());
			throw new KeycloakAuthException("Token refresh failed: " + response.statusCode() + " - " + response.body());
		}
		
		// Parse JSON response to extract new tokens
		JsonNode json = objectMapper.readTree(response.body());
		accessToken = json.get("access_token").asText();
		refreshToken = json.get("refresh_token").asText();
		
		log.info("Access token refreshed successfully");
		
		return accessToken;
	}
	
	/**
	 * Logs out the current user by clearing stored tokens.
	 * 
	 * <p>Note: This only clears local tokens. For a complete logout that also invalidates
	 * the session on Keycloak server, call the logout endpoint separately.
	 */
	public void logout()
	{
		log.debug("Logging out - clearing tokens");
		accessToken = null;
		refreshToken = null;
	}
	
	/**
	 * Checks if the user is currently logged in (has an access token).
	 * 
	 * @return true if an access token is available, false otherwise
	 */
	public boolean isLoggedIn()
	{
		return accessToken != null;
	}
}
