package de.ruu.app.jeeeraaah.frontend.ui.fx.util;

import de.ruu.app.jeeeraaah.frontend.ui.fx.auth.AuthenticationHelper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * CDI Producer for {@link ServiceOperationExecutor}.
 *
 * <p>This class provides a CDI-managed instance of {@link ServiceOperationExecutor}
 * that can be injected into any controller or service.</p>
 *
 * <h2>Usage in Controllers</h2>
 * <pre>{@code
 * @Inject private ServiceOperationExecutor executor;
 *
 * // Then use it:
 * Set<TaskGroupFlat> groups = executor.execute(
 *     () -> taskGroupServiceClient.findAllFlat(),
 *     "fetching task groups",
 *     "Failed to load task groups",
 *     "Load failed after re-login"
 * );
 * }</pre>
 *
 * @author r-uu
 */
@Slf4j
@ApplicationScoped
public class ServiceOperationExecutorProducer
{
	@Inject private AuthenticationHelper authHelper;

	/**
	 * Produces a {@link ServiceOperationExecutor} instance for CDI injection.
	 *
	 * @return a new executor instance configured with the authentication helper
	 */
	@Produces
	@ApplicationScoped
	public ServiceOperationExecutor produceServiceOperationExecutor()
	{
		log.debug("Producing ServiceOperationExecutor with AuthenticationHelper");
		return new ServiceOperationExecutor(authHelper);
	}
}
