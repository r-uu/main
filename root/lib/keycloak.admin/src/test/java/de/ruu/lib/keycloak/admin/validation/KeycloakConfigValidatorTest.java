package de.ruu.lib.keycloak.admin.validation;

import de.ruu.lib.keycloak.admin.validation.KeycloakConfigValidator.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link KeycloakConfigValidator}.
 * 
 * @author r-uu
 * @since 2025-12-27
 */
class KeycloakConfigValidatorTest
{
	@Test
	void testValidateRoles_AllPresent()
	{
		Set<String> tokenRoles = Set.of("task-read", "task-write", "task-delete");
		Set<String> requiredRoles = Set.of("task-read", "task-write");
		
		RoleValidationResult result = KeycloakConfigValidator.validateRoles(tokenRoles, requiredRoles);
		
		assertTrue(result.isValid());
		assertTrue(result.getMissingRoles().isEmpty());
		assertEquals(1, result.getExtraRoles().size());
		assertTrue(result.getExtraRoles().contains("task-delete"));
	}
	
	@Test
	void testValidateRoles_MissingRoles()
	{
		Set<String> tokenRoles = Set.of("task-read");
		Set<String> requiredRoles = Set.of("task-read", "task-write", "task-delete");
		
		RoleValidationResult result = KeycloakConfigValidator.validateRoles(tokenRoles, requiredRoles);
		
		assertFalse(result.isValid());
		assertEquals(2, result.getMissingRoles().size());
		assertTrue(result.getMissingRoles().contains("task-write"));
		assertTrue(result.getMissingRoles().contains("task-delete"));
		assertTrue(result.getExtraRoles().isEmpty());
		
		assertFalse(result.getRecommendations().isEmpty());
		assertTrue(result.getSummary().contains("Missing"));
	}
	
	@Test
	void testValidateAudience_Valid()
	{
		Set<String> tokenAudiences = Set.of("jeeeraaah-backend", "account");
		String expectedAudience = "jeeeraaah-backend";
		
		AudienceValidationResult result = KeycloakConfigValidator
				.validateAudience(tokenAudiences, expectedAudience);
		
		assertTrue(result.isValid());
		assertEquals("jeeeraaah-backend", result.getExpectedAudience());
	}
	
	@Test
	void testValidateAudience_Missing()
	{
		Set<String> tokenAudiences = Set.of("account");
		String expectedAudience = "jeeeraaah-backend";
		
		AudienceValidationResult result = KeycloakConfigValidator
				.validateAudience(tokenAudiences, expectedAudience);
		
		assertFalse(result.isValid());
		assertFalse(result.getRecommendations().isEmpty());
		assertTrue(result.getSummary().contains("does not contain"));
	}
	
	@Test
	void testValidateRoleNamingConsistency_Consistent()
	{
		Set<String> roles = Set.of("task-read", "task-write", "taskgroup-read", "taskgroup-write");
		
		NamingConsistencyResult result = KeycloakConfigValidator
				.validateRoleNamingConsistency(roles);
		
		assertTrue(result.isConsistent());
		assertTrue(result.getSuspiciousRoles().isEmpty());
		assertTrue(result.getSuggestions().isEmpty());
	}
	
	@Test
	void testValidateRoleNamingConsistency_Inconsistent()
	{
		// Use roles where "taskgroup-read" conflicts with "task-group-read"
		Set<String> roles = Set.of("task-read", "task-write", "taskgroup-read", "task-group-read");
		
		NamingConsistencyResult result = KeycloakConfigValidator
				.validateRoleNamingConsistency(roles);
		
		assertFalse(result.isConsistent());
		assertFalse(result.getSuspiciousRoles().isEmpty());
		
		// Both "taskgroup-read" and "task-group-read" should be flagged as conflicting
		assertFalse(result.getRecommendations().isEmpty());
	}
	
	@Test
	void testValidateTokenLifetime_Optimal()
	{
		int accessTokenLifetime = 300;  // 5 minutes
		int refreshTokenLifetime = 1800; // 30 minutes
		
		TokenLifetimeValidationResult result = KeycloakConfigValidator
				.validateTokenLifetime(accessTokenLifetime, refreshTokenLifetime);
		
		assertTrue(result.getWarnings().isEmpty());
		assertEquals(300, result.getAccessTokenLifetimeSeconds());
		assertEquals(1800, result.getRefreshTokenLifetimeSeconds());
	}
	
	@Test
	void testValidateTokenLifetime_TooShort()
	{
		int accessTokenLifetime = 30;  // 30 seconds - too short
		int refreshTokenLifetime = 60;  // 1 minute - too short
		
		TokenLifetimeValidationResult result = KeycloakConfigValidator
				.validateTokenLifetime(accessTokenLifetime, refreshTokenLifetime);
		
		assertFalse(result.getWarnings().isEmpty());
		assertTrue(result.getSummary().contains("warning"));
		
		// Should have warnings about short lifetimes
		assertTrue(result.getWarnings().stream()
				.anyMatch(w -> w.contains("very short")));
	}
	
	@Test
	void testValidateTokenLifetime_RefreshShorterThanAccess()
	{
		int accessTokenLifetime = 600;  // 10 minutes
		int refreshTokenLifetime = 300; // 5 minutes - shorter than access!
		
		TokenLifetimeValidationResult result = KeycloakConfigValidator
				.validateTokenLifetime(accessTokenLifetime, refreshTokenLifetime);
		
		assertFalse(result.getWarnings().isEmpty());
		assertTrue(result.getWarnings().stream()
				.anyMatch(w -> w.contains("shorter than access token")));
	}
	
	@Test
	void testValidationReport_FullyValid()
	{
		RoleValidationResult roleValidation = KeycloakConfigValidator
				.validateRoles(Set.of("task-read"), Set.of("task-read"));
		
		AudienceValidationResult audienceValidation = KeycloakConfigValidator
				.validateAudience(Set.of("jeeeraaah-backend"), "jeeeraaah-backend");
		
		NamingConsistencyResult namingConsistency = KeycloakConfigValidator
				.validateRoleNamingConsistency(Set.of("task-read", "task-write"));
		
		TokenLifetimeValidationResult tokenLifetime = KeycloakConfigValidator
				.validateTokenLifetime(300, 1800);
		
		ValidationReport report = ValidationReport.builder()
				.roleValidation(roleValidation)
				.audienceValidation(audienceValidation)
				.namingConsistency(namingConsistency)
				.tokenLifetime(tokenLifetime)
				.build();
		
		assertTrue(report.isFullyValid());
		
		String detailedReport = report.getDetailedReport();
		assertNotNull(detailedReport);
		assertTrue(detailedReport.contains("Overall Status"));
		assertTrue(detailedReport.contains("VALID"));
	}
	
	@Test
	void testValidationReport_WithIssues()
	{
		RoleValidationResult roleValidation = KeycloakConfigValidator
				.validateRoles(Set.of("task-read"), Set.of("task-read", "task-write")); // Missing role
		
		AudienceValidationResult audienceValidation = KeycloakConfigValidator
				.validateAudience(Set.of("account"), "jeeeraaah-backend"); // Wrong audience
		
		NamingConsistencyResult namingConsistency = KeycloakConfigValidator
				.validateRoleNamingConsistency(Set.of("task-read"));
		
		TokenLifetimeValidationResult tokenLifetime = KeycloakConfigValidator
				.validateTokenLifetime(30, 60); // Too short
		
		ValidationReport report = ValidationReport.builder()
				.roleValidation(roleValidation)
				.audienceValidation(audienceValidation)
				.namingConsistency(namingConsistency)
				.tokenLifetime(tokenLifetime)
				.build();
		
		assertFalse(report.isFullyValid());
		
		String detailedReport = report.getDetailedReport();
		assertNotNull(detailedReport);
		assertTrue(detailedReport.contains("ISSUES DETECTED"));
		assertTrue(detailedReport.contains("task-write")); // Missing role
	}
}
