package de.ruu.app.jeeeraaah.frontend.ui.fx.dash;

import de.ruu.app.jeeeraaah.common.api.bean.TaskBean;
import de.ruu.app.jeeeraaah.common.api.bean.TaskGroupBean;
import de.ruu.app.jeeeraaah.common.api.domain.TaskGroupLazy;
import de.ruu.app.jeeeraaah.common.api.mapping.lazy.bean.Map_TaskGroup_Lazy_Bean;
import de.ruu.lib.mapstruct.ReferenceCycleTracking;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.NonNull;

import java.util.Collections;
import java.util.Set;

@ApplicationScoped
class MainTaskBeansBuilder
{
	/**
	 * Convert the given lazy parameters to a set of fully populated task beans. The task group lazy is used to create a
	 * task group bean which is then used to create the task beans from the lazy tasks.
	 *
	 * @param groupLazy task group lazy
	 * @return set of task beans representing the main tasks of the group
	 */
	Set<TaskBean> build(@NonNull TaskGroupLazy groupLazy)
	{
		TaskGroupBean groupBean = Map_TaskGroup_Lazy_Bean.INSTANCE.map(groupLazy);
		if (groupBean.mainTasks().isPresent()) return groupBean.mainTasks().get();
		return Collections.emptySet();
	}
}