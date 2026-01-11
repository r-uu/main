package de.ruu.lib.keycloak.admin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.ClientRepresentation;

import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

/**
 * Integration tests for KeycloakClientManager.
 * 
 * <p><strong>Prerequisites:</strong></p>
 * <ul>
 *   <li>Keycloak must be running on localhost:8080</li>
 *   <li>Admin credentials: admin / admin</li>
 *   <li>Realm "jeeeraaah-realm" must exist</li>
 * </ul>
 * 
 * <p><strong>Note:</strong> These tests are disabled by default. Remove @Disabled
 * annotation and ensure Keycloak is running to execute them.</p>
 * 
 * @author r-uu
 * @since 2025-12-27
 */
@Slf4j
@Disabled("Requires Keycloak running on localhost:8080")
class KeycloakClientManagerIT
{
	private KeycloakClientManager manager;
	private String createdClientUuid;

	@BeforeEach
	void setUp()
	{
		manager = KeycloakClientManager.builder()
				.serverUrl("http://localhost:8080")
				.realm("jeeeraaah-realm")
				.adminUsername("admin")
				.adminPassword("admin")
				.build();
	}

	@AfterEach
	void tearDown() throws KeycloakAdminException
	{
		// Cleanup: delete created client if exists
		if (createdClientUuid != null)
		{
			try
			{
				manager.deleteClient(createdClientUuid);
				log.info("Cleaned up test client: {}", createdClientUuid);
			}
			catch (Exception e)
			{
				log.warn("Failed to cleanup test client: {}", createdClientUuid, e);
			}
		}

		if (manager != null)
		{
			manager.close();
		}
	}

	@Test
	void testCreatePublicClient() throws KeycloakAdminException
	{
		String clientId = "test-public-client-" + System.currentTimeMillis();

		createdClientUuid = manager.createPublicClient(
				clientId,
				Arrays.asList("http://localhost:3000/*"),
				Arrays.asList("http://localhost:3000")
		);

		assertNotNull(createdClientUuid);
		assertFalse(createdClientUuid.isEmpty());

		// Verify client exists
		ClientRepresentation client = manager.findClientByClientId(clientId);
		assertNotNull(client);
		assertEquals(clientId, client.getClientId());
		assertTrue(client.isPublicClient());
		assertTrue(client.isDirectAccessGrantsEnabled());
	}

	@Test
	void testCreateConfidentialClient() throws KeycloakAdminException
	{
		String clientId = "test-confidential-client-" + System.currentTimeMillis();

		createdClientUuid = manager.createConfidentialClient(
				clientId,
				Arrays.asList("http://localhost:8080/callback"),
				true
		);

		assertNotNull(createdClientUuid);

		// Verify client exists
		ClientRepresentation client = manager.findClientByClientId(clientId);
		assertNotNull(client);
		assertEquals(clientId, client.getClientId());
		assertFalse(client.isPublicClient());
		assertTrue(client.isServiceAccountsEnabled());

		// Get client secret
		String secret = manager.getClientSecret(createdClientUuid);
		assertNotNull(secret);
		assertFalse(secret.isEmpty());
	}

	@Test
	void testCreateBearerOnlyClient() throws KeycloakAdminException
	{
		String clientId = "test-bearer-client-" + System.currentTimeMillis();

		createdClientUuid = manager.createBearerOnlyClient(clientId);

		assertNotNull(createdClientUuid);

		// Verify client exists
		ClientRepresentation client = manager.findClientByClientId(clientId);
		assertNotNull(client);
		assertEquals(clientId, client.getClientId());
		assertTrue(client.isBearerOnly());
	}

	@Test
	void testSetDirectAccessGrantsEnabled() throws KeycloakAdminException
	{
		String clientId = "test-client-" + System.currentTimeMillis();

		createdClientUuid = manager.createPublicClient(
				clientId,
				Arrays.asList("http://localhost:3000/*"),
				Arrays.asList("http://localhost:3000")
		);

		// Disable direct access grants
		manager.setDirectAccessGrantsEnabled(createdClientUuid, false);

		// Verify
		ClientRepresentation client = manager.findClientByClientId(clientId);
		assertFalse(client.isDirectAccessGrantsEnabled());

		// Re-enable
		manager.setDirectAccessGrantsEnabled(createdClientUuid, true);

		// Verify
		client = manager.findClientByClientId(clientId);
		assertTrue(client.isDirectAccessGrantsEnabled());
	}

	@Test
	void testDeleteClient() throws KeycloakAdminException
	{
		String clientId = "test-delete-client-" + System.currentTimeMillis();

		String clientUuid = manager.createPublicClient(
				clientId,
				Arrays.asList("http://localhost:3000/*"),
				Arrays.asList("http://localhost:3000")
		);

		// Delete client
		manager.deleteClient(clientUuid);
		createdClientUuid = null; // Prevent cleanup attempt

		// Verify client no longer exists
		ClientRepresentation client = manager.findClientByClientId(clientId);
		assertNull(client);
	}

	@Test
	void testFindNonExistentClient()
	{
		ClientRepresentation client = manager.findClientByClientId("this-client-does-not-exist-12345");
		assertNull(client);
	}
}
