package de.ruu.app.jeeeraaah.common.api.domain;

import java.util.Optional;
import java.util.Set;

import lombok.NonNull;

public interface TaskService<T extends Task<? extends TaskGroup<?>, ? extends Task<?, ?>>> {
	@NonNull
	T create(@NonNull T task) throws Exception;

	Optional<? extends T> read(@NonNull Long id) throws Exception;

	@NonNull
	T update(@NonNull T task) throws Exception;

	void delete(@NonNull Long id) throws Exception;

	// TODO
	// @NonNull T createAsSubTaskFor (@NonNull T task, @NonNull T subTask ) throws
	// Exception
	// @NonNull T createAsPredecessorFor(@NonNull T task, @NonNull T prededessor)
	// throws Exception
	// @NonNull T createAsSuccessorFor (@NonNull T task, @NonNull T successor )
	// throws Exception

	default Optional<? extends T> find(@NonNull Long id) throws Exception {
		return read(id);
	}

	Set<? extends T> findAll() throws Exception;

	Optional<? extends T> findWithRelated(@NonNull Long id) throws Exception;

	void addSubTask(@NonNull T task, @NonNull T subTask) throws Exception;

	void addPredecessor(@NonNull T task, @NonNull T predecessor) throws Exception;

	void addSuccessor(@NonNull T task, @NonNull T successor) throws Exception;

	void removeSubTask(@NonNull T task, @NonNull T subTask) throws Exception;

	void removePredecessor(@NonNull T task, @NonNull T predecessor) throws Exception;

	void removeSuccessor(@NonNull T task, @NonNull T successor) throws Exception;

	void removeNeighboursFromTask(@NonNull RemoveNeighboursFromTaskConfig removeNeighboursFromTaskConfig)
			throws Exception;
}