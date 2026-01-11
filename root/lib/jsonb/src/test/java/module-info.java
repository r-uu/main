/**
 * Test module descriptor for JUnit 5 tests.
 *
 * This module-info.java enables JPMS-compliant test execution both in Maven and IntelliJ IDEA.
 *
 * Important: This is an 'open' module which automatically opens all packages for reflection
 * (required by JUnit, Mockito, and test frameworks).
 */
open module de.ruu.lib.jsonb
{
	// Export packages (same as main module)
	exports de.ruu.lib.jsonb;

	// Dependencies from main module
	requires de.ruu.lib.util;
	requires java.desktop;
	requires jakarta.json;
	requires jakarta.json.bind;
	requires jakarta.ws.rs;

	// Lombok - must remain static (compile-time only)
	// The generated code (e.g., log field from @Slf4j) is in the compiled classes
	requires static lombok;

	// Runtime dependencies
	requires org.slf4j;

	// Test-specific dependencies
	requires org.junit.jupiter.api;
}

