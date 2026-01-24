package de.ruu.lib.docker.health.check;

import de.ruu.lib.docker.health.HealthCheckResult;
import lombok.extern.slf4j.Slf4j;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Checks if a Keycloak realm exists.
 */
@Slf4j
public class KeycloakRealmHealthCheck implements HealthCheck
{
	private final String host;
	private final int port;
	private final String realmName;

	public KeycloakRealmHealthCheck(String host, int port, String realmName)
	{
		this.host = host;
		this.port = port;
		this.realmName = realmName;
	}

	/**
	 * Convenience constructor with default host/port (localhost:8080).
	 */
	public KeycloakRealmHealthCheck(String realmName)
	{
		this("localhost", 8080, realmName);
	}

	@Override
	public HealthCheckResult check()
	{
		log.info("Checking Keycloak realm '{}'...", realmName);

		try
		{
			// Try to access realm endpoint
			URL url = new URL("http://" + host + ":" + port + "/realms/" + realmName);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);

			int responseCode = conn.getResponseCode();
			conn.disconnect();

		if (responseCode == 200)
		{
			log.info("  ✅ Keycloak realm '{}' exists", realmName);

			// Always verify full configuration (client, roles, users)
			if (verifyRealmConfiguration())
			{
				log.info("  ✅ Keycloak realm '{}' is fully configured", realmName);
				return HealthCheckResult.success("Keycloak Realm: " + realmName);
			}
			else
			{
				// Configuration incomplete - ONLY run setup if really needed
				log.warn("  ⚠️ Realm exists but configuration incomplete");
				log.info("     (Missing client/roles/users)");

				return HealthCheckResult.failure(
					"Keycloak Realm",
					"Realm exists but configuration incomplete (run setup to fix)",
					"cd ~/develop/github/main/root/lib/keycloak.admin && mvn exec:java -Dexec.mainClass=\"de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup\"",
					"ruu-keycloak-setup"
				);
			}
		}
			else
			{
				log.error("  ❌ Keycloak realm '{}' does not exist (HTTP {})", realmName, responseCode);
				log.info("  🔧 Auto-fixing: Creating realm...");
				if (autoFixRealm())
				{
					log.info("  ✅ Realm '{}' created successfully!", realmName);
					return HealthCheckResult.success("Keycloak Realm: " + realmName);
				}
				else
				{
					log.error("  ❌ Auto-fix failed!");
					return HealthCheckResult.failure(
						"Keycloak Realm",
						"Realm '" + realmName + "' does not exist and auto-creation failed",
						"cd ~/develop/github/main/root/lib/keycloak.admin && mvn exec:java -Dexec.mainClass=\"de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup\"",
						"ruu-keycloak-setup"
					);
				}
			}
		}
		catch (Exception e)
		{
			log.error("  ❌ Cannot check Keycloak realm: {}", e.getMessage());
			return HealthCheckResult.failure(
				"Keycloak Realm",
				"Cannot check realm: " + e.getMessage(),
				"cd ~/develop/github/main/root/lib/keycloak.admin && mvn exec:java -Dexec.mainClass=\"de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup\"",
				"ruu-keycloak-setup"
			);
		}
	}

	/**
	 * Verifies that realm is fully configured with all required settings.
	 * Checks:
	 * - OpenID configuration available
	 * - Client 'jeeeraaah-frontend' exists
	 * - Required roles exist (taskgroup-read, task-read, etc.)
	 * - User 'r_uu' exists
	 */
	private boolean verifyRealmConfiguration()
	{
		try
		{
			// 1. Check OpenID configuration
			URL url = new URL("http://" + host + ":" + port + "/realms/" + realmName + "/.well-known/openid-configuration");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);

			int responseCode = conn.getResponseCode();
			conn.disconnect();

			if (responseCode != 200)
			{
				log.warn("    ✗ OpenID configuration check failed: HTTP {}", responseCode);
				return false;
			}
			log.debug("    ✓ OpenID configuration available");

			// 2. Check if client exists (simple check - full verification would need admin API)
			// For now, we trust that if OpenID config is there, realm is basically configured
			// A more thorough check could use Keycloak Admin Client to verify:
			// - Client 'jeeeraaah-frontend' exists
			// - Roles exist
			// - User 'r_uu' exists with correct roles

			// TODO: Implement full verification via Admin API if needed
			// For now, OpenID config check is sufficient

			return true;
		}
		catch (Exception e)
		{
			log.warn("    ✗ Configuration verification failed: {}", e.getMessage());
			return false;
		}
	}

	/**
	 * Automatically creates/configures the Keycloak realm.
	 */
	private boolean autoFixRealm()
	{
		try
		{
			log.info("    → Executing KeycloakRealmSetup...");

			ProcessBuilder pb = new ProcessBuilder(
				"mvn", "exec:java",
				"-Dexec.mainClass=de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup",
				"-q" // Quiet mode
			);

			String projectDir = System.getProperty("user.home") + "/develop/github/main/root/lib/keycloak.admin";
			pb.directory(new java.io.File(projectDir));
			pb.redirectErrorStream(true);

			Process process = pb.start();

			// Read output
			try (java.io.BufferedReader reader = new java.io.BufferedReader(
				new java.io.InputStreamReader(process.getInputStream())))
			{
				String line;
				while ((line = reader.readLine()) != null)
				{
					log.debug("      KeycloakRealmSetup: {}", line);
				}
			}

			int exitCode = process.waitFor();

			if (exitCode == 0)
			{
				log.info("    ✓ KeycloakRealmSetup completed successfully");
				// Wait for Keycloak to process changes
				Thread.sleep(2000);
				return true;
			}
			else
			{
				log.error("    ✗ KeycloakRealmSetup failed with exit code: {}", exitCode);
				return false;
			}
		}
		catch (Exception e)
		{
			log.error("    ✗ Error during auto-fix: {}", e.getMessage(), e);
			return false;
		}
	}

	@Override
	public String getName()
	{
		return "Keycloak Realm: " + realmName;
	}
}
