package de.ruu.app.jeeeraaah.frontend.ui.fx.task.gantt;

import de.ruu.app.jeeeraaah.common.api.bean.TaskBean;
import de.ruu.app.jeeeraaah.common.api.bean.TaskGroupBean;
import de.ruu.app.jeeeraaah.common.api.domain.TaskGroupFlat;
import de.ruu.app.jeeeraaah.frontend.api.client.ws.rs.TaskGroupServiceClient;
import de.ruu.lib.ws.rs.NonTechnicalException;
import de.ruu.lib.ws.rs.TechnicalException;
import de.ruu.lib.fx.comp.FXCController.DefaultFXCController;
import de.ruu.lib.fx.control.dialog.ExceptionDialog;
import jakarta.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static de.ruu.app.jeeeraaah.common.api.domain.Task.COMPARATOR;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
import static javafx.scene.control.SelectionMode.SINGLE;

/**
 * Hybrid Gantt chart controller using TreeTableView for task hierarchy
 * and Canvas for timeline visualization.
 * <p>
 * This approach solves the "frozen column" problem by naturally separating:
 * - Left: TreeTableView with task names (always visible)
 * - Right: Canvas with scrollable timeline
 */
@Slf4j
public class HybridGanttController extends DefaultFXCController<HybridGantt, HybridGanttService>
        implements HybridGanttService {
    
    @FXML private TreeTableView<TaskTreeTableDataItem> taskTree;
    @FXML private ScrollPane timelineScrollPane;
    @FXML private HBox hybridContainer;
    
    @Inject private TaskGroupServiceClient taskGroupServiceClient;
    @Inject private DataItemFactory dataItemFactory;
    
    private GanttTimelineCanvas timelineCanvas;
    private LocalDate currentStartDate;
    private LocalDate currentEndDate;
    
    /**
     * Initializes the hybrid Gantt view
     */
    @FXML
    protected void initialize() {
        // Initialize dates
        currentStartDate = LocalDate.of(2025, 1, 1);
        currentEndDate = LocalDate.of(2025, 3, 31);
        
        // Setup task tree
        setupTaskTree();
        
        // Setup timeline canvas
        setupTimelineCanvas();
        
        // Setup synchronization
        setupSynchronization();
    }
    
    /**
     * Sets up the task tree (left panel)
     */
    private void setupTaskTree() {
        TreeItem<TaskTreeTableDataItem> rootTreeItem = new TreeItem<>();
        taskTree.setRoot(rootTreeItem);
        taskTree.setShowRoot(false);
        
        // Create single column for task names
        TreeTableColumn<TaskTreeTableDataItem, String> nameColumn = 
            new TreeTableColumn<>("Task Name");
        
        nameColumn.setCellValueFactory(cdfs -> {
            TreeItem<TaskTreeTableDataItem> treeItem = cdfs.getValue();
            TaskTreeTableDataItem tableDataItem = treeItem.getValue();
            TaskBean task = tableDataItem.task();
            return new SimpleStringProperty(task.name());
        });
        
        // Make column fill available width
        nameColumn.setPrefWidth(400);
        nameColumn.setMinWidth(200);
        nameColumn.setMaxWidth(Double.MAX_VALUE);
        
        // Visual styling
        nameColumn.setStyle(
            "-fx-background-color: #f5f5f5; " +
            "-fx-border-width: 0 2 0 0; " +
            "-fx-border-color: #cccccc; " +
            "-fx-font-weight: bold;"
        );
        
        taskTree.getColumns().add(nameColumn);
        taskTree.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        taskTree.getSelectionModel().setSelectionMode(SINGLE);
        
        // Set fixed row height to match canvas
        taskTree.setFixedCellSize(GanttTimelineCanvas.getRowHeight());
    }
    
    /**
     * Sets up the timeline canvas (right panel)
     */
    private void setupTimelineCanvas() {
        timelineCanvas = new GanttTimelineCanvas(currentStartDate, currentEndDate);
        timelineCanvas.render();
        
        // Wrap canvas in scrollpane
        if (timelineScrollPane == null) {
            timelineScrollPane = new ScrollPane(timelineCanvas);
            timelineScrollPane.setFitToHeight(true);
            timelineScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            timelineScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            
            // Add to container
            if (hybridContainer != null) {
                HBox.setHgrow(timelineScrollPane, Priority.ALWAYS);
                hybridContainer.getChildren().add(timelineScrollPane);
            }
        } else {
            timelineScrollPane.setContent(timelineCanvas);
        }
    }
    
    /**
     * Sets up synchronization between tree and canvas
     */
    private void setupSynchronization() {
        // Synchronize vertical scrolling
        taskTree.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                // Get the VirtualFlow from the tree (contains scroll position)
                // Note: This is a simplified version - full implementation would
                // require accessing the VirtualFlow through the skin
                log.debug("Task tree skin initialized - ready for scroll synchronization");
            }
        });
        
        // Listen to tree expansion events to update canvas
        taskTree.expandedItemCountProperty().addListener((obs, oldVal, newVal) -> {
            log.debug("Tree expansion changed: {} -> {}", oldVal, newVal);
            updateCanvasFromTree();
        });
        
        // Listen to selection changes
        taskTree.getSelectionModel().selectedItemProperty().addListener(
            (ChangeListener<TreeItem<TaskTreeTableDataItem>>) (obs, old, newValue) -> {
                if (newValue != null) {
                    log.debug("Selected task: {}", newValue.getValue().task().name());
                }
            }
        );
    }
    
    /**
     * Populates the Gantt chart with data
     */
    @Override
    public void populate(TaskGroupFlat taskGroup, @NonNull LocalDate start, @NonNull LocalDate end) {
        currentStartDate = start;
        currentEndDate = end;
        
        // Clear existing data
        taskTree.getRoot().getChildren().clear();
        
        if (isNull(taskGroup)) {
            return;
        }
        
        // Fetch task group data from server
        try {
            Optional<TaskGroupBean> optional =
                taskGroupServiceClient.findWithTasksAndDirectNeighbours(requireNonNull(taskGroup.id()));
            
            if (optional.isPresent()) {
                TaskGroupBean taskGroupBean = optional.get();
                
                // Get main tasks (no super task)
                List<TaskBean> mainTasks = new ArrayList<>();
                
                if (taskGroupBean.tasks().isPresent()) {
                    mainTasks = taskGroupBean.tasks().get().stream()
                        .filter(taskBean -> taskBean.superTask().isEmpty())
                        .collect(Collectors.toList());
                } else {
                    log.warn("No tasks in group: {}", taskGroupBean.name());
                }
                
                mainTasks.sort(COMPARATOR);
                
                // Populate tree
                mainTasks.forEach(mainTask -> 
                    taskTree.getRoot().getChildren().add(
                        populateTreeNode(mainTask, start, end)
                    )
                );
                
                // Update canvas
                updateCanvasFromTree();
                
                log.debug("Hybrid Gantt populated with {} main tasks", mainTasks.size());
            }
        } catch (TechnicalException | NonTechnicalException e) {
            ExceptionDialog.showAndWait("Failed to fetch task group from backend", e);
        }
    }
    
    /**
     * Creates a tree node for a task and its subtasks
     */
    private TreeItem<TaskTreeTableDataItem> populateTreeNode(
        TaskBean task, 
        @NonNull LocalDate start,
        @NonNull LocalDate end
    ) {
        return populateTreeNode(task, start, end, new HashSet<>());
    }
    
    /**
     * Creates a tree node with circular reference protection
     */
    private TreeItem<TaskTreeTableDataItem> populateTreeNode(
        TaskBean task,
        @NonNull LocalDate start,
        @NonNull LocalDate end,
        Set<Long> processedTaskIds
    ) {
        // Prevent infinite recursion
        if (task.id() != null && processedTaskIds.contains(task.id())) {
            log.warn("Circular reference detected for task: {} (ID: {})", 
                task.name(), task.id());
            return null;
        }
        
        if (task.id() != null) {
            processedTaskIds.add(task.id());
        }
        
        TaskTreeTableDataItem dataItem = new TaskTreeTableDataItem(task, start, end);
        TreeItem<TaskTreeTableDataItem> result = new TreeItem<>(dataItem);
        
        // Add subtasks
        if (task.subTasks().isPresent()) {
            task.subTasks().get().forEach(subTask -> {
                TreeItem<TaskTreeTableDataItem> childNode = 
                    populateTreeNode(subTask, start, end, processedTaskIds);
                if (childNode != null) {
                    result.getChildren().add(childNode);
                }
            });
        }
        
        return result;
    }
    
    /**
     * Updates the canvas to match the current tree state
     */
    private void updateCanvasFromTree() {
        List<GanttTimelineCanvas.TaskRowData> rows = new ArrayList<>();
        
        // Traverse visible tree items and create canvas rows
        traverseVisibleItems(taskTree.getRoot(), rows, 0);
        
        // Update canvas
        timelineCanvas.setDateRange(currentStartDate, currentEndDate);
        timelineCanvas.setTaskRows(rows);
        timelineCanvas.render();
        
        log.debug("Canvas updated with {} visible rows", rows.size());
    }
    
    /**
     * Recursively traverses visible tree items
     */
    private void traverseVisibleItems(
        TreeItem<TaskTreeTableDataItem> item,
        List<GanttTimelineCanvas.TaskRowData> rows,
        int level
    ) {
        for (TreeItem<TaskTreeTableDataItem> child : item.getChildren()) {
            TaskTreeTableDataItem dataItem = child.getValue();
            boolean visible = true;  // Root level children are always visible
            
            rows.add(new GanttTimelineCanvas.TaskRowData(
                dataItem.task(),
                level,
                visible
            ));
            
            // If expanded, traverse children
            if (child.isExpanded() && !child.getChildren().isEmpty()) {
                traverseVisibleItems(child, rows, level + 1);
            }
        }
    }
    
    /**
     * Updates the date range filter
     */
    @Override
    public void setDateRange(@NonNull LocalDate start, @NonNull LocalDate end) {
        currentStartDate = start;
        currentEndDate = end;
        
        // Refresh canvas with new date range
        if (timelineCanvas != null) {
            updateCanvasFromTree();
        }
    }
}
