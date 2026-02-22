package de.ruu.app.jeeeraaah.common.api.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.*;

class RemoveNeighboursFromTaskConfigTest
{
	@Test void recordConstructorCreatesConfigCorrectly()
	{
		// Test record canonical constructor with validation and defensive copies
		RemoveNeighboursFromTaskConfig cfg = new RemoveNeighboursFromTaskConfig
		(
				42L,
				true,
				Set.of(1L, 2L),
				Set.of(3L),
				Set.of(4L, 5L)
		);

		assertThat(cfg.idTask()                ).isEqualTo(42L);
		assertThat(cfg.removeFromSuperTask()   ).isEqualTo(true);
		assertThat(cfg.removeFromPredecessors()).containsExactlyInAnyOrder(1L, 2L);
		assertThat(cfg.removeFromSubTasks()    ).containsExactly(3L);
		assertThat(cfg.removeFromSuccessors()  ).containsExactlyInAnyOrder(4L, 5L);
	}

	@Test void jacksonDeserializationWorks() throws Exception
	{
		// Test Jackson deserialization with parameter names (via -parameters compiler flag)
		String json = """
				{
					"idTask": 99,
					"removeFromSuperTask": false,
					"removeFromPredecessors": [10, 20],
					"removeFromSubTasks": [30],
					"removeFromSuccessors": []
				}
				""";

		ObjectMapper mapper = new ObjectMapper();
		RemoveNeighboursFromTaskConfig cfg = mapper.readValue(json, RemoveNeighboursFromTaskConfig.class);

		assertThat(cfg.idTask()).isEqualTo(99L);
		assertThat(cfg.removeFromSuperTask()).isEqualTo(false);
		assertThat(cfg.removeFromPredecessors()).containsExactlyInAnyOrder(10L, 20L);
		assertThat(cfg.removeFromSubTasks()).containsExactly(30L);
		assertThat(cfg.removeFromSuccessors()).isEmpty();
	}
}