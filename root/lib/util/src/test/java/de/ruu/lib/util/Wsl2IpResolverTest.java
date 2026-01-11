package de.ruu.lib.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class Wsl2IpResolverTest
{
	@AfterEach
	void clearCache()
	{
		Wsl2IpResolver.clearCache();
	}

	@Test
	void testGetWsl2Ip_shouldReturnValidIp()
	{
		String ip = Wsl2IpResolver.getWsl2Ip();

		assertNotNull(ip, "IP should not be null");
		assertFalse(ip.isEmpty(), "IP should not be empty");

		// Should be either localhost or valid IP format
		assertTrue(
				ip.equals("localhost") || ip.matches("\\d+\\.\\d+\\.\\d+\\.\\d+"),
				"IP should be 'localhost' or valid IP format, but was: " + ip
		);

		System.out.println("Detected WSL2 IP: " + ip);
	}

	@Test
	void testGetWsl2Ip_shouldCacheResult()
	{
		String ip1 = Wsl2IpResolver.getWsl2Ip();
		String ip2 = Wsl2IpResolver.getWsl2Ip();

		assertSame(ip1, ip2, "Should return same cached instance");
	}

	@Test
	void testClearCache_shouldInvalidateCache()
	{
		String ip1 = Wsl2IpResolver.getWsl2Ip();
		Wsl2IpResolver.clearCache();
		String ip2 = Wsl2IpResolver.getWsl2Ip();

		// Values should be equal, but not same instance (new object after cache clear)
		assertEquals(ip1, ip2, "IPs should be equal");
	}

	@Test
	void testResolve_shouldReplacePlaceholder() throws Exception
	{
		Properties props = new Properties();
		props.setProperty("db.host", "${WSL2_IP}");
		props.setProperty("db.port", "5432");
		props.setProperty("other.host", "localhost");

		Wsl2IpResolver.resolve(props);

		String dbHost = props.getProperty("db.host");
		assertNotNull(dbHost);
		assertFalse(dbHost.contains("${WSL2_IP}"), "Placeholder should be replaced");
		assertFalse(dbHost.contains("$"), "No dollar signs should remain");

		// Other properties should remain unchanged
		assertEquals("5432", props.getProperty("db.port"));
		assertEquals("localhost", props.getProperty("other.host"));

		System.out.println("Resolved db.host: " + dbHost);
	}

	@Test
	void testResolve_shouldHandleMultiplePlaceholders() throws Exception
	{
		Properties props = new Properties();
		props.setProperty("host1", "${WSL2_IP}");
		props.setProperty("host2", "${WSL2_IP}");
		props.setProperty("url", "jdbc:postgresql://${WSL2_IP}:5432/db");

		Wsl2IpResolver.resolve(props);

		String host1 = props.getProperty("host1");
		String host2 = props.getProperty("host2");
		String url = props.getProperty("url");

		assertFalse(host1.contains("$"));
		assertFalse(host2.contains("$"));
		assertFalse(url.contains("$"));
		assertTrue(url.startsWith("jdbc:postgresql://"));
		assertTrue(url.endsWith(":5432/db"));

		System.out.println("Resolved URL: " + url);
	}

	@Test
	void testLoadAndResolve_shouldLoadAndReplacePlaceholders() throws Exception
	{
		// Create temporary properties file
		File tempFile = File.createTempFile("test-config", ".properties");
		tempFile.deleteOnExit();

		try (FileWriter writer = new FileWriter(tempFile))
		{
			writer.write("db.host=${WSL2_IP}\n");
			writer.write("db.port=5432\n");
			writer.write("api.url=http://${WSL2_IP}:9080\n");
		}

		Properties props = Wsl2IpResolver.loadAndResolve(tempFile.getAbsolutePath());

		String dbHost = props.getProperty("db.host");
		String apiUrl = props.getProperty("api.url");

		assertNotNull(dbHost);
		assertFalse(dbHost.contains("$"));

		assertNotNull(apiUrl);
		assertFalse(apiUrl.contains("$"));
		assertTrue(apiUrl.startsWith("http://"));
		assertTrue(apiUrl.endsWith(":9080"));

		System.out.println("Loaded and resolved properties:");
		System.out.println("  db.host = " + dbHost);
		System.out.println("  api.url = " + apiUrl);
	}
}

