package de.ruu.lib.util.config.mp;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link ConfigFileInitializer}.
 *
 * <p>
 * Tests cover:
 * <ul>
 * <li>Creating new config files with default values</li>
 * <li>Preserving existing values when adding new defaults</li>
 * <li>Postgres-specific utility methods</li>
 * <li>Ensuring required properties exist</li>
 * </ul>
 */
@Slf4j
class ConfigFileInitializerTest
{
	@TempDir
	Path tempDir;

	@AfterEach
	void cleanup()
	{
		System.clearProperty(WritableFileConfigSource.CONFIG_FILE_NAME_KEY);
	}

	/**
	 * Tests creating a new config file with custom default values.
	 */
	@Test
	void testInitializeConfigFileCreatesNewFile()
	{
		File configFile = tempDir.resolve("test.config").toFile();

		Map<String, String> defaults = new HashMap<>();
		defaults.put("app.name", "TestApp");
		defaults.put("app.version", "1.0.0");
		defaults.put("app.debug", "false");

		WritableFileConfigSource config = ConfigFileInitializer.initializeConfigFile(configFile.getAbsolutePath(),
				defaults);

		assertThat("Config file should be created", configFile.exists(), is(true));
		assertThat("Should have all default properties", config.getPropertyNames(), hasSize(3));
		assertThat("Should have correct value for app.name", config.getValue("app.name"), is("TestApp"));
		assertThat("Should have correct value for app.version", config.getValue("app.version"), is("1.0.0"));
		assertThat("Should have correct value for app.debug", config.getValue("app.debug"), is("false"));
	}

	/**
	 * Tests that existing values are preserved when initializing.
	 */
	@Test
	void testInitializeConfigFilePreservesExistingValues()
	{
		File configFile = tempDir.resolve("test.config").toFile();

		// First, create a config with some values
		Map<String, String> initialDefaults = new HashMap<>();
		initialDefaults.put("app.name", "InitialName");
		initialDefaults.put("app.version", "1.0.0");

		WritableFileConfigSource config1 = ConfigFileInitializer.initializeConfigFile(configFile.getAbsolutePath(),
				initialDefaults);

		// Modify one value
		config1.setProperty("app.name", "ModifiedName");

		// Now initialize again with different defaults
		Map<String, String> newDefaults = new HashMap<>();
		newDefaults.put("app.name", "NewDefaultName"); // Should NOT overwrite
		newDefaults.put("app.version", "2.0.0"); // Should NOT overwrite
		newDefaults.put("app.debug", "true"); // Should be added

		WritableFileConfigSource config2 = ConfigFileInitializer.initializeConfigFile(configFile.getAbsolutePath(),
				newDefaults);

		assertThat("Existing modified value should be preserved", config2.getValue("app.name"), is("ModifiedName"));
		assertThat("Existing value should be preserved", config2.getValue("app.version"), is("1.0.0"));
		assertThat("New property should be added", config2.getValue("app.debug"), is("true"));
	}

	/**
	 * Tests ensuring required properties exist.
	 */
	@Test
	void testEnsureRequiredProperties()
	{
		File configFile = tempDir.resolve("test.config").toFile();

		// Create initial config with some properties
		Map<String, String> initialDefaults = new HashMap<>();
		initialDefaults.put("prop.a", "valueA");
		initialDefaults.put("prop.b", "valueB");

		WritableFileConfigSource config = ConfigFileInitializer.initializeConfigFile(configFile.getAbsolutePath(),
				initialDefaults);

		// Define required properties (some exist, some don't)
		Map<String, String> requiredDefaults = new HashMap<>();
		requiredDefaults.put("prop.a", "defaultA"); // Already exists
		requiredDefaults.put("prop.c", "valueC"); // New
		requiredDefaults.put("prop.d", "valueD"); // New

		int addedCount = ConfigFileInitializer.ensureRequiredProperties(config, requiredDefaults);

		assertThat("Should have added 2 properties", addedCount, is(2));
		assertThat("Should preserve existing value", config.getValue("prop.a"), is("valueA"));
		assertThat("Should have added prop.c", config.getValue("prop.c"), is("valueC"));
		assertThat("Should have added prop.d", config.getValue("prop.d"), is("valueD"));
		assertThat("Should have 4 properties total", config.getPropertyNames(), hasSize(4));
	}

	/**
	 * Tests ensuring required properties when all already exist.
	 */
	@Test
	void testEnsureRequiredPropertiesAllExist()
	{
		File configFile = tempDir.resolve("test.config").toFile();

		Map<String, String> initialDefaults = new HashMap<>();
		initialDefaults.put("prop.a", "valueA");
		initialDefaults.put("prop.b", "valueB");

		WritableFileConfigSource config = ConfigFileInitializer.initializeConfigFile(configFile.getAbsolutePath(),
				initialDefaults);

		// All required properties already exist
		Map<String, String> requiredDefaults = new HashMap<>();
		requiredDefaults.put("prop.a", "defaultA");
		requiredDefaults.put("prop.b", "defaultB");

		int addedCount = ConfigFileInitializer.ensureRequiredProperties(config, requiredDefaults);

		assertThat("Should not have added any properties", addedCount, is(0));
		assertThat("Should still have 2 properties", config.getPropertyNames(), hasSize(2));
	}

	/**
	 * Tests initialization with empty defaults map.
	 */
	@Test
	void testInitializeConfigFileWithEmptyDefaults()
	{
		File configFile = tempDir.resolve("test.config").toFile();

		Map<String, String> emptyDefaults = new HashMap<>();

		WritableFileConfigSource config = ConfigFileInitializer.initializeConfigFile(configFile.getAbsolutePath(),
				emptyDefaults);

		assertThat("Config file should be created even with empty defaults", configFile.exists(), is(true));
		assertThat("Should have no properties", config.getPropertyNames(), hasSize(0));
	}
}
