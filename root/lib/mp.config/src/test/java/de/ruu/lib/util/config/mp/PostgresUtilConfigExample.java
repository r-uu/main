package de.ruu.lib.util.config.mp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

import java.io.File;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * Example demonstrating how to create a config file with default values using WritableFileConfigSource.
 *
 * <p>
 * This example shows:
 * <ul>
 * <li>How to create a new config file in the project root directory</li>
 * <li>How to populate it with default values</li>
 * <li>How to check if a config file already exists before creating it</li>
 * <li>How to initialize default values only if they don't exist</li>
 * </ul>
 */
@Slf4j
class PostgresUtilConfigExample
{
	/**
	 * Example: Creates a postgresutil.config file in the project root with default values.
	 *
	 * <p>
	 * This demonstrates the complete workflow:
	 * <ol>
	 * <li>Set the system property to point to the desired config file location</li>
	 * <li>Create a WritableFileConfigSource instance</li>
	 * <li>Populate it with default values using setProperty()</li>
	 * <li>The file is automatically created and saved</li>
	 * </ol>
	 */
	@Test
	void demonstrateCreatingConfigFileWithDefaults()
	{
		// Step 1: Define the config file path (project root directory)
		String configFilePath = "postgresutil.config.properties";

		// Step 2: Set the system property to tell WritableFileConfigSource where to store the file
		System.setProperty(WritableFileConfigSource.CONFIG_FILE_NAME_KEY, configFilePath);

		try
		{
			// Step 3: Create a new WritableFileConfigSource instance
			WritableFileConfigSource config = new WritableFileConfigSource();

			// Step 4: Set default values
			// These calls will automatically create the file and save the properties
			config.setProperty("postgres.host", "localhost");
			config.setProperty("postgres.port", "5432");
			config.setProperty("postgres.database", "mydb");
			config.setProperty("postgres.username", "admin");
			config.setProperty("postgres.password", "changeme");
			config.setProperty("postgres.schema", "public");
			config.setProperty("postgres.ssl.enabled", "false");
			config.setProperty("postgres.connection.timeout", "30000");
			config.setProperty("postgres.max.pool.size", "10");

			log.info("Config file created at: {}", new File(configFilePath).getAbsolutePath());
			log.info("Properties written: {}", config.getPropertyNames().size());

			// Verify the file was created and properties were saved
			File configFile = new File(configFilePath);
			assertThat("Config file should exist", configFile.exists(), is(true));
			assertThat("Should have 9 properties", config.getPropertyNames().size(), is(9));

			// Cleanup: Delete the test file
			configFile.delete();
		}
		finally
		{
			// Cleanup: Reset system property
			System.clearProperty(WritableFileConfigSource.CONFIG_FILE_NAME_KEY);
		}
	}

	/**
	 * Example: Safe initialization that doesn't overwrite existing config.
	 *
	 * <p>
	 * This shows how to:
	 * <ul>
	 * <li>Check if config values already exist</li>
	 * <li>Only set default values if they're not already present</li>
	 * <li>Preserve user modifications while ensuring all required properties exist</li>
	 * </ul>
	 */
	@Test
	void demonstrateSafeDefaultInitialization()
	{
		String configFilePath = "postgresutil.config.properties";
		System.setProperty(WritableFileConfigSource.CONFIG_FILE_NAME_KEY, configFilePath);

		try
		{
			WritableFileConfigSource config = new WritableFileConfigSource();

			// Helper method to set a property only if it doesn't exist
			setDefaultIfNotExists(config, "postgres.host", "localhost");
			setDefaultIfNotExists(config, "postgres.port", "5432");
			setDefaultIfNotExists(config, "postgres.database", "mydb");
			setDefaultIfNotExists(config, "postgres.username", "admin");
			setDefaultIfNotExists(config, "postgres.password", "changeme");
			setDefaultIfNotExists(config, "postgres.schema", "public");
			setDefaultIfNotExists(config, "postgres.ssl.enabled", "false");
			setDefaultIfNotExists(config, "postgres.connection.timeout", "30000");
			setDefaultIfNotExists(config, "postgres.max.pool.size", "10");

			log.info("Default properties initialized (existing values preserved)");

			// Verify
			assertThat("postgres.host should have default value", config.getValue("postgres.host"), is("localhost"));

			// Cleanup
			new File(configFilePath).delete();
		}
		finally
		{
			System.clearProperty(WritableFileConfigSource.CONFIG_FILE_NAME_KEY);
		}
	}

	/**
	 * Example: Creating config file programmatically in a utility method.
	 *
	 * <p>
	 * This shows a reusable pattern for initializing config files in your application.
	 */
	@Test
	void demonstrateUtilityMethodPattern()
	{
		String configFilePath = "postgresutil.config.properties";

		try
		{
			// Call a utility method that handles the entire setup
			WritableFileConfigSource config = initializePostgresUtilConfig(configFilePath);

			// Verify it was created with all defaults
			assertThat("Should have all default properties", config.getPropertyNames().size(), is(greaterThanOrEqualTo(9)));

			log.info("Config initialized using utility method");

			// Cleanup
			new File(configFilePath).delete();
		}
		finally
		{
			System.clearProperty(WritableFileConfigSource.CONFIG_FILE_NAME_KEY);
		}
	}

	/**
	 * Utility method: Initializes a WritableFileConfigSource with default Postgres values.
	 *
	 * @param configFilePath Path where the config file should be created
	 * @return Configured WritableFileConfigSource instance
	 */
	private WritableFileConfigSource initializePostgresUtilConfig(String configFilePath)
	{
		// Set system property for the config file location
		System.setProperty(WritableFileConfigSource.CONFIG_FILE_NAME_KEY, configFilePath);

		// Create config source
		WritableFileConfigSource config = new WritableFileConfigSource();

		// Initialize with default values (only if not already set)
		setDefaultIfNotExists(config, "postgres.host", "localhost");
		setDefaultIfNotExists(config, "postgres.port", "5432");
		setDefaultIfNotExists(config, "postgres.database", "mydb");
		setDefaultIfNotExists(config, "postgres.username", "admin");
		setDefaultIfNotExists(config, "postgres.password", "changeme");
		setDefaultIfNotExists(config, "postgres.schema", "public");
		setDefaultIfNotExists(config, "postgres.ssl.enabled", "false");
		setDefaultIfNotExists(config, "postgres.connection.timeout", "30000");
		setDefaultIfNotExists(config, "postgres.max.pool.size", "10");

		log.info("Postgres utility config initialized at: {}", new File(configFilePath).getAbsolutePath());

		return config;
	}

	/**
	 * Helper method: Sets a property only if it doesn't already have a value.
	 *
	 * <p>
	 * This is useful for setting default values without overwriting user customizations.
	 *
	 * @param config The config source
	 * @param key Property key
	 * @param defaultValue Default value to set if property doesn't exist
	 */
	private void setDefaultIfNotExists(WritableFileConfigSource config, String key, String defaultValue)
	{
		if (config.getValue(key) == null)
		{
			config.setProperty(key, defaultValue);
			log.debug("Set default value for {}: {}", key, defaultValue);
		}
		else
		{
			log.debug("Property {} already exists with value: {}", key, config.getValue(key));
		}
	}
}
