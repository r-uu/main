package de.ruu.app.jeeeraaah.backend.api.ws.rs;

import static de.ruu.app.jeeeraaah.common.api.domain.PathsCommon.PATH_JEEERAAAH_ROOT;
import static de.ruu.app.jeeeraaah.common.api.domain.PathsCommon.TOKEN_JEEERAAAH_ROOT;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * JAX-RS Application class that defines the root path for all REST endpoints.
 * <p>
 * The {@code @ApplicationPath} annotation configures the base URI path segment for all JAX-RS resources. With
 * {@code TOKEN_JEEERAAAH_ROOT = "jeeeraaah"}, all REST endpoints will be accessible under
 * {@code http://host:port/jeeeraaah/...}
 * <p>
 * This class is auto-discovered by the JAX-RS runtime through annotation scanning. No manual methods or configuration
 * are required.
 */
@ApplicationPath(TOKEN_JEEERAAAH_ROOT)
@OpenAPIDefinition
(
	info = @Info(title = "JEEE-RAAAH backend API", version = "1.0.0", description = "backend API with JTA/JPA and OpenAPI on OpenLiberty"),
	servers =
	{
		@Server(url = "http://172.26.187.214:9080" + PATH_JEEERAAAH_ROOT, description = "WSL2 (from Windows)"    ),
		@Server(url = "http://localhost:9080"      + PATH_JEEERAAAH_ROOT, description = "localhost"              ),
		@Server(url =                                PATH_JEEERAAAH_ROOT, description = "relative (current host)")
	}
)
public class JeeeRaaah extends Application
{
	// Empty class - JAX-RS will automatically:
	// 1. Discover and register all @Path annotated resources
	// 2. Use Jackson as JSON provider (configured via dependencies in pom.xml)
	//
	// Jackson will replace the default JSON-B (Yasson) provider because:
	// - jackson-jakarta-rs-json-provider is on the classpath
	// - Jackson providers have higher priority than JSON-B
}