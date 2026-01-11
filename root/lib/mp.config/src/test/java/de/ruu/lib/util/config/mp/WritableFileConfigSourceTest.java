package de.ruu.lib.util.config.mp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import lombok.extern.slf4j.Slf4j;

/**
 * Unit tests for {@link WritableFileConfigSource}.
 *
 * <p>
 * Tests cover:
 * <ul>
 * <li>Lazy loading of configuration properties from file</li>
 * <li>Reading property values from the config source</li>
 * <li>Writing new properties and persisting them to file</li>
 * <li>Removing properties and persisting changes</li>
 * <li>Getting all property names and the properties map</li>
 * <li>Default behaviour when config file doesn't exist</li>
 * </ul>
 */
@Slf4j
class WritableFileConfigSourceTest
{
	@TempDir
	Path tempDir;

	private File testConfigFile;
	private String originalConfigFileProperty;

	/**
	 * Sets up a temporary config file for each test. Stores the original system property value to restore it later.
	 */
	@BeforeEach
	void beforeEach() throws IOException
	{
		testConfigFile = tempDir.resolve("test-config.properties").toFile();

		// store original property value
		originalConfigFileProperty = System.getProperty(WritableFileConfigSource.CONFIG_FILE_NAME_KEY);

		// set system property to point to our test file
		System.setProperty(WritableFileConfigSource.CONFIG_FILE_NAME_KEY, testConfigFile.getAbsolutePath());

		// create a test config file with initial properties
		Properties props = new Properties();
		props.setProperty("test.property.one", "value1");
		props.setProperty("test.property.two", "value2");
		props.setProperty("test.number", "42");

		try (FileOutputStream fos = new FileOutputStream(testConfigFile))
		{
			props.store(fos, "Test configuration");
		}

		log.debug("Test config file created at: {}", testConfigFile.getAbsolutePath());
	}

	/** cleans up after each test by restoring the original system property. */
	@AfterEach
	void afterEach()
	{
		// Restore original property value
		if (originalConfigFileProperty != null)
		{
			System.setProperty(WritableFileConfigSource.CONFIG_FILE_NAME_KEY, originalConfigFileProperty);
		}
		else
		{
			System.clearProperty(WritableFileConfigSource.CONFIG_FILE_NAME_KEY);
		}
	}

	/** tests that the config source returns the correct name including the file path. */
	@Test
	void testGetName()
	{
		WritableFileConfigSource source = new WritableFileConfigSource();

		String name = source.getName();

		assertThat("Name should contain class name", name, containsString("WritableFileConfigSource"));
		assertThat("Name should contain file path", name, containsString(testConfigFile.getAbsolutePath()));
	}

	/**
	 * Tests that the config source has the expected ordinal value. Higher ordinal means higher priority in MicroProfile
	 * Config.
	 */
	@Test
	void testGetOrdinal()
	{
		WritableFileConfigSource source = new WritableFileConfigSource();

		int ordinal = source.getOrdinal();

		assertThat("Ordinal should be 500", ordinal, is(500));
	}

	/** tests that properties are loaded from the file when first accessed. */
	@Test
	void testLazyLoadingOfProperties()
	{
		WritableFileConfigSource source = new WritableFileConfigSource();

		// Properties should be loaded on first access
		String value = source.getValue("test.property.one");

		assertThat("Property should be loaded from file", value, is("value1"));
	}

	/** Tests retrieving a property value by key. */
	@Test
	void testGetValue()
	{
		WritableFileConfigSource source = new WritableFileConfigSource();

		String value1 = source.getValue("test.property.one");
		String value2 = source.getValue("test.property.two");
		String value3 = source.getValue("test.number");

		assertThat("First property value should match", value1, is("value1"));
		assertThat("Second property value should match", value2, is("value2"));
		assertThat("Numeric property value should match", value3, is("42"));
	}

	/** tests retrieving a non-existent property returns null. */
	@Test
	void testGetValueNonExistent()
	{
		WritableFileConfigSource source = new WritableFileConfigSource();

		String value = source.getValue("non.existent.key");

		assertThat("Non-existent property should return null", value, is(nullValue()));
	}

	/** tests getting all property names. */
	@Test
	void testGetPropertyNames()
	{
		WritableFileConfigSource source = new WritableFileConfigSource();

		Set<String> propertyNames = source.getPropertyNames();

		assertThat("Property names should not be null", propertyNames, is(notNullValue()));
		assertThat("Should contain all loaded properties", propertyNames, hasSize(3));
		assertThat("Should contain first property", propertyNames, hasItem("test.property.one"));
		assertThat("Should contain second property", propertyNames, hasItem("test.property.two"));
		assertThat("Should contain third property", propertyNames, hasItem("test.number"));
	}

	/** tests getting all properties as a map. */
	@Test
	void testGetProperties()
	{
		WritableFileConfigSource source = new WritableFileConfigSource();

		Map<String, String> properties = source.getProperties();

		assertThat("Properties map should not be null", properties, is(notNullValue()));
		assertThat("Properties map should contain all entries", properties.entrySet(), hasSize(3));
		assertThat("Properties map should contain correct value", properties, hasEntry("test.property.one", "value1"));
		assertThat("Properties map should contain correct value", properties, hasEntry("test.property.two", "value2"));
		assertThat("Properties map should contain correct value", properties, hasEntry("test.number", "42"));
	}

	/** tests that the returned properties map is unmodifiable. */
	@Test
	void testGetPropertiesIsUnmodifiable()
	{
		WritableFileConfigSource source = new WritableFileConfigSource();

		Map<String, String> properties = source.getProperties();

		try
		{
			properties.put("new.key", "new.value");
			assertThat("Should have thrown UnsupportedOperationException", false);
		}
		catch (UnsupportedOperationException e)
		{
			// Expected - map should be unmodifiable
			assertThat("Exception should be thrown when trying to modify", true);
		}
	}

	/** tests setting a new property and verifying it's persisted to file. */
	@Test
	void testSetProperty() throws IOException
	{
		WritableFileConfigSource source = new WritableFileConfigSource();

		// Set a new property
		source.setProperty("new.property", "new.value");

		// Verify it's in the config source
		String value = source.getValue("new.property");
		assertThat("New property should be retrievable", value, is("new.value"));

		// Verify it's persisted to file
		Properties fileProps = new Properties();
		try (var fis = new java.io.FileInputStream(testConfigFile))
		{
			fileProps.load(fis);
		}

		assertThat("New property should be persisted to file", fileProps.getProperty("new.property"), is("new.value"));
	}

	/** tests updating an existing property value. */
	@Test
	void testUpdateProperty() throws IOException
	{
		WritableFileConfigSource source = new WritableFileConfigSource();

		// Update existing property
		source.setProperty("test.property.one", "updated.value");

		// Verify it's updated
		String value = source.getValue("test.property.one");
		assertThat("Property should be updated", value, is("updated.value"));

		// Verify it's persisted to file
		Properties fileProps = new Properties();
		try (var fis = new java.io.FileInputStream(testConfigFile))
		{
			fileProps.load(fis);
		}

		assertThat("Updated property should be persisted to file", fileProps.getProperty("test.property.one"),
				is("updated.value"));
	}

	/** tests removing a property. */
	@Test
	void testRemoveProperty() throws IOException
	{
		WritableFileConfigSource source = new WritableFileConfigSource();

		// Remove property
		source.removeProperty("test.property.one");

		// Verify it's removed from config source
		String value = source.getValue("test.property.one");
		assertThat("Removed property should return null", value, is(nullValue()));

		// Verify other properties still exist
		assertThat("Other properties should still exist", source.getValue("test.property.two"), is("value2"));

		// Verify removal is persisted to file
		Properties fileProps = new Properties();
		try (var fis = new java.io.FileInputStream(testConfigFile))
		{
			fileProps.load(fis);
		}

		assertThat("Removed property should not be in file", fileProps.containsKey("test.property.one"), is(false));
		assertThat("Other properties should still be in file", fileProps.getProperty("test.property.two"), is("value2"));
	}

	/**
	 * Tests behavior when config file doesn't exist. Should not throw an exception, just log a warning.
	 */
	@Test
	void testMissingConfigFile()
	{
		// Delete the test config file
		testConfigFile.delete();

		WritableFileConfigSource source = new WritableFileConfigSource();

		// Should not throw exception, just have empty properties
		Map<String, String> properties = source.getProperties();

		assertThat("Properties should be empty when file doesn't exist", properties.entrySet(), hasSize(0));
	}

	/** tests that multiple operations maintain consistency. */
	@Test
	void testMultipleOperations() throws IOException
	{
		WritableFileConfigSource source = new WritableFileConfigSource();

		// Add a property
		source.setProperty("prop.a", "valueA");

		// Update another
		source.setProperty("test.number", "100");

		// Remove one
		source.removeProperty("test.property.two");

		// Verify state
		assertThat("New property should exist", source.getValue("prop.a"), is("valueA"));
		assertThat("Updated property should have new value", source.getValue("test.number"), is("100"));
		assertThat("Removed property should be null", source.getValue("test.property.two"), is(nullValue()));
		assertThat("Unchanged property should still exist", source.getValue("test.property.one"), is("value1"));

		// Verify file persistence
		Properties fileProps = new Properties();
		try (var fis = new java.io.FileInputStream(testConfigFile))
		{
			fileProps.load(fis);
		}

		assertThat("File should have 3 properties", fileProps.size(), is(3));
		assertThat("File should contain new property", fileProps.getProperty("prop.a"), is("valueA"));
		assertThat("File should contain updated property", fileProps.getProperty("test.number"), is("100"));
		assertThat("File should not contain removed property", fileProps.containsKey("test.property.two"), is(false));
	}
}
