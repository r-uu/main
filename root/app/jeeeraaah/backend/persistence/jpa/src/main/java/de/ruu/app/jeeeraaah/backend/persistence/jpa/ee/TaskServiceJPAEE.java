package de.ruu.app.jeeeraaah.backend.persistence.jpa.ee;

import static java.util.Objects.nonNull;

import de.ruu.app.jeeeraaah.backend.persistence.jpa.TaskLazyMapper;
import de.ruu.app.jeeeraaah.backend.persistence.jpa.internal.TaskGroupRepositoryJPA;
import de.ruu.app.jeeeraaah.backend.persistence.jpa.internal.TaskRepositoryJPA;
import de.ruu.app.jeeeraaah.backend.persistence.jpa.internal.TaskServiceJPA;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Transactional // jakarta.transaction.Transactional interceptor
@Slf4j
public class TaskServiceJPAEE extends TaskServiceJPA
{
	@Inject private TaskRepositoryJPAEE      repository;
	@Inject private TaskGroupRepositoryJPAEE taskGroupRepository;
	@Inject private TaskLazyMapper           mapper;

	@PostConstruct private void postConstruct()
	{
		log.debug("repository available         : {}", nonNull(repository         ));
		log.debug("taskGroupRepository available: {}", nonNull(taskGroupRepository));
		log.debug("mapper available             : {}", nonNull(mapper             ));
	}

	@Override protected TaskRepositoryJPA      repository         () { return repository;          }
	@Override protected TaskGroupRepositoryJPA taskGroupRepository() { return taskGroupRepository; }
	@Override protected TaskLazyMapper         taskLazyMapper     () { return mapper;              }
}