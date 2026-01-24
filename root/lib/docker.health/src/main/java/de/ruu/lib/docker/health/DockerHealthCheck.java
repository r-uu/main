package de.ruu.lib.docker.health;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Comprehensive health check for Docker environment.
 *
 * <p><b>DEPRECATED:</b> Use {@link HealthCheckRunner} with {@link HealthCheckProfiles} instead.
 * This class is kept for backward compatibility only.
 *
 * <p><b>Migration example:</b>
 * <pre>
 * // Old way (deprecated):
 * DockerHealthCheck healthCheck = new DockerHealthCheck();
 * boolean healthy = healthCheck.checkHealth();
 *
 * // New way (recommended):
 * HealthCheckRunner runner = HealthCheckProfiles.fullEnvironment();
 * boolean healthy = runner.runAll();
 * </pre>
 *
 * <p>Verifies that all required services are available:
 * <ul>
 *   <li>PostgreSQL databases (jeeeraaah, lib_test, keycloak)</li>
 *   <li>Keycloak server and realm</li>
 *   <li>JasperReports service</li>
 * </ul>
 *
 * <p>If any service is missing or unhealthy, provides executable commands to fix the issue.
 *
 * @deprecated Use {@link HealthCheckRunner} with {@link HealthCheckProfiles} instead
 */
@Slf4j
@Deprecated(since = "0.0.1", forRemoval = true)
public class DockerHealthCheck
{
	private final HealthCheckRunner runner;
	private final List<HealthIssue> issues = new ArrayList<>();

	/**
	 * Represents a health check issue with fix command.
	 *
	 * @deprecated Use {@link HealthCheckResult} instead
	 */
	@Deprecated(since = "0.0.1", forRemoval = true)
	public static class HealthIssue
	{
		private final String service;
		private final String problem;
		private final String fixCommand;
		private final String alias;

		public HealthIssue(String service, String problem, String fixCommand, String alias)
		{
			this.service = service;
			this.problem = problem;
			this.fixCommand = fixCommand;
			this.alias = alias;
		}

		public String getService() { return service; }
		public String getProblem() { return problem; }
		public String getFixCommand() { return fixCommand; }
		public String getAlias() { return alias; }
	}

	public DockerHealthCheck()
	{
		// Use the new HealthCheckRunner internally
		this.runner = HealthCheckProfiles.fullEnvironment();
	}

	/**
	 * Performs complete health check and returns result.
	 *
	 * @return true if all services are healthy, false otherwise
	 * @deprecated Use {@link HealthCheckRunner#runAll()} instead
	 */
	@Deprecated(since = "0.0.1", forRemoval = true)
	public boolean checkHealth()
	{
		boolean healthy = runner.runAll();

		// Convert HealthCheckResults to HealthIssues for backward compatibility
		issues.clear();
		for (HealthCheckResult result : runner.getFailures())
		{
			issues.add(new HealthIssue(
				result.getService(),
				result.getProblem(),
				result.getFixCommand(),
				result.getAlias()
			));
		}

		return healthy;
	}

	/**
	 * Returns list of issues found during health check.
	 *
	 * @deprecated Use {@link HealthCheckRunner#getFailures()} instead
	 */
	@Deprecated(since = "0.0.1", forRemoval = true)
	public List<HealthIssue> getIssues()
	{
		return new ArrayList<>(issues);
	}

	/**
	 * Returns true if all services are healthy.
	 *
	 * @deprecated Use {@link HealthCheckRunner#isHealthy()} instead
	 */
	@Deprecated(since = "0.0.1", forRemoval = true)
	public boolean isHealthy()
	{
		return runner.isHealthy();
	}

	/**
	 * Main method for standalone testing.
	 *
	 * @deprecated Use {@link HealthCheckRunner#main(String[])} instead
	 */
	@Deprecated(since = "0.0.1", forRemoval = true)
	public static void main(String[] args)
	{
		log.warn("DockerHealthCheck is deprecated. Use HealthCheckRunner instead.");
		DockerHealthCheck healthCheck = new DockerHealthCheck();
		boolean healthy = healthCheck.checkHealth();

		System.exit(healthy ? 0 : 1);
	}
}
