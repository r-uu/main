package de.ruu.app.jeeeraaah.common.api.domain;

import de.ruu.lib.jpa.core.Entity;
import de.ruu.lib.util.Strings;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Optional;

import static lombok.AccessLevel.NONE;

/**
 * Flat representation of a task group entity that doesn't include related tasks but the remaining relevant fields of
 * {@link TaskGroup}.
 * <p>
 * This is useful for clients that need to display or manipulate task groups without loading related tasks.
 */
public interface TaskGroupFlat extends Entity<Long>, Comparable<TaskGroupFlat>
{
	@NonNull String  name       ();
	Optional<String> description();

	@NonNull TaskGroupFlat name       (@NonNull  String name       );
	@NonNull TaskGroupFlat description(@Nullable String description);

	@Override default int compareTo(@NonNull TaskGroupFlat other) { return this.name().compareTo(other.name()); }

	@Getter
	@Setter
	@Accessors(fluent = true)
	public static class TaskGroupFlatSimple implements TaskGroupFlat
	{
		@Setter(NONE)
		private @Nullable  Long            id;
		@Setter(NONE)
		private @Nullable  Short           version;
		// no lombok-generation of setter because of additional validation in manually created method
		private @NonNull  String           name;
		private           Optional<String> description;

		public TaskGroupFlatSimple(@NonNull String name) { name(name); }

		public TaskGroupFlatSimple(@NonNull TaskGroupEntity<? extends TaskEntity<?, ?>> in)
		{
			this(in.name());
			id          = in.id();
			version     = in.version();
			description = in.description();
		}

		/**
		 * manually created fluent setter with extra parameter check (see throws documentation)
		 * @param name non-null, non-empty, non-blank
		 * @return {@code this}
		 * @throws IllegalArgumentException if {@code name} parameter is empty or blank
		 * @throws NullPointerException     if {@code name} parameter is {@code null}
		 */
		@Override @NonNull public TaskGroupFlatSimple name(@NonNull String name)
		{
			if (Strings.isEmptyOrBlank(name)) throw new IllegalArgumentException("name must not be empty nor blank");
			this.name = name;
			return this;
		}

		@Override public @NonNull TaskGroupFlat description(@Nullable String description)
		{
			this.description = Optional.ofNullable(description);
			return this;
		}
	}
}
