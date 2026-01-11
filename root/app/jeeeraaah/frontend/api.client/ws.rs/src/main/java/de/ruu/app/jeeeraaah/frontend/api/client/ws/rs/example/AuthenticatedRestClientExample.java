package de.ruu.app.jeeeraaah.frontend.api.client.ws.rs.example;

import static de.ruu.app.jeeeraaah.common.api.mapping.Mappings.toBean;

import de.ruu.app.jeeeraaah.common.api.bean.TaskGroupBean;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskGroupDTO;
import de.ruu.app.jeeeraaah.frontend.api.client.ws.rs.auth.KeycloakAuthService;
import de.ruu.lib.mapstruct.ReferenceCycleTracking;
import de.ruu.lib.ws.rs.TechnicalException;
import jakarta.inject.Inject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * <h2>Beispiel: REST-Client mit Keycloak-Authentifizierung</h2>
 * 
 * <p>Dieses Beispiel zeigt, wie ein REST-Client angepasst werden muss, um Keycloak JWT-Tokens
 * für die Authentifizierung zu verwenden.</p>
 * 
 * <h3>Problem ohne Authentifizierung:</h3>
 * <pre>
 * // ❌ Ohne Token → Server gibt 401 Unauthorized zurück
 * Response response = webTarget.request().get();
 * </pre>
 * 
 * <h3>Lösung mit Keycloak:</h3>
 * <pre>
 * // ✅ Mit Bearer Token → Server akzeptiert Request
 * Response response = webTarget.request()
 *     .header("Authorization", "Bearer " + authService.getAccessToken())
 *     .get();
 * </pre>
 * 
 * <h3>Authentifizierungsflow:</h3>
 * <ol>
 *   <li><strong>Login:</strong> User gibt Username/Password ein → {@code authService.login(username, password)}</li>
 *   <li><strong>Token erhalten:</strong> Keycloak gibt Access Token zurück (JWT, gültig 5-15 Min.)</li>
 *   <li><strong>API-Calls:</strong> Token wird in HTTP-Header {@code Authorization: Bearer <token>} mitgesendet</li>
 *   <li><strong>Token-Validierung:</strong> Server prüft Token-Signatur und Rollen</li>
 *   <li><strong>Token-Expiry:</strong> Bei 401 Unauthorized → {@code authService.refreshAccessToken()}</li>
 *   <li><strong>Refresh-Token:</strong> Neues Access Token wird angefordert (ohne Re-Login)</li>
 * </ol>
 * 
 * <h3>Wichtige Konzepte:</h3>
 * 
 * <h4>1. Bearer Token Authentication</h4>
 * <pre>
 * Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
 * </pre>
 * <ul>
 *   <li><strong>"Bearer":</strong> Authentifizierungsschema (RFC 6750)</li>
 *   <li><strong>Token:</strong> JWT (JSON Web Token) mit Signatur, Claims (User-ID, Rollen, Ablaufdatum)</li>
 *   <li><strong>Server-Validierung:</strong> Prüft Signatur mit Public Key, extrahiert Rollen aus Token</li>
 * </ul>
 * 
 * <h4>2. Token Refresh bei 401 Unauthorized</h4>
 * <p>Access Tokens sind kurzlebig (5-15 Min.). Wenn ein Token abläuft, gibt der Server 401 zurück.
 * Statt den User erneut einloggen zu lassen, kann das Token mit dem Refresh Token erneuert werden:</p>
 * <ul>
 *   <li><strong>Access Token:</strong> Kurzlebig, wird bei jedem API-Call mitgesendet</li>
 *   <li><strong>Refresh Token:</strong> Langlebig (30 Tage), wird nur für Token-Refresh verwendet</li>
 *   <li><strong>Vorteil:</strong> User bleibt eingeloggt, ohne ständig Passwort eingeben zu müssen</li>
 * </ul>
 * 
 * <h4>3. Security Best Practices</h4>
 * <ul>
 *   <li><strong>Niemals Tokens loggen:</strong> Könnte von Angreifern abgefangen werden</li>
 *   <li><strong>HTTPS verwenden:</strong> In Production immer SSL/TLS für sichere Token-Übertragung</li>
 *   <li><strong>Tokens nicht persistieren:</strong> Nur im Arbeitsspeicher halten, nicht in Dateien speichern</li>
 *   <li><strong>Logout implementieren:</strong> Tokens löschen bei Logout</li>
 * </ul>
 * 
 * @see KeycloakAuthService Zentrale Klasse für Login, Token-Refresh, Logout
 */
@Slf4j
public class AuthenticatedRestClientExample
{
	/**
	 * <strong>Dependency Injection:</strong> KeycloakAuthService verwaltet Tokens zentral.
	 * Alle REST-Clients teilen sich die gleiche Service-Instanz (@Singleton).
	 */
	@Inject
	private KeycloakAuthService authService;

	/**
	 * JAX-RS HTTP-Client für REST-Calls
	 */
	private Client client;

	// ================================================================================================================
	// BEISPIEL 1: Einfacher GET-Request mit Token
	// ================================================================================================================

	/**
	 * <h3>Beispiel 1: GET-Request mit Authentifizierung</h3>
	 * 
	 * <p><strong>Vorher (ohne Auth):</strong></p>
	 * <pre>
	 * Response response = webTarget.request().get();
	 * </pre>
	 * 
	 * <p><strong>Nachher (mit Auth):</strong></p>
	 * <pre>
	 * Response response = webTarget.request()
	 *     .header("Authorization", "Bearer " + authService.getAccessToken())
	 *     .get();
	 * </pre>
	 * 
	 * <p><strong>Was passiert:</strong></p>
	 * <ol>
	 *   <li>User hat sich bereits eingeloggt → Access Token ist in {@code authService} gespeichert</li>
	 *   <li>Token wird als HTTP-Header {@code Authorization: Bearer <token>} mitgesendet</li>
	 *   <li>Server validiert Token-Signatur und prüft Rollen</li>
	 *   <li>Wenn Token gültig und User hat Rolle {@code taskgroup-read} → Server gibt Daten zurück</li>
	 *   <li>Wenn Token ungültig/abgelaufen → Server gibt 401 Unauthorized</li>
	 * </ol>
	 * 
	 * @param id TaskGroup-ID
	 * @return TaskGroupBean oder null bei 404
	 * @throws TechnicalException bei Netzwerk- oder Server-Fehlern
	 */
	public TaskGroupBean findTaskGroupById_Simple(Long id) throws TechnicalException
	{
		String baseURL = "http://localhost:9080/jeeeraaah-backend";
		WebTarget webTarget = client.target(baseURL)
				.path("taskgroup")
				.path("by-id")
				.resolveTemplate("id", id);

		log.debug("Calling: {}", webTarget.getUri());

		try (Response response = webTarget
				.request()
				// ✅ HIER: Authorization-Header mit Bearer Token hinzufügen
				.header("Authorization", "Bearer " + authService.getAccessToken())
				.get())
		{
			int status = response.getStatus();
			log.debug("Response status: {}", status);

			if (status == 200) // OK
			{
				TaskGroupDTO dto = response.readEntity(TaskGroupDTO.class);
				return toBean(dto, new ReferenceCycleTracking());
			}
			else if (status == 404) // Not Found
			{
				log.debug("TaskGroup with id {} not found", id);
				return null;
			}
			else if (status == 401) // Unauthorized
			{
				// Token ist abgelaufen oder ungültig
				log.warn("Authentication failed (401 Unauthorized). Token expired?");
				throw new TechnicalException("Authentication required");
			}
			else if (status == 403) // Forbidden
			{
				// Token ist gültig, aber User hat nicht die erforderliche Rolle
				log.warn("Access denied (403 Forbidden). Missing role 'taskgroup-read'?");
				throw new TechnicalException("Access denied");
			}
			else
			{
				log.error("Unexpected status: {}", status);
				throw new TechnicalException("Unexpected status: " + status);
			}
		}
		catch (ProcessingException e)
		{
			// Netzwerk-Fehler (Server nicht erreichbar, Timeout, etc.)
			throw new TechnicalException("Communication error", e);
		}
	}

	// ================================================================================================================
	// BEISPIEL 2: GET-Request mit automatischem Token-Refresh
	// ================================================================================================================

	/**
	 * <h3>Beispiel 2: GET-Request mit automatischem Token-Refresh</h3>
	 * 
	 * <p>Dieses Beispiel zeigt, wie man mit abgelaufenen Tokens umgeht:</p>
	 * 
	 * <h4>Token-Expiry Problem:</h4>
	 * <ul>
	 *   <li>Access Tokens sind nur 5-15 Minuten gültig</li>
	 *   <li>Während ein User die App nutzt, kann das Token ablaufen</li>
	 *   <li>Server gibt dann 401 Unauthorized zurück</li>
	 * </ul>
	 * 
	 * <h4>Lösung: Automatischer Token-Refresh</h4>
	 * <ol>
	 *   <li>Ersten Request mit aktuellem Token senden</li>
	 *   <li>Wenn 401 Unauthorized → Token ist abgelaufen</li>
	 *   <li>{@code authService.refreshAccessToken()} aufrufen → neues Token holen</li>
	 *   <li>Request mit neuem Token wiederholen</li>
	 *   <li>Wenn Refresh fehlschlägt → Refresh Token ist auch abgelaufen → Re-Login erforderlich</li>
	 * </ol>
	 * 
	 * <h4>Wichtig:</h4>
	 * <ul>
	 *   <li><strong>Nur 1x wiederholen:</strong> Wenn 2. Request auch 401 gibt, nicht endlos probieren</li>
	 *   <li><strong>Refresh Token:</strong> Wird beim Login automatisch gespeichert</li>
	 *   <li><strong>Logout bei Fehler:</strong> Wenn Refresh fehlschlägt, User ausloggen</li>
	 * </ul>
	 * 
	 * @param id TaskGroup-ID
	 * @return TaskGroupBean oder null bei 404
	 * @throws TechnicalException bei Netzwerk- oder Auth-Fehlern
	 */
	public TaskGroupBean findTaskGroupById_WithTokenRefresh(Long id) throws TechnicalException
	{
		String baseURL = "http://localhost:9080/jeeeraaah-backend";
		WebTarget webTarget = client.target(baseURL)
				.path("taskgroup")
				.path("by-id")
				.resolveTemplate("id", id);

		log.debug("Calling: {}", webTarget.getUri());

		// ============================================================================================================
		// SCHRITT 1: Ersten Request mit aktuellem Token senden
		// ============================================================================================================
		try (Response response = webTarget
				.request()
				.header("Authorization", "Bearer " + authService.getAccessToken())
				.get())
		{
			int status = response.getStatus();

			// ========================================================================================================
			// SCHRITT 2: Wenn 401 Unauthorized → Token-Refresh versuchen
			// ========================================================================================================
			if (status == 401)
			{
				log.debug("Access token expired (401), attempting token refresh...");

				try
				{
					// Neues Access Token mit Refresh Token holen
					String newToken = authService.refreshAccessToken();
					log.debug("Token refresh successful, retrying request...");

					// ================================================================================================
					// SCHRITT 3: Request mit neuem Token wiederholen
					// ================================================================================================
					try (Response retryResponse = webTarget
							.request()
							.header("Authorization", "Bearer " + newToken)
							.get())
					{
						int retryStatus = retryResponse.getStatus();

						if (retryStatus == 200)
						{
							TaskGroupDTO dto = retryResponse.readEntity(TaskGroupDTO.class);
							return toBean(dto, new ReferenceCycleTracking());
						}
						else if (retryStatus == 404)
						{
							return null;
						}
						else if (retryStatus == 401)
						{
							// Auch nach Refresh noch 401 → Refresh Token ist auch abgelaufen
							log.error("Authentication failed even after token refresh. Re-login required.");
							authService.logout(); // Lokale Tokens löschen
							throw new TechnicalException("Re-login required");
						}
						else
						{
							throw new TechnicalException("Unexpected status after retry: " + retryStatus);
						}
					}
				}
				catch (Exception e)
				{
					// Token-Refresh fehlgeschlagen → Refresh Token ist abgelaufen
					log.error("Token refresh failed", e);
					authService.logout(); // Lokale Tokens löschen
					throw new TechnicalException("Re-login required", e);
				}
			}

			// ========================================================================================================
			// SCHRITT 4: Normale Response-Verarbeitung (kein 401)
			// ========================================================================================================
			else if (status == 200)
			{
				TaskGroupDTO dto = response.readEntity(TaskGroupDTO.class);
				return toBean(dto, new ReferenceCycleTracking());
			}
			else if (status == 404)
			{
				return null;
			}
			else if (status == 403)
			{
				log.warn("Access denied (403). Missing role 'taskgroup-read'?");
				throw new TechnicalException("Access denied");
			}
			else
			{
				throw new TechnicalException("Unexpected status: " + status);
			}
		}
		catch (ProcessingException e)
		{
			throw new TechnicalException("Communication error", e);
		}
	}

	// ================================================================================================================
	// BEISPIEL 3: Wiederverwendbare Methode für Token-Handling
	// ================================================================================================================

	/**
	 * <h3>Beispiel 3: Wiederverwendbare Hilfsmethode</h3>
	 * 
	 * <p>Um Code-Duplikation zu vermeiden, kann man das Token-Refresh-Pattern in eine
	 * Hilfsmethode auslagern, die von allen REST-Methoden verwendet wird.</p>
	 * 
	 * <h4>Vorteil:</h4>
	 * <ul>
	 *   <li>Token-Handling ist an einer Stelle zentralisiert</li>
	 *   <li>Alle REST-Methoden nutzen die gleiche Logik</li>
	 *   <li>Wartbarkeit: Änderungen nur an einer Stelle nötig</li>
	 * </ul>
	 * 
	 * <h4>Verwendung:</h4>
	 * <pre>
	 * Response response = executeWithAuth(webTarget, webTarget -&gt; webTarget.request().get());
	 * </pre>
	 * 
	 * @param id TaskGroup-ID
	 * @return TaskGroupBean oder null bei 404
	 * @throws TechnicalException bei Fehlern
	 */
	public TaskGroupBean findTaskGroupById_WithHelper(Long id) throws TechnicalException
	{
		String baseURL = "http://localhost:9080/jeeeraaah-backend";
		WebTarget webTarget = client.target(baseURL)
				.path("taskgroup")
				.path("by-id")
				.resolveTemplate("id", id);

		// ============================================================================================================
		// Verwendung der Hilfsmethode: Gesamte Request-Logik mit Token-Handling
		// ============================================================================================================
		Response response = executeWithAuth(
				webTarget,
				// Lambda-Funktion: Definiert, wie der Request ausgeführt wird
				// Der Parameter ist bereits ein Invocation.Builder (nicht WebTarget)
				builder -> builder.get()
		);

		int status = response.getStatus();

		if (status == 200)
		{
			TaskGroupDTO dto = response.readEntity(TaskGroupDTO.class);
			return toBean(dto, new ReferenceCycleTracking());
		}
		else if (status == 404)
		{
			return null;
		}
		else
		{
			throw new TechnicalException("Unexpected status: " + status);
		}
	}

	/**
	 * <h3>Wiederverwendbare Hilfsmethode: Request mit automatischem Token-Refresh</h3>
	 * 
	 * <p>Diese Methode kapselt die komplette Token-Handling-Logik:</p>
	 * <ol>
	 *   <li>Request mit aktuellem Token ausführen</li>
	 *   <li>Bei 401: Token refreshen und Request wiederholen</li>
	 *   <li>Bei Refresh-Fehler: Logout und Exception werfen</li>
	 * </ol>
	 * 
	 * <h4>Functional Interface RequestExecutor:</h4>
	 * <p>Ermöglicht flexible Request-Definition (GET, POST, PUT, DELETE):</p>
	 * <pre>
	 * // GET:
	 * executeWithAuth(webTarget, t -&gt; t.request().get())
	 * 
	 * // POST:
	 * executeWithAuth(webTarget, t -&gt; t.request().post(entity(dto, APPLICATION_JSON)))
	 * 
	 * // PUT:
	 * executeWithAuth(webTarget, t -&gt; t.request().put(entity(dto, APPLICATION_JSON)))
	 * 
	 * // DELETE:
	 * executeWithAuth(webTarget, t -&gt; t.request().delete())
	 * </pre>
	 * 
	 * @param webTarget JAX-RS WebTarget (URL + Path-Parameter)
	 * @param requestExecutor Lambda-Funktion, die den Request ausführt
	 * @return HTTP Response
	 * @throws TechnicalException bei Fehlern
	 */
	private Response executeWithAuth(WebTarget webTarget, RequestExecutor requestExecutor) throws TechnicalException
	{
		try
		{
			// ========================================================================================================
			// SCHRITT 1: Request mit aktuellem Token ausführen
			// ========================================================================================================
			Response response = requestExecutor.execute(
					webTarget.request().header("Authorization", "Bearer " + authService.getAccessToken())
			);

			// ========================================================================================================
			// SCHRITT 2: Bei 401 → Token-Refresh und Retry
			// ========================================================================================================
			if (response.getStatus() == 401)
			{
				log.debug("Token expired (401), refreshing and retrying...");
				response.close(); // Wichtig: Ersten Response schließen

				try
				{
					String newToken = authService.refreshAccessToken();
					log.debug("Token refreshed successfully");

					// Request mit neuem Token wiederholen
					response = requestExecutor.execute(
							webTarget.request().header("Authorization", "Bearer " + newToken)
					);

					// Wenn auch nach Refresh noch 401 → Re-Login erforderlich
					if (response.getStatus() == 401)
					{
						log.error("Authentication failed even after refresh. Re-login required.");
						authService.logout();
						throw new TechnicalException("Re-login required");
					}
				}
				catch (Exception e)
				{
					log.error("Token refresh failed", e);
					authService.logout();
					throw new TechnicalException("Re-login required", e);
				}
			}

			return response;
		}
		catch (ProcessingException e)
		{
			throw new TechnicalException("Communication error", e);
		}
	}

	/**
	 * Functional Interface für flexible Request-Ausführung.
	 * 
	 * <p>Ermöglicht es, verschiedene HTTP-Methoden (GET, POST, PUT, DELETE) über
	 * eine einheitliche Schnittstelle zu verwenden.</p>
	 */
	@FunctionalInterface
	private interface RequestExecutor
	{
		/**
		 * Führt einen HTTP-Request aus.
		 * 
		 * @param requestBuilder JAX-RS Invocation.Builder mit bereits gesetzten Headern
		 * @return HTTP Response
		 */
		Response execute(jakarta.ws.rs.client.Invocation.Builder requestBuilder);
	}

	// ================================================================================================================
	// ZUSAMMENFASSUNG & EMPFEHLUNGEN
	// ================================================================================================================

	/**
	 * <h2>Zusammenfassung: Was muss in TaskGroupServiceClient / TaskServiceClient geändert werden?</h2>
	 * 
	 * <h3>1. KeycloakAuthService injizieren:</h3>
	 * <pre>
	 * {@literal @}Inject
	 * private KeycloakAuthService authService;
	 * </pre>
	 * 
	 * <h3>2. Authorization-Header in allen Requests hinzufügen:</h3>
	 * <pre>
	 * // Vorher:
	 * Response response = webTarget.request().get();
	 * 
	 * // Nachher:
	 * Response response = webTarget.request()
	 *     .header("Authorization", "Bearer " + authService.getAccessToken())
	 *     .get();
	 * </pre>
	 * 
	 * <h3>3. 401 Unauthorized behandeln (Token-Refresh):</h3>
	 * <pre>
	 * if (response.getStatus() == 401) {
	 *     authService.refreshAccessToken();
	 *     // Request wiederholen mit neuem Token
	 * }
	 * </pre>
	 * 
	 * <h3>4. Alternative: Wiederverwendbare executeWithAuth()-Methode verwenden</h3>
	 * <p>Siehe Beispiel 3 für zentralisierte Token-Handling-Logik.</p>
	 * 
	 * <h3>5. Optional: ClientRequestFilter für automatisches Token-Injection</h3>
	 * <p>Statt in jeder Methode {@code .header("Authorization", ...)} zu schreiben, kann man
	 * einen JAX-RS Filter erstellen, der den Header automatisch hinzufügt:</p>
	 * 
	 * <pre>
	 * {@literal @}Provider
	 * public class AuthorizationHeaderFilter implements ClientRequestFilter {
	 *     {@literal @}Inject
	 *     private KeycloakAuthService authService;
	 *     
	 *     {@literal @}Override
	 *     public void filter(ClientRequestContext requestContext) {
	 *         if (authService.isLoggedIn()) {
	 *             requestContext.getHeaders().add(
	 *                 "Authorization",
	 *                 "Bearer " + authService.getAccessToken()
	 *             );
	 *         }
	 *     }
	 * }
	 * 
	 * // Registrieren im ClientBuilder:
	 * client = ClientBuilder.newBuilder()
	 *     .register(AuthorizationHeaderFilter.class)
	 *     .build();
	 * </pre>
	 * 
	 * <h3>Empfehlung für Ihr Projekt:</h3>
	 * <ol>
	 *   <li><strong>Kurzfristig:</strong> Beispiel 2 verwenden (manuelles Token-Refresh in jeder Methode)</li>
	 *   <li><strong>Mittelfristig:</strong> Beispiel 3 verwenden (executeWithAuth-Hilfsmethode)</li>
	 *   <li><strong>Langfristig:</strong> ClientRequestFilter implementieren (automatisches Token-Injection)</li>
	 * </ol>
	 */
	public void recommendations()
	{
		// Diese Methode dient nur als Dokumentation
	}
}
