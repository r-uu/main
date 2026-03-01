package de.ruu.app.jeeeraaah.backend.api.ws.rs;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

/**
 * JAX-RS provider that configures Jackson ObjectMapper for the backend API.
 * <p>
 * This provider is automatically discovered by JAX-RS and supplies a properly configured ObjectMapper for JSON
 * serialization/deserialization. It enables:
 * <ul>
 * <li>Java 8 date/time types (LocalDate, LocalDateTime, etc.) via JavaTimeModule</li>
 * <li>Optional support via Jdk8Module</li>
 * <li>ISO-8601 date formatting (not timestamps)</li>
 * <li>Lenient deserialization (ignores unknown properties)</li>
 * </ul>
 */
@Provider
public class JacksonConfig implements ContextResolver<ObjectMapper>
{
	private final ObjectMapper objectMapper;

	public JacksonConfig()
	{
		objectMapper = new ObjectMapper();

		// Register Java 8 modules
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.registerModule(new Jdk8Module());

		// Configure serialization
		objectMapper.disable(WRITE_DATES_AS_TIMESTAMPS); // Use ISO-8601 strings instead of timestamps
		objectMapper.disable(FAIL_ON_EMPTY_BEANS);

		// Configure deserialization
		objectMapper.disable(FAIL_ON_UNKNOWN_PROPERTIES);
	}

	@Override public ObjectMapper getContext(Class<?> type) { return objectMapper; }
}
