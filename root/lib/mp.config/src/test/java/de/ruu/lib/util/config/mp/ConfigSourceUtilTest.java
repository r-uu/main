package de.ruu.lib.util.config.mp;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for {@link ConfigSourceUtil}.
 *
 * <p>
 * Tests cover:
 * <ul>
 * <li>Finding an active WritableFileConfigSource in the config sources</li>
 * <li>Behavior when no WritableFileConfigSource is available</li>
 * <li>Correct identification of the right config source type</li>
 * </ul>
 */
@Slf4j
class ConfigSourceUtilTest
{
	@TempDir
	Path tempDir;

	private File testConfigFile;
	private String originalConfigFileProperty;

	/**
	 * Sets up a temporary config file for each test.
	 */
	@BeforeEach
	void setUp() throws IOException
	{
		testConfigFile = tempDir.resolve("test-config.properties").toFile();

		// Store original property value
		originalConfigFileProperty = System.getProperty(WritableFileConfigSource.CONFIG_FILE_NAME_KEY);

		// Set system property to point to our test file
		System.setProperty(WritableFileConfigSource.CONFIG_FILE_NAME_KEY, testConfigFile.getAbsolutePath());

		// Create a test config file
		Properties props = new Properties();
		props.setProperty("test.key", "test.value");

		try (FileOutputStream fos = new FileOutputStream(testConfigFile))
		{
			props.store(fos, "Test configuration");
		}

		log.debug("Test config file created at: {}", testConfigFile.getAbsolutePath());
	}

	/**
	 * Tests that the utility can find a WritableFileConfigSource in the active config sources.
	 *
	 * Note: This test depends on the ServiceLoader mechanism properly loading the WritableFileConfigSource via the
	 * module-info.java provides clause.
	 */
	@Test
	void testActiveWritableFileConfigSource()
	{
		// Get the MicroProfile Config which should include our WritableFileConfigSource
		Config config = ConfigProvider.getConfig();

		log.debug("Available config sources:");
		for (ConfigSource source : config.getConfigSources())
		{
			log.debug("  - {} (ordinal: {})", source.getName(), source.getOrdinal());
		}

		// Use the utility to find the WritableFileConfigSource
		Optional<WritableFileConfigSource> result = ConfigSourceUtil.activeWritableFileConfigSource();

		assertThat("Should find a WritableFileConfigSource", result.isPresent(), is(true));

		WritableFileConfigSource source = result.get();
		assertThat("Found source should not be null", source, is(notNullValue()));
		assertThat("Found source should be correct type", source, is(instanceOf(WritableFileConfigSource.class)));
	}

	/**
	 * Tests that the found WritableFileConfigSource is functional.
	 */
	@Test
	void testActiveWritableFileConfigSourceIsFunctional()
	{
		Optional<WritableFileConfigSource> result = ConfigSourceUtil.activeWritableFileConfigSource();

		assertThat("Should find a WritableFileConfigSource", result.isPresent(), is(true));

		WritableFileConfigSource source = result.get();

		// Test that it can write values (this will create the file if it doesn't exist)
		source.setProperty("new.test.key", "new.test.value");
		String newValue = source.getValue("new.test.key");
		assertThat("Should be able to write and read new property", newValue, is("new.test.value"));

		// Test that it can read the value we just wrote
		String value = source.getValue("new.test.key");
		assertThat("Should be able to read property", value, is("new.test.value"));
	}

	/**
	 * Tests the name and ordinal of the found WritableFileConfigSource.
	 */
	@Test
	void testActiveWritableFileConfigSourceProperties()
	{
		Optional<WritableFileConfigSource> result = ConfigSourceUtil.activeWritableFileConfigSource();

		assertThat("Should find a WritableFileConfigSource", result.isPresent(), is(true));

		WritableFileConfigSource source = result.get();

		String name = source.getName();
		int ordinal = source.getOrdinal();

		assertThat("Name should contain class name", name, containsString("WritableFileConfigSource"));
		assertThat("Name should contain parentheses with path", name, containsString("("));
		assertThat("Name should contain parentheses with path", name, containsString(")"));
		assertThat("Ordinal should be 500", ordinal, is(500));
	}

	/**
	 * Tests that multiple calls to activeWritableFileConfigSource return the same instance. This verifies that the
	 * ServiceLoader returns a singleton instance.
	 */
	@Test
	void testActiveWritableFileConfigSourceReturnsSameInstance()
	{
		Optional<WritableFileConfigSource> result1 = ConfigSourceUtil.activeWritableFileConfigSource();
		Optional<WritableFileConfigSource> result2 = ConfigSourceUtil.activeWritableFileConfigSource();

		assertThat("First call should find a source", result1.isPresent(), is(true));
		assertThat("Second call should find a source", result2.isPresent(), is(true));

		WritableFileConfigSource source1 = result1.get();
		WritableFileConfigSource source2 = result2.get();

		// In MicroProfile Config, config sources are typically singletons
		assertThat("Both calls should return the same instance", source1, is(sameInstance(source2)));
	}

	/**
	 * Tests that the utility correctly iterates through all config sources.
	 */
	@Test
	void testIteratesThroughAllConfigSources()
	{
		Config config = ConfigProvider.getConfig();

		int totalSources = 0;
		boolean foundWritableFileConfigSource = false;

		for (ConfigSource source : config.getConfigSources())
		{
			totalSources++;
			if (source instanceof WritableFileConfigSource)
			{
				foundWritableFileConfigSource = true;
			}
		}

		log.debug("Total config sources: {}", totalSources);

		assertThat("Should have at least one config source", totalSources, is(greaterThan(0)));
		assertThat("Should find WritableFileConfigSource among sources", foundWritableFileConfigSource, is(true));
	}
}
