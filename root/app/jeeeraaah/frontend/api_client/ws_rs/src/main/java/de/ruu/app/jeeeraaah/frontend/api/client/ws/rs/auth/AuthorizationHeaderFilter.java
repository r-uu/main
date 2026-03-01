package de.ruu.app.jeeeraaah.frontend.api.client.ws.rs.auth;

import java.io.IOException;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * <h1>JAX-RS Client Request Filter for Automatic Keycloak Token Injection</h1>
 * 
 * <p>This filter automatically adds the JWT bearer token to every outgoing HTTP request,
 * eliminating the need to manually set the {@code Authorization} header in each service method.</p>
 * 
 * <h2>How It Works</h2>
 * 
 * <p><strong>Automatic Registration:</strong></p>
 * <p>When registered with a JAX-RS {@code Client} (via {@code ClientBuilder.register()}),
 * this filter intercepts <strong>every</strong> HTTP request before it's sent to the server.</p>
 * 
 * <p><strong>Token Injection Process:</strong></p>
 * <ol>
 *   <li><strong>Request Interception:</strong> JAX-RS calls {@link #filter(ClientRequestContext)} 
 *       before sending any HTTP request</li>
 *   <li><strong>Login Check:</strong> Verifies if user is logged in via {@link KeycloakAuthService#isLoggedIn()}</li>
 *   <li><strong>Header Addition:</strong> If logged in, adds {@code Authorization: Bearer <token>} 
 *       to the request headers</li>
 *   <li><strong>Request Continuation:</strong> Request proceeds to server with authentication header</li>
 * </ol>
 * 
 * <h2>Benefits</h2>
 * 
 * <ul>
 *   <li><strong>DRY Principle:</strong> No need to repeat {@code .header("Authorization", "Bearer " + token)} 
 *       in every service method</li>
 *   <li><strong>Centralized Auth Logic:</strong> All authentication header handling in one place</li>
 *   <li><strong>Automatic Application:</strong> Works for GET, POST, PUT, DELETE, and all HTTP methods</li>
 *   <li><strong>Clean Service Code:</strong> Service methods focus on business logic, not auth mechanics</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * 
 * <h3>1. Register Filter During Client Creation</h3>
 * <pre>
 * {@literal @}PostConstruct
 * public void initClient() {
 *     ObjectMapper objectMapper = createObjectMapper();
 *     JacksonJsonProvider jacksonProvider = new JacksonJsonProvider(objectMapper);
 *     
 *     // Create JAX-RS client with filter registered
 *     client = ClientBuilder.newBuilder()
 *         .register(jacksonProvider)                    // JSON serialization
 *         .register(AuthorizationHeaderFilter.class)    // ✅ Automatic token injection
 *         .property(CONNECT_TIMEOUT, 5000)
 *         .property(READ_TIMEOUT, 15000)
 *         .build();
 * }
 * </pre>
 * 
 * <h3>2. Service Methods - Before (Manual Token Handling)</h3>
 * <pre>
 * // ❌ OLD WAY: Manual header in every method
 * public TaskBean findById(Long id) {
 *     Response response = client.target(baseURL)
 *         .path("task/{id}")
 *         .resolveTemplate("id", id)
 *         .request()
 *         .header("Authorization", "Bearer " + authService.getAccessToken())  // Repetitive!
 *         .get();
 *     // ... process response
 * }
 * </pre>
 * 
 * <h3>3. Service Methods - After (Automatic Token Injection)</h3>
 * <pre>
 * // ✅ NEW WAY: Filter adds header automatically
 * public TaskBean findById(Long id) {
 *     Response response = client.target(baseURL)
 *         .path("task/{id}")
 *         .resolveTemplate("id", id)
 *         .request()
 *         .get();  // No manual header needed - filter handles it!
 *     // ... process response
 * }
 * </pre>
 * 
 * <h2>Integration with Token Refresh</h2>
 * 
 * <p><strong>Important:</strong> This filter only <em>injects</em> the current access token.
 * It does <strong>not</strong> handle token expiry (401 Unauthorized responses).</p>
 * 
 * <p><strong>Token Refresh Handling:</strong></p>
 * <ul>
 *   <li><strong>Option A:</strong> Use {@code executeWithAuth()} helper method in service clients
 *       (handles both injection and refresh)</li>
 *   <li><strong>Option B:</strong> Implement a {@code ClientResponseFilter} to detect 401 and refresh
 *       (more complex, requires request replay)</li>
 *   <li><strong>Current Implementation:</strong> Filter handles injection, {@code executeWithAuth()}
 *       handles refresh in service clients</li>
 * </ul>
 * 
 * <h2>Design Decisions</h2>
 * 
 * <h3>Why Use {@code @Provider} Annotation?</h3>
 * <p>The {@code @Provider} annotation marks this class as a JAX-RS provider component,
 * making it discoverable by JAX-RS implementations. However, in our setup, we explicitly
 * register it via {@code ClientBuilder.register()}, so the annotation serves primarily
 * as documentation.</p>
 * 
 * <h3>Why Inject {@code KeycloakAuthService}?</h3>
 * <p>Using CDI {@code @Inject} ensures we get the same {@code @Singleton} instance
 * that all service clients share. This guarantees we're always using the current token
 * from the active user session.</p>
 * 
 * <h3>Why Check {@code isLoggedIn()}?</h3>
 * <p>Not all requests require authentication (though in our case, they typically do).
 * The check prevents adding an empty or invalid {@code Authorization} header when
 * no user is logged in.</p>
 * 
 * <h2>Security Considerations</h2>
 * 
 * <ul>
 *   <li><strong>Token Exposure:</strong> The token is sent as a plain HTTP header.
 *       Use HTTPS in production to prevent token interception.</li>
 *   <li><strong>Token Storage:</strong> {@link KeycloakAuthService} stores tokens only in memory,
 *       never persisted to disk, minimizing security risks.</li>
 *   <li><strong>Token Refresh:</strong> Access tokens are short-lived (5-15 minutes).
 *       Implement proper refresh logic in service clients.</li>
 *   <li><strong>Logout:</strong> Ensure {@code authService.logout()} is called on session end
 *       to clear tokens from memory.</li>
 * </ul>
 * 
 * <h2>Troubleshooting</h2>
 * 
 * <h3>Filter Not Working?</h3>
 * <ul>
 *   <li><strong>Check Registration:</strong> Ensure {@code .register(AuthorizationHeaderFilter.class)}
 *       is called in {@code ClientBuilder}</li>
 *   <li><strong>Check Login State:</strong> Verify {@code authService.isLoggedIn()} returns true</li>
 *   <li><strong>Check Token:</strong> Verify {@code authService.getAccessToken()} is not null</li>
 *   <li><strong>Enable Logging:</strong> Set log level to DEBUG to see filter activity</li>
 * </ul>
 * 
 * <h3>Still Getting 401 Unauthorized?</h3>
 * <ul>
 *   <li><strong>Token Expired:</strong> Access tokens expire after 5-15 minutes.
 *       Implement token refresh in service clients.</li>
 *   <li><strong>Wrong Token:</strong> Verify token was obtained for correct Keycloak realm/client</li>
 *   <li><strong>Server Config:</strong> Check backend server.xml has correct JWKS URI and issuer</li>
 *   <li><strong>Missing Roles:</strong> User may lack required roles (403 Forbidden instead of 401)</li>
 * </ul>
 * 
 * @see KeycloakAuthService Token management service
 * @see ClientRequestFilter JAX-RS client request filter interface
 * @see jakarta.ws.rs.client.ClientBuilder Client builder for registration
 * 
 * @author r-uu
 * @since 2025-12-26
 */
@Provider  // Marks this as a JAX-RS provider component
@Slf4j     // Lombok annotation for logging
public class AuthorizationHeaderFilter implements ClientRequestFilter
{
	/**
	 * Keycloak authentication service that manages JWT tokens.
	 * 
	 * <p><strong>Important:</strong> This is NOT injected via {@code @Inject} because
	 * Jersey's HK2 DI container doesn't know about CDI beans. Instead, it must be
	 * provided via constructor when registering the filter.</p>
	 * 
	 * <p>Ensures all filters share the same {@code @Singleton} instance and token state.</p>
	 */
	private final KeycloakAuthService authService;
	
	/**
	 * Constructor for explicit dependency injection.
	 * 
	 * <p>Required because Jersey's HK2 cannot inject CDI beans.
	 * The service must be passed when registering the filter:</p>
	 * <pre>
	 * KeycloakAuthService authService = CDI.current().select(KeycloakAuthService.class).get();
	 * client = ClientBuilder.newBuilder()
	 *     .register(new AuthorizationHeaderFilter(authService))
	 *     .build();
	 * </pre>
	 * 
	 * @param authService The Keycloak authentication service (CDI singleton)
	 */
	public AuthorizationHeaderFilter(KeycloakAuthService authService)
	{
		this.authService = authService;
	}

	/**
	 * Intercepts every outgoing HTTP request and adds the JWT bearer token.
	 * 
	 * <p><strong>Execution Flow:</strong></p>
	 * <ol>
	 *   <li>JAX-RS calls this method before sending the HTTP request</li>
	 *   <li>We check if user is logged in via {@link KeycloakAuthService#isLoggedIn()}</li>
	 *   <li>If logged in, we add {@code Authorization: Bearer <token>} to headers</li>
	 *   <li>Request continues to server with authentication header</li>
	 * </ol>
	 * 
	 * <p><strong>Header Format:</strong></p>
	 * <pre>
	 * Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0...
	 * </pre>
	 * 
	 * <p><strong>When This Method Is Called:</strong></p>
	 * <ul>
	 *   <li>Before <strong>every</strong> HTTP request: GET, POST, PUT, DELETE, etc.</li>
	 *   <li>After request builder is configured but before sending to server</li>
	 *   <li>Only for clients where this filter is registered</li>
	 * </ul>
	 * 
	 * <p><strong>Performance Impact:</strong></p>
	 * <ul>
	 *   <li>Minimal: Simple null check and header addition</li>
	 *   <li>No network I/O or heavy computation</li>
	 *   <li>Executes in same thread as request, so no async overhead</li>
	 * </ul>
	 * 
	 * @param requestContext The JAX-RS request context containing headers, URI, method, etc.
	 *                       Can be modified to add/remove headers before sending
	 * @throws IOException If an I/O error occurs (rare, typically only if custom logic added)
	 * 
	 * @see KeycloakAuthService#isLoggedIn() Check if user has valid session
	 * @see KeycloakAuthService#getAccessToken() Get current JWT access token
	 */
	@Override
	public void filter(ClientRequestContext requestContext) throws IOException
	{
		log.info("=== AuthorizationHeaderFilter called ===");
		log.info("  Request: {} {}", requestContext.getMethod(), requestContext.getUri());
		log.info("  AuthService instance ID: {}", System.identityHashCode(authService));
		log.info("  isLoggedIn(): {}", authService.isLoggedIn());
		
		// Only add Authorization header if user is logged in
		// This prevents sending invalid/empty tokens for unauthenticated requests
		if (authService.isLoggedIn())
		{
			String token = authService.getAccessToken();
			log.info("  Token present: {}", token != null);
			if (token != null)
			{
				log.info("  Token length: {}", token.length());
				log.info("  Token (first 50 chars): {}...", token.substring(0, Math.min(50, token.length())));
			}
			
			// Add JWT bearer token to Authorization header
			// Format: "Authorization: Bearer <jwt-token>"
			String authHeaderValue = "Bearer " + token;
			log.info("  Authorization header value length: {}", authHeaderValue.length());
			log.info("  Authorization header value (first 60 chars): {}",
					authHeaderValue.substring(0, Math.min(60, authHeaderValue.length())));

			requestContext.getHeaders().add("Authorization", authHeaderValue);

			// Verify header was added
			String verifyHeader = requestContext.getHeaderString("Authorization");
			log.info("  ✅ Authorization header added. Verification: {}",
					verifyHeader != null ? verifyHeader.substring(0, Math.min(60, verifyHeader.length())) : "NULL");
		}
		else
		{
			// User not logged in - request will proceed without Authorization header
			// Backend will likely return 401 Unauthorized
			log.warn("  ❌ Skipping Authorization header - user not logged in");
		}
	}
}
