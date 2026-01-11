package de.ruu.lib.postgres;

import de.ruu.lib.util.config.mp.WritableFileConfigSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class PostgresUtilTest
{
	@TempDir Path tempDir;

	/** Tests the postgres-specific initialization with default path. */
	@Test
	void testInitializePostgresUtilConfigDefaultPath()
	{
		WritableFileConfigSource config = PostgresUtil.initializePostgresUtilConfig();

		File configFile = new File("postgresutil.config.properties");

		try
		{
			assertThat("Config file should be created", configFile.exists(), is(true));
			assertThat("Should have all postgres properties", config.getPropertyNames(), hasSize(greaterThanOrEqualTo(9)));

			// Check some default values
			assertThat("Should have default host", config.getValue("postgres.host"), is("localhost"));
			assertThat("Should have default port", config.getValue("postgres.port"), is("5432"));
			assertThat("Should have default database", config.getValue("postgres.database"), is("mydb"));
			assertThat("Should have default username", config.getValue("postgres.username"), is("admin"));
			assertThat("Should have default schema", config.getValue("postgres.schema"), is("public"));
			assertThat("Should have default SSL setting", config.getValue("postgres.ssl.enabled"), is("false"));
		}
		finally
		{
			configFile.delete();
		}
	}

	/**
	 * Tests the postgres-specific initialization with custom path.
	 */
	@Test
	void testInitializePostgresUtilConfigCustomPath()
	{
		File configFile = tempDir.resolve("custom-postgres.config").toFile();

		WritableFileConfigSource config = PostgresUtil.initializePostgresUtilConfig(configFile.getAbsolutePath());

		assertThat("Config file should be created at custom path", configFile.exists(), is(true));
		assertThat("Should have all postgres properties", config.getPropertyNames(), hasSize(greaterThanOrEqualTo(9)));

		// Verify it's a valid postgres config
		assertThat("Should have postgres.host property", config.getValue("postgres.host"), is(notNullValue()));
		assertThat("Should have postgres.port property", config.getValue("postgres.port"), is(notNullValue()));
	}
}
