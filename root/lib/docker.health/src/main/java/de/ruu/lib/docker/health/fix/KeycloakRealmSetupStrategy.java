package de.ruu.lib.docker.health.fix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Auto-fix strategy for setting up missing Keycloak realm.
 *
 * <p>Executes Maven command to run KeycloakRealmSetup Java application.</p>
 */
public class KeycloakRealmSetupStrategy implements AutoFixStrategy
{
	private static final Logger log = LoggerFactory.getLogger(KeycloakRealmSetupStrategy.class);

	private final String projectDir;

	/**
	 * Creates strategy with default project directory.
	 */
	public KeycloakRealmSetupStrategy()
	{
		this("~/develop/github/main/root/lib/keycloak.admin");
	}

	/**
	 * Creates strategy with custom project directory.
	 *
	 * @param projectDir directory containing keycloak.admin module
	 */
	public KeycloakRealmSetupStrategy(String projectDir)
	{
		this.projectDir = projectDir;
	}

	@Override
	public boolean canHandle(String serviceName)
	{
		return "Keycloak Realm".equals(serviceName);
	}

	@Override
	public boolean fix(String serviceName)
	{
		try
		{
			log.info("Setting up Keycloak realm...");

			String command = String.format(
				"cd %s && mvn -q exec:java -Dexec.mainClass=\"de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup\"",
				projectDir
			);

			ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
			pb.redirectErrorStream(true);

			Process process = pb.start();

			// Log output
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())))
			{
				reader.lines().forEach(line -> log.debug("realm-setup: {}", line));
			}

			int exitCode = process.waitFor();

			if (exitCode == 0)
			{
				log.info("✅ Keycloak realm setup completed successfully");
				return true;
			}
			else
			{
				log.error("❌ Keycloak realm setup failed (exit code: {})", exitCode);
				return false;
			}
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
			log.error("Interrupted during realm setup: {}", e.getMessage());
			return false;
		}
		catch (Exception e)
		{
			log.error("Failed to setup Keycloak realm: {}", e.getMessage());
			return false;
		}
	}

	@Override
	public String getDescription()
	{
		return "Sets up Keycloak realm using KeycloakRealmSetup";
	}
}
