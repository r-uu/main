package de.ruu.app.jeeeraaah.backend.api.ws.rs.health;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import de.ruu.lib.keycloak.admin.validation.JwtTokenParser;
import de.ruu.lib.keycloak.admin.validation.KeycloakConfigValidator.ValidationReport;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Admin endpoint for validating Keycloak configuration and JWT tokens.
 * 
 * <p>This endpoint provides runtime diagnostics for authentication and authorization issues.
 * Useful for troubleshooting Keycloak integration problems without restarting the server.
 * 
 * <p><strong>Security Note:</strong> This endpoint requires 'task-group-admin' role.
 * Only trusted administrators should have access to this endpoint as it exposes
 * configuration details.
 * 
 * @author r-uu
 * @since 2025-12-27
 */
@ApplicationScoped
@Path("/admin/keycloak")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin", description = "Administrative endpoints for configuration validation")
@Slf4j
public class KeycloakValidationEndpoint
{
	@Inject
	KeycloakConfigurationValidator validator;
	
	/**
	 * Validates the current Keycloak configuration.
	 * Returns information about required roles and naming consistency.
	 * 
	 * @return validation summary
	 */
	@GET
	@Path("/validate-config")
	@RolesAllowed("taskgroup-admin")
	@Operation(
			summary = "Validate Keycloak configuration",
			description = "Checks role naming consistency and provides setup instructions")
	@APIResponse(
			responseCode = "200",
			description = "Configuration validation completed")
	public Response validateConfiguration()
	{
		log.info("Admin endpoint called: validate-config");
		
		try
		{
			// Trigger re-validation with null parameter (CDI observer doesn't need it)
			validator.validateConfiguration(null);
			
			return Response.ok()
					.entity("{\"status\": \"Configuration validation completed. Check server logs for details.\"}")
					.build();
		}
		catch (Exception e)
		{
			log.error("Configuration validation failed", e);
			return Response.serverError()
					.entity("{\"error\": \"" + e.getMessage() + "\"}")
					.build();
		}
	}
	
	/**
	 * Validates a specific JWT token.
	 * 
	 * <p>This endpoint accepts a JWT token and performs comprehensive validation:
	 * <ul>
	 *   <li>Role presence check</li>
	 *   <li>Audience validation</li>
	 *   <li>Token lifetime analysis</li>
	 *   <li>Role naming consistency</li>
	 * </ul>
	 * 
	 * @param request token validation request
	 * @return detailed validation report
	 */
	@POST
	@Path("/validate-token")
	@RolesAllowed("taskgroup-admin")
	@Operation(
			summary = "Validate JWT token",
			description = "Performs comprehensive validation of a JWT token including roles, audience, and lifetime")
	@org.eclipse.microprofile.openapi.annotations.parameters.RequestBody(
			description = "Token validation request containing the JWT token to validate",
			required = true,
			content = @org.eclipse.microprofile.openapi.annotations.media.Content(
					mediaType = MediaType.APPLICATION_JSON,
					schema = @org.eclipse.microprofile.openapi.annotations.media.Schema(implementation = TokenValidationRequest.class)))
	@APIResponse(
			responseCode = "200",
			description = "Token validation completed",
			content = @org.eclipse.microprofile.openapi.annotations.media.Content(
					mediaType = MediaType.TEXT_PLAIN))
	@APIResponse(
			responseCode = "400",
			description = "Invalid token format")
	public Response validateToken(TokenValidationRequest request)
	{
		if (request == null || request.getToken() == null || request.getToken().isBlank())
		{
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("{\"error\": \"Token is required\"}")
					.build();
		}
		
		log.info("Admin endpoint called: validate-token (token length: {} chars)", 
				request.getToken().length());
		
		try
		{
			// Parse token to show basic info
			JwtTokenParser.TokenInfo tokenInfo = JwtTokenParser.parseToken(request.getToken());
			
			log.info("""
					Token info:
					  Issuer: {}
					  Subject: {}
					  Preferred Username: {}
					  Audiences: {}
					  Roles: {}
					  Lifetime: {}s
					  Remaining: {}s
					  Expired: {}""",
					tokenInfo.getIssuer(),
					tokenInfo.getSubject(),
					tokenInfo.getPreferredUsername(),
					tokenInfo.getAudiences(),
					tokenInfo.getRoles(),
					tokenInfo.getLifetimeSeconds(),
					tokenInfo.getRemainingLifetimeSeconds(),
					tokenInfo.isExpired());

			// Perform comprehensive validation
			ValidationReport report = validator.validateToken(request.getToken());
			
			String detailedReport = report.getDetailedReport();
			log.info("Validation Report:\n{}", detailedReport);
			
			return Response.ok()
					.entity(detailedReport)
					.type(MediaType.TEXT_PLAIN)
					.build();
		}
		catch (Exception e)
		{
			log.error("Token validation failed", e);
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("{\"error\": \"" + e.getMessage() + "\"}")
					.build();
		}
	}
	
	/**
	 * Request object for token validation.
	 */
	public static class TokenValidationRequest
	{
		private String token;
		
		public String getToken() { return token; }
		public void setToken(String token) { this.token = token; }
	}
}
