package de.ruu.app.jeeeraaah.backend.common.mapping.lazy.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.ruu.app.jeeeraaah.backend.common.mapping.jpa.dto.AbstractJPATest;
import de.ruu.app.jeeeraaah.backend.persistence.jpa.TaskGroupJPA;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskGroupDTOLazy;
import de.ruu.lib.junit.DisabledOnServerNotListening;

/**
 * Integration tests for Map_TaskGroup_Lazy_JPA with persisted entities. Note: This mapper takes TaskGroupLazy and
 * creates TaskGroupJPA (without context parameter). According to @Mapping config, it ignores description and tasks
 * fields.
 */
@DisabledOnServerNotListening(propertyNameHost = "database.host", propertyNamePort = "database.port")
class Map_TaskGroup_Lazy_JPA_IntegrationTest extends AbstractJPATest
{
	@Test
	void map_createsTaskGroupJPA_fromLazy()
	{
		// Arrange
		TaskGroupJPA sourceGroup = persistAndFlush(new TaskGroupJPA("Source Group"));

		clearPersistenceContext();
		TaskGroupJPA reloaded = find(TaskGroupJPA.class, sourceGroup.id());

		// Create lazy DTO from persisted group
		TaskGroupDTOLazy lazy = new TaskGroupDTOLazy(reloaded);

		// Act
		TaskGroupJPA result = Map_TaskGroup_Lazy_JPA.INSTANCE.map(lazy);

		// Assert
		assertThat(result)          .isNotNull();
		assertThat(result.name   ()).isEqualTo("Source Group");
		assertThat(result.id     ()).isEqualTo(sourceGroup.id());
		assertThat(result.version()).isEqualTo(sourceGroup.version());
	}

	@Test
	void map_ignoresDescription_asPerMappingConfig()
	{
		// Arrange
		TaskGroupJPA sourceGroup = persistAndFlush(new TaskGroupJPA("Source Group"));
		sourceGroup.description("This should be ignored");
		persistAndFlush(sourceGroup);

		clearPersistenceContext();
		TaskGroupJPA reloaded = find(TaskGroupJPA.class, sourceGroup.id());

		TaskGroupDTOLazy lazy = new TaskGroupDTOLazy(reloaded);

		// Act
		TaskGroupJPA result = Map_TaskGroup_Lazy_JPA.INSTANCE.map(lazy);

		// Assert: description mapping is ignored per @Mapping(target = "description", ignore = true)
		// TaskGroupJPA.description() returns Optional<String>, should be empty
		assertThat(result.description().isPresent()).isFalse();
	}

	@Test
	void map_ignoresTasks_asPerMappingConfig()
	{
		// Arrange
		TaskGroupJPA sourceGroup = persistAndFlush(new TaskGroupJPA("Source Group"));

		clearPersistenceContext();
		TaskGroupJPA reloaded = find(TaskGroupJPA.class, sourceGroup.id());

		TaskGroupDTOLazy lazy = new TaskGroupDTOLazy(reloaded);

		// Act
		TaskGroupJPA result = Map_TaskGroup_Lazy_JPA.INSTANCE.map(lazy);

		// Assert: tasks mapping is ignored per @Mapping(target = "tasks", ignore = true)
		assertThat(result.tasks().isPresent()).isFalse();
	}

	@Test
	void map_createsNewInstance()
	{
		// Arrange
		TaskGroupJPA sourceGroup = persistAndFlush(new TaskGroupJPA("Source Group"));

		clearPersistenceContext();
		TaskGroupJPA reloaded = find(TaskGroupJPA.class, sourceGroup.id());

		TaskGroupDTOLazy lazy = new TaskGroupDTOLazy(reloaded);

		// Act
		TaskGroupJPA result = Map_TaskGroup_Lazy_JPA.INSTANCE.map(lazy);

		// Assert - should be new instance, not same as source
		assertThat(result).isNotSameAs(reloaded);
		assertThat(result.name()).isEqualTo(reloaded.name());
	}

	@Test
	void objectFactory_createsTaskGroupJPA()
	{
		// Arrange
		TaskGroupJPA sourceGroup = persistAndFlush(new TaskGroupJPA("Source Group"));

		clearPersistenceContext();
		TaskGroupJPA reloaded = find(TaskGroupJPA.class, sourceGroup.id());

		TaskGroupDTOLazy lazy = new TaskGroupDTOLazy(reloaded);

		// Act
		TaskGroupJPA result = Map_TaskGroup_Lazy_JPA.INSTANCE.create(lazy);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.name()).isEqualTo("Source Group");
	}
}
