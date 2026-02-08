package de.ruu.app.jeeeraaah.frontend.ui.fx.task.gantt;

import de.ruu.app.jeeeraaah.common.api.domain.TaskGroupFlat;
import de.ruu.lib.fx.comp.FXCService;
import lombok.NonNull;

import java.time.LocalDate;

/**
 * Service interface for HybridGantt component.
 * <p>
 * Provides methods to populate and configure the hybrid Gantt chart view.
 */
public interface HybridGanttService extends FXCService
{
    /**
     * Populates the Gantt chart with task data for the specified date range.
     *
     * @param taskGroup the task group to display
     * @param start the start date of the visible timeline
     * @param end the end date of the visible timeline
     */
    void populate(TaskGroupFlat taskGroup, @NonNull LocalDate start, @NonNull LocalDate end);
    
    /**
     * Updates the visible date range without reloading data.
     *
     * @param start the new start date
     * @param end the new end date
     */
    void setDateRange(@NonNull LocalDate start, @NonNull LocalDate end);
}
