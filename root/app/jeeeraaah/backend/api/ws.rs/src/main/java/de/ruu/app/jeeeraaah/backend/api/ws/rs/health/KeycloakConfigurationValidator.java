package de.ruu.app.jeeeraaah.backend.api.ws.rs.health;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.ruu.lib.keycloak.admin.validation.KeycloakConfigValidator;
import de.ruu.lib.keycloak.admin.validation.KeycloakConfigValidator.AudienceValidationResult;
import de.ruu.lib.keycloak.admin.validation.KeycloakConfigValidator.NamingConsistencyResult;
import de.ruu.lib.keycloak.admin.validation.KeycloakConfigValidator.RoleValidationResult;
import de.ruu.lib.keycloak.admin.validation.KeycloakConfigValidator.TokenLifetimeValidationResult;
import de.ruu.lib.keycloak.admin.validation.KeycloakConfigValidator.ValidationReport;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import lombok.extern.slf4j.Slf4j;

/**
 * Validates Keycloak configuration consistency at application startup.
 * 
 * <p>This validator performs the following checks:
 * <ul>
 *   <li>Scans all REST services for {@link RolesAllowed} annotations</li>
 *   <li>Validates that role naming follows consistent conventions</li>
 *   <li>Provides recommendations for configuration improvements</li>
 *   <li>Logs detailed reports for troubleshooting authentication issues</li>
 * </ul>
 * 
 * <p>The validator runs at application startup and logs warnings if inconsistencies are detected.
 * This helps catch common configuration errors before they cause runtime authentication failures.
 * 
 * @author r-uu
 * @since 2025-12-27
 */
@ApplicationScoped
@Slf4j
public class KeycloakConfigurationValidator
{
	private static final String DASHES = "=".repeat(80);
	
	@ConfigProperty(name = "mp.jwt.verify.audiences", defaultValue = "jeeeraaah-backend")
	String expectedAudience;
	
	/**
	 * Packages to scan for REST services with @RolesAllowed annotations.
	 */
	private static final List<String> PACKAGES_TO_SCAN = List.of(
			"de.ruu.app.jeeeraaah.backend.api.ws.rs"
	);
	
	/**
	 * REST service classes to scan for role definitions.
	 */
	private static final List<Class<?>> SERVICE_CLASSES = List.of(
			de.ruu.app.jeeeraaah.backend.api.ws.rs.TaskService.class,
			de.ruu.app.jeeeraaah.backend.api.ws.rs.TaskGroupService.class
	);
	
	/**
	 * Validates configuration on application startup.
	 * Uses CDI event observer to ensure validation runs after bean initialization.
	 */
	public void validateConfiguration(@Observes @Initialized(ApplicationScoped.class) Object init)
	{
		log.info("{}", DASHES);
		log.info("Keycloak Configuration Validation - Starting");
		log.info("{}", DASHES);
		
		try
		{
			// Extract all required roles from @RolesAllowed annotations
			Set<String> requiredRoles = extractRequiredRoles();
			
			log.info("Detected {} required roles from @RolesAllowed annotations:", requiredRoles.size());
			requiredRoles.stream()
					.sorted()
					.forEach(role -> log.info("  - {}", role));
			log.info("");
			
			// Validate role naming consistency
			NamingConsistencyResult namingResult = KeycloakConfigValidator
					.validateRoleNamingConsistency(requiredRoles);
			
			log.info("Role Naming Consistency Check:");
			log.info("  {}", namingResult.getSummary());
			if (!namingResult.isConsistent())
			{
				log.warn("Detected potential naming inconsistencies:");
				namingResult.getRecommendations().forEach(r -> log.warn("  {}", r));
			}
			log.info("");
			
			// Expected audience configuration
			log.info("JWT Audience Configuration:");
			log.info("  Expected audience (from mp.jwt.verify.audiences): {}", expectedAudience);
			log.info("  Note: Verify that Keycloak client has audience mapper configured");
			log.info("  to include '{}' in token 'aud' claim", expectedAudience);
			log.info("");
			
			// Provide setup instructions
			logSetupInstructions(requiredRoles);
			
			if (!namingResult.isConsistent())
			{
				log.warn("{}", DASHES);
				log.warn("CONFIGURATION WARNING: Naming inconsistencies detected");
				log.warn("Review the recommendations above to fix configuration issues");
				log.warn("{}", DASHES);
			}
			else
			{
				log.info("{}", DASHES);
				log.info("Keycloak Configuration Validation - Completed Successfully");
				log.info("{}", DASHES);
			}
		}
		catch (Exception e)
		{
			log.error("Failed to validate Keycloak configuration", e);
			log.error("{}", DASHES);
		}
	}
	
	/**
	 * Extracts all unique role names from @RolesAllowed annotations in REST services.
	 */
	private Set<String> extractRequiredRoles()
	{
		Set<String> roles = new HashSet<>();
		
		for (Class<?> serviceClass : SERVICE_CLASSES)
		{
			// Check class-level annotation
			RolesAllowed classRoles = serviceClass.getAnnotation(RolesAllowed.class);
			if (classRoles != null)
			{
				roles.addAll(Arrays.asList(classRoles.value()));
			}
			
			// Check method-level annotations
			for (Method method : serviceClass.getDeclaredMethods())
			{
				RolesAllowed methodRoles = method.getAnnotation(RolesAllowed.class);
				if (methodRoles != null)
				{
					roles.addAll(Arrays.asList(methodRoles.value()));
				}
			}
		}
		
		// Filter out default Keycloak roles
		return roles.stream()
				.filter(role -> !role.startsWith("default-"))
				.filter(role -> !role.equals("offline_access"))
				.filter(role -> !role.equals("uma_authorization"))
				.collect(Collectors.toSet());
	}
	
	/**
	 * Logs setup instructions for configuring Keycloak roles.
	 */
	private void logSetupInstructions(Set<String> requiredRoles)
	{
		log.info("Keycloak Setup Instructions:");
		log.info("----------------------------");
		log.info("");
		log.info("1. Create required roles in Keycloak (if not already present):");
		log.info("");
		
		List<String> sortedRoles = requiredRoles.stream().sorted().collect(Collectors.toList());
		for (String role : sortedRoles)
		{
			log.info("   docker exec keycloak /opt/keycloak/bin/kcadm.sh create roles \\");
			log.info("     -r jeeeraaah-realm -s name={}", role);
		}
		
		log.info("");
		log.info("2. Assign roles to users (example for user 'r-uu'):");
		log.info("");
		
		for (String role : sortedRoles)
		{
			log.info("   docker exec keycloak /opt/keycloak/bin/kcadm.sh add-roles \\");
			log.info("     -r jeeeraaah-realm --uusername r-uu --rolename {}", role);
		}
		
		log.info("");
		log.info("3. Configure audience mapper for client 'jeeeraaah-frontend':");
		log.info("");
		log.info("   Navigate in Keycloak Admin Console:");
		log.info("   Clients → jeeeraaah-frontend → Client scopes");
		log.info("   → jeeeraaah-frontend-dedicated → Add mapper");
		log.info("   → By configuration → Audience");
		log.info("   Set: Included Custom Audience = {}", expectedAudience);
		log.info("");
		log.info("4. Verify token claims after obtaining a token:");
		log.info("");
		log.info("   curl -s -X POST http://localhost:8080/realms/jeeeraaah-realm/protocol/openid-connect/token \\");
		log.info("     -H 'Content-Type: application/x-www-form-urlencoded' \\");
		log.info("     -d 'grant_type=password&client_id=jeeeraaah-frontend&username=r-uu&password=r-uu-password' \\");
		log.info("     | jq -r '.access_token' | cut -d. -f2 | base64 -d | jq .");
		log.info("");
		log.info("   Verify:");
		log.info("   - 'aud' claim contains: \"{}\"", expectedAudience);
		log.info("   - 'realm_access.roles' contains all required roles listed above");
		log.info("");
	}
	
	/**
	 * Creates a validation report from a JWT token.
	 * This method can be called from admin endpoints to validate actual tokens.
	 * 
	 * @param token JWT token to validate
	 * @return comprehensive validation report
	 */
	public ValidationReport validateToken(String token)
	{
		try
		{
			de.ruu.lib.keycloak.admin.validation.JwtTokenParser.TokenInfo tokenInfo = 
					de.ruu.lib.keycloak.admin.validation.JwtTokenParser.parseToken(token);
			
			Set<String> requiredRoles = extractRequiredRoles();
			
			RoleValidationResult roleValidation = KeycloakConfigValidator
					.validateRoles(tokenInfo.getRoles(), requiredRoles);
			
			AudienceValidationResult audienceValidation = KeycloakConfigValidator
					.validateAudience(tokenInfo.getAudiences(), expectedAudience);
			
			NamingConsistencyResult namingConsistency = KeycloakConfigValidator
					.validateRoleNamingConsistency(tokenInfo.getRoles());
			
			TokenLifetimeValidationResult tokenLifetime = KeycloakConfigValidator
					.validateTokenLifetime(
							(int) tokenInfo.getLifetimeSeconds(),
							1800); // Refresh token lifetime - would need to be passed separately
			
			return ValidationReport.builder()
					.roleValidation(roleValidation)
					.audienceValidation(audienceValidation)
					.namingConsistency(namingConsistency)
					.tokenLifetime(tokenLifetime)
					.build();
		}
		catch (Exception e)
		{
			log.error("Failed to validate token", e);
			throw new IllegalArgumentException("Invalid token format", e);
		}
	}
}
