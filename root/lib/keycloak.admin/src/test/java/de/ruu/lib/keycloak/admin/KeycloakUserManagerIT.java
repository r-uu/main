package de.ruu.lib.keycloak.admin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;

import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for KeycloakUserManager.
 * 
 * <p><strong>Prerequisites:</strong></p>
 * <ul>
 *   <li>Keycloak must be running on localhost:8080</li>
 *   <li>Admin credentials: admin / admin</li>
 *   <li>Realm "jeeeraaah-realm" must exist</li>
 *   <li>Role "task-admin" must exist in the realm</li>
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
class KeycloakUserManagerIT
{
	private KeycloakUserManager manager;
	private String createdUserId;

	@BeforeEach
	void setUp()
	{
		manager = KeycloakUserManager.builder()
				.serverUrl("http://localhost:8080")
				.realm("jeeeraaah-realm")
				.adminUsername("admin")
				.adminPassword("admin")
				.build();
	}

	@AfterEach
	void tearDown() throws KeycloakAdminException
	{
		// Cleanup: delete created user if exists
		if (createdUserId != null)
		{
			try
			{
				manager.deleteUser(createdUserId);
				log.info("Cleaned up test user: {}", createdUserId);
			}
			catch (Exception e)
			{
				log.warn("Failed to cleanup test user: {}", createdUserId, e);
			}
		}

		if (manager != null)
		{
			manager.close();
		}
	}

	@Test
	void testCreateUser() throws KeycloakAdminException
	{
		String username = "testuser-" + System.currentTimeMillis();
		String email = username + "@example.com";

		createdUserId = manager.createUser(username, email);

		assertNotNull(createdUserId);
		assertFalse(createdUserId.isEmpty());

		// Verify user exists
		UserRepresentation user = manager.findUserByUsername(username);
		assertNotNull(user);
		assertEquals(username, user.getUsername());
		assertEquals(email, user.getEmail());
		assertTrue(user.isEnabled());
		assertTrue(user.isEmailVerified());
	}

	@Test
	void testCreateUserWithPasswordAndRoles() throws KeycloakAdminException
	{
		String username = "testuser-" + System.currentTimeMillis();
		String email = username + "@example.com";
		String password = "test-password";

		createdUserId = manager.createUser(username, email, password, "task-admin");

		assertNotNull(createdUserId);

		// Verify user exists
		UserRepresentation user = manager.findUserByUsername(username);
		assertNotNull(user);
		assertEquals(username, user.getUsername());
	}

	@Test
	void testSetPassword() throws KeycloakAdminException
	{
		String username = "testuser-" + System.currentTimeMillis();
		String email = username + "@example.com";

		createdUserId = manager.createUser(username, email);

		// Should not throw exception
		assertDoesNotThrow(() -> manager.setPassword(createdUserId, "new-password", false));
	}

	@Test
	void testDeleteUser() throws KeycloakAdminException
	{
		String username = "testuser-" + System.currentTimeMillis();
		String email = username + "@example.com";

		String userId = manager.createUser(username, email);

		// Delete user
		manager.deleteUser(userId);
		createdUserId = null; // Prevent cleanup attempt

		// Verify user no longer exists
		UserRepresentation user = manager.findUserByUsername(username);
		assertNull(user);
	}

	@Test
	void testFindNonExistentUser()
	{
		UserRepresentation user = manager.findUserByUsername("this-user-does-not-exist-12345");
		assertNull(user);
	}
}
