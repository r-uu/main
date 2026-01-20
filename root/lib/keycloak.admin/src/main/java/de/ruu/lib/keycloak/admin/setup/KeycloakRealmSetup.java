package de.ruu.lib.keycloak.admin.setup;

import de.ruu.lib.keycloak.admin.KeycloakAdminException;
import de.ruu.lib.keycloak.admin.KeycloakClientManager;
import de.ruu.lib.keycloak.admin.KeycloakRealmManager;
import de.ruu.lib.keycloak.admin.KeycloakUserManager;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import jakarta.ws.rs.core.Response;
import java.util.Arrays;

/**
 * Keycloak Realm Setup Utility
 *
 * <p>Erstellt automatisch den benötigten Keycloak Realm mit Client und Testuser
 * für die JEEERAaH Anwendung.</p>
 *
 * <h2>Erstellt folgende Ressourcen:</h2>
 * <ul>
 *   <li>Realm: realm_default</li>
 *   <li>Client: jeeeraaah-frontend (Public Client, Direct Access Grants)</li>
 *   <li>User: r_uu / r_uu_password</li>
 * </ul>
 *
 * <h2>Voraussetzungen:</h2>
 * <ul>
 *   <li>Keycloak Server läuft auf http://localhost:8080</li>
 *   <li>Admin Credentials: admin / admin</li>
 * </ul>
 *
 * @author r-uu
 * @since 2026-01-19
 */
@Slf4j
public class KeycloakRealmSetup
{
	private static final String KEYCLOAK_URL = System.getProperty("keycloak.url", "http://localhost:8080");
	private static final String ADMIN_USER = System.getProperty("keycloak.admin.user", "admin");
	private static final String ADMIN_PASSWORD = System.getProperty("keycloak.admin.password",
			System.getenv().getOrDefault("KEYCLOAK_ADMIN_PASSWORD", "changeme_in_local_env"));
	private static final String REALM_NAME = System.getProperty("keycloak.realm", "realm_default");
	private static final String CLIENT_ID = System.getProperty("keycloak.client.id", "jeeeraaah-frontend");
	private static final String TEST_USER = System.getProperty("keycloak.test.user", "r_uu");
	private static final String TEST_PASSWORD = System.getProperty("keycloak.test.password", "r_uu_password");

	public static void main(String[] args)
	{
		log.info("=== Keycloak Realm Setup ===");
		log.info("Keycloak URL: {}", KEYCLOAK_URL);
		log.info("Realm: {}", REALM_NAME);
		log.info("Client: {}", CLIENT_ID);
		log.info("");

		try (Keycloak keycloak = createKeycloakClient())
		{
			// 1. Create Realm
			createRealm(keycloak);

			// 2. Create Client
			createClient(keycloak);

			// 3. Create Test User
			createTestUser(keycloak);

			log.info("");
			log.info("=== Setup abgeschlossen ===");
			log.info("✅ Realm: {}", REALM_NAME);
			log.info("✅ Client: {} (Public Client, Direct Access Grants aktiviert)", CLIENT_ID);
			log.info("✅ Test User: {} / {}", TEST_USER, TEST_PASSWORD);
			log.info("");
			log.info("Test-Login-Command:");
			log.info("curl -X POST '{}/realms/{}/protocol/openid-connect/token' \\", KEYCLOAK_URL, REALM_NAME);
			log.info("  -H 'Content-Type: application/x-www-form-urlencoded' \\");
			log.info("  -d 'username={}' \\", TEST_USER);
			log.info("  -d 'password={}' \\", TEST_PASSWORD);
			log.info("  -d 'grant_type=password' \\");
			log.info("  -d 'client_id={}'", CLIENT_ID);
		}
		catch (Exception e)
		{
			log.error("❌ Setup fehlgeschlagen: {}", e.getMessage(), e);
			System.exit(1);
		}
	}

	private static Keycloak createKeycloakClient()
	{
		log.info("Verbinde mit Keycloak Server...");
		return KeycloakBuilder.builder()
				.serverUrl(KEYCLOAK_URL)
				.realm("master")
				.username(ADMIN_USER)
				.password(ADMIN_PASSWORD)
				.clientId("admin-cli")
				.build();
	}

	private static void createRealm(Keycloak keycloak)
	{
		log.info("Prüfe Realm '{}'...", REALM_NAME);

		try
		{
			// Prüfe ob Realm bereits existiert
			keycloak.realm(REALM_NAME).toRepresentation();
			log.info("✓ Realm '{}' existiert bereits", REALM_NAME);
		}
		catch (Exception e)
		{
			log.info("Erstelle Realm '{}'...", REALM_NAME);

			RealmRepresentation realm = new RealmRepresentation();
			realm.setRealm(REALM_NAME);
			realm.setEnabled(true);
			realm.setDisplayName("JEEERAaH Default Realm");
			realm.setRegistrationAllowed(false);
			realm.setResetPasswordAllowed(true);

			try
			{
				keycloak.realms().create(realm);
				log.info("✅ Realm '{}' erfolgreich erstellt", REALM_NAME);
			}
			catch (Exception ex)
			{
				log.error("Fehler beim Erstellen des Realms '{}'", REALM_NAME, ex);
				throw new RuntimeException("Failed to create realm: " + REALM_NAME, ex);
			}
		}
	}

	private static void createClient(Keycloak keycloak) throws KeycloakAdminException
	{
		log.info("Prüfe Client '{}'...", CLIENT_ID);

		try (KeycloakClientManager clientManager = KeycloakClientManager.builder()
				.serverUrl(KEYCLOAK_URL)
				.realm(REALM_NAME)
				.adminUsername(ADMIN_USER)
				.adminPassword(ADMIN_PASSWORD)
				.build())
		{
			// Prüfe ob Client existiert
			org.keycloak.representations.idm.ClientRepresentation existingClient =
				clientManager.findClientByClientId(CLIENT_ID);

			if (existingClient != null)
			{
				String clientUuid = existingClient.getId();
				log.info("✓ Client '{}' existiert bereits (UUID: {})", CLIENT_ID, clientUuid);

				// Stelle sicher, dass Direct Access Grants aktiviert ist
				try
				{
					org.keycloak.representations.idm.ClientRepresentation client =
						keycloak.realm(REALM_NAME).clients().get(clientUuid).toRepresentation();

					if (!Boolean.TRUE.equals(client.isDirectAccessGrantsEnabled()))
					{
						log.info("Aktiviere Direct Access Grants für Client '{}'...", CLIENT_ID);
						client.setDirectAccessGrantsEnabled(true);
						client.setPublicClient(true);
						keycloak.realm(REALM_NAME).clients().get(clientUuid).update(client);
						log.info("✅ Direct Access Grants aktiviert");
					}
					else
					{
						log.info("✓ Direct Access Grants bereits aktiviert");
					}
				}
				catch (Exception ex)
				{
					log.warn("Konnte Direct Access Grants nicht prüfen/setzen: {}", ex.getMessage());
				}
			}
			else
			{
				// Client existiert nicht, erstelle ihn
				log.info("Erstelle Client '{}'...", CLIENT_ID);

				String clientUuid = clientManager.createPublicClient(
						CLIENT_ID,
						Arrays.asList("*"),  // redirectUris
						Arrays.asList("*")   // webOrigins
				);

				log.info("✅ Client '{}' erstellt (UUID: {})", CLIENT_ID, clientUuid);

				// Direct Access Grants explizit aktivieren
				try
				{
					org.keycloak.representations.idm.ClientRepresentation client =
						keycloak.realm(REALM_NAME).clients().get(clientUuid).toRepresentation();
					client.setDirectAccessGrantsEnabled(true);
					client.setPublicClient(true);
					keycloak.realm(REALM_NAME).clients().get(clientUuid).update(client);
					log.info("✅ Direct Access Grants aktiviert für Client '{}'", CLIENT_ID);
				}
				catch (Exception ex)
				{
					log.error("FEHLER: Konnte Direct Access Grants nicht aktivieren: {}", ex.getMessage());
					throw new KeycloakAdminException("Direct Access Grants konnte nicht aktiviert werden", ex);
				}
			}
		}
	}

	private static void createTestUser(Keycloak keycloak) throws KeycloakAdminException
	{
		log.info("Prüfe Testuser '{}'...", TEST_USER);

		try (KeycloakUserManager userManager = KeycloakUserManager.builder()
				.serverUrl(KEYCLOAK_URL)
				.realm(REALM_NAME)
				.adminUsername(ADMIN_USER)
				.adminPassword(ADMIN_PASSWORD)
				.build())
		{
			UserRepresentation existingUser = userManager.findUserByUsername(TEST_USER);

			if (existingUser != null)
			{
				// User existiert bereits
				String userId = existingUser.getId();
				log.info("✓ User '{}' existiert bereits (ID: {})", TEST_USER, userId);

				// Aktualisiere Passwort und lösche Required Actions
				log.info("Aktualisiere User-Konfiguration...");

				// Passwort direkt über Keycloak API setzen
				org.keycloak.representations.idm.CredentialRepresentation credential =
					new org.keycloak.representations.idm.CredentialRepresentation();
				credential.setType(org.keycloak.representations.idm.CredentialRepresentation.PASSWORD);
				credential.setValue(TEST_PASSWORD);
				credential.setTemporary(false);
				keycloak.realm(REALM_NAME).users().get(userId).resetPassword(credential);
				log.info("✅ Passwort für User '{}' gesetzt", TEST_USER);

				// Required Actions explizit löschen über Keycloak API
				try
				{
					UserRepresentation user = keycloak.realm(REALM_NAME).users().get(userId).toRepresentation();
					user.setRequiredActions(new java.util.ArrayList<>());  // Leere Liste
					user.setEmailVerified(true);
					user.setEnabled(true);
					user.setFirstName("Test");  // firstName ist erforderlich für Keycloak User Profile
					user.setLastName("User");   // lastName ist erforderlich für Keycloak User Profile
					keycloak.realm(REALM_NAME).users().get(userId).update(user);
					log.info("✅ User '{}' aktualisiert (Required Actions gelöscht)", TEST_USER);
				}
				catch (Exception ex)
				{
					log.warn("Warnung beim Aktualisieren des Users: {}", ex.getMessage());
				}
			}
			else
			{
				// User existiert nicht, erstelle ihn
				log.info("Erstelle Testuser '{}'...", TEST_USER);

				String userId = userManager.createUser(
						TEST_USER,
						TEST_USER + "@example.com",
						TEST_PASSWORD
						// Keine Rollen
				);

				log.info("✅ User '{}' erstellt (ID: {})", TEST_USER, userId);

				// Passwort nochmal explizit setzen (zur Sicherheit)
				org.keycloak.representations.idm.CredentialRepresentation credential =
					new org.keycloak.representations.idm.CredentialRepresentation();
				credential.setType(org.keycloak.representations.idm.CredentialRepresentation.PASSWORD);
				credential.setValue(TEST_PASSWORD);
				credential.setTemporary(false);
				keycloak.realm(REALM_NAME).users().get(userId).resetPassword(credential);
				log.info("✅ Passwort für User '{}' gesetzt", TEST_USER);

				// Required Actions explizit löschen
				try
				{
					UserRepresentation user = keycloak.realm(REALM_NAME).users().get(userId).toRepresentation();
					user.setRequiredActions(new java.util.ArrayList<>());  // Leere Liste
					user.setEmailVerified(true);
					user.setEnabled(true);
					user.setFirstName("Test");  // firstName ist erforderlich für Keycloak User Profile
					user.setLastName("User");   // lastName ist erforderlich für Keycloak User Profile
					keycloak.realm(REALM_NAME).users().get(userId).update(user);
					log.info("✅ Required Actions für User '{}' gelöscht", TEST_USER);
				}
				catch (Exception ex)
				{
					log.warn("Warnung beim Löschen der Required Actions: {}", ex.getMessage());
				}
			}
		}
	}
}
