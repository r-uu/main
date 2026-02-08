package de.ruu.app.jeeeraaah.frontend.ui.fx.task.gantt;

import de.ruu.lib.fx.comp.FXComponent;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Hybrid Gantt Chart FX Component.
 * <p>
 * Combines TreeTableView (left, for task hierarchy) with Canvas-based timeline (right, for dates).
 * This approach naturally solves the "frozen column" problem by separating concerns.
 */
@Slf4j
@Dependent
public class HybridGantt extends FXComponent<HybridGanttController, HybridGanttService>
{
    @Inject private HybridGanttController controller;
    
    @Override public Class<HybridGanttController> typeController() { 
        return HybridGanttController.class; 
    }
    
    @Override protected HybridGanttController controller() { 
        return controller; 
    }
}
