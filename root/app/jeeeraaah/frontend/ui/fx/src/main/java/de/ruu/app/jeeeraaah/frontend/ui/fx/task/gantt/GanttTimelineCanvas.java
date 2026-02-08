package de.ruu.app.jeeeraaah.frontend.ui.fx.task.gantt;

import de.ruu.app.jeeeraaah.common.api.bean.TaskBean;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Canvas-based timeline renderer for Gantt chart.
 * <p>
 * This component renders:
 * - Date headers (day/month labels)
 * - Task bars with colors based on task timeline
 * - Grid lines for visual separation
 * <p>
 * Designed to work alongside TreeTableView showing task names (hybrid approach).
 */
@Slf4j
public class GanttTimelineCanvas extends Canvas {
    
    // Visual constants
    private static final double COLUMN_WIDTH = 30.0;  // Width of each day column
    private static final double ROW_HEIGHT = 24.0;    // Height of each task row
    private static final double HEADER_HEIGHT = 40.0; // Height of date header
    
    private static final Color GRID_COLOR = Color.rgb(220, 220, 220);
    private static final Color HEADER_BG = Color.rgb(245, 245, 245);
    private static final Color TASK_BAR_COLOR = Color.rgb(100, 150, 200);
    
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("dd");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMM");
    
    // Data model
    private LocalDate startDate;
    private LocalDate endDate;
    private List<TaskRowData> taskRows = new ArrayList<>();
    
    /**
     * Represents a single row in the timeline (corresponds to a tree row)
     */
    public static class TaskRowData {
        public final TaskBean task;
        public final int level;  // Indentation level in tree
        public final boolean visible;  // Whether row is visible (parent expanded)
        
        public TaskRowData(TaskBean task, int level, boolean visible) {
            this.task = task;
            this.level = level;
            this.visible = visible;
        }
    }
    
    public GanttTimelineCanvas(LocalDate startDate, LocalDate endDate) {
        super();
        this.startDate = startDate;
        this.endDate = endDate;
        
        // Calculate initial canvas size
        updateCanvasSize();
    }
    
    /**
     * Sets the date range for the timeline
     */
    public void setDateRange(LocalDate start, LocalDate end) {
        this.startDate = start;
        this.endDate = end;
        updateCanvasSize();
        render();
    }
    
    /**
     * Sets the task data to render
     */
    public void setTaskRows(List<TaskRowData> rows) {
        this.taskRows = rows;
        updateCanvasSize();
        render();
    }
    
    /**
     * Updates canvas size based on date range and number of visible rows
     */
    private void updateCanvasSize() {
        if (startDate == null || endDate == null) {
            return;
        }
        
        // Calculate number of days
        long dayCount = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        // Calculate visible rows
        long visibleRows = taskRows.stream().filter(row -> row.visible).count();
        
        // Set canvas size
        double width = dayCount * COLUMN_WIDTH;
        double height = HEADER_HEIGHT + (visibleRows * ROW_HEIGHT);
        
        setWidth(width);
        setHeight(height);
    }
    
    /**
     * Renders the entire timeline canvas
     */
    public void render() {
        GraphicsContext gc = getGraphicsContext2D();
        
        // Clear canvas
        gc.clearRect(0, 0, getWidth(), getHeight());
        
        // Draw header
        drawDateHeader(gc);
        
        // Draw task rows
        drawTaskRows(gc);
        
        // Draw grid
        drawGrid(gc);
    }
    
    /**
     * Draws the date header with day and month labels
     */
    private void drawDateHeader(GraphicsContext gc) {
        // Background
        gc.setFill(HEADER_BG);
        gc.fillRect(0, 0, getWidth(), HEADER_HEIGHT);
        
        // Date labels
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("System", 10));
        gc.setTextAlign(TextAlignment.CENTER);
        
        LocalDate currentDate = startDate;
        int dayIndex = 0;
        String currentMonth = null;
        
        while (!currentDate.isAfter(endDate)) {
            double x = dayIndex * COLUMN_WIDTH;
            
            // Draw day number
            String dayText = currentDate.format(DAY_FORMATTER);
            gc.fillText(dayText, x + COLUMN_WIDTH / 2, HEADER_HEIGHT - 10);
            
            // Draw month label on first day of month or first column
            String month = currentDate.format(MONTH_FORMATTER);
            if (!month.equals(currentMonth)) {
                gc.setFont(Font.font("System", 11));
                gc.fillText(month, x + COLUMN_WIDTH / 2, 15);
                gc.setFont(Font.font("System", 10));
                currentMonth = month;
            }
            
            currentDate = currentDate.plusDays(1);
            dayIndex++;
        }
    }
    
    /**
     * Draws task bars for all visible tasks
     */
    private void drawTaskRows(GraphicsContext gc) {
        int visibleRowIndex = 0;
        
        for (TaskRowData rowData : taskRows) {
            if (!rowData.visible) {
                continue; // Skip hidden rows
            }
            
            double y = HEADER_HEIGHT + (visibleRowIndex * ROW_HEIGHT);
            
            // Draw task bar if task has dates
            if (rowData.task.start().isPresent() && rowData.task.end().isPresent()) {
                drawTaskBar(gc, rowData.task, y);
            }
            
            visibleRowIndex++;
        }
    }
    
    /**
     * Draws a single task bar
     */
    private void drawTaskBar(GraphicsContext gc, TaskBean task, double y) {
        LocalDate taskStart = task.start().get();
        LocalDate taskEnd = task.end().get();
        
        // Calculate bar position and size
        long startDayOffset = java.time.temporal.ChronoUnit.DAYS.between(startDate, taskStart);
        long durationDays = java.time.temporal.ChronoUnit.DAYS.between(taskStart, taskEnd) + 1;
        
        // Only draw if task overlaps with visible date range
        if (taskEnd.isBefore(startDate) || taskStart.isAfter(endDate)) {
            return;
        }
        
        // Clamp to visible range
        long clampedStartOffset = Math.max(0, startDayOffset);
        long clampedEndOffset = Math.min(
            java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate),
            startDayOffset + durationDays - 1
        );
        long clampedDuration = clampedEndOffset - clampedStartOffset + 1;
        
        double x = clampedStartOffset * COLUMN_WIDTH + 2;  // +2 for padding
        double width = clampedDuration * COLUMN_WIDTH - 4;  // -4 for padding
        double barHeight = ROW_HEIGHT - 6;  // Leave some vertical padding
        
        // Draw task bar
        gc.setFill(TASK_BAR_COLOR);
        gc.fillRoundRect(x, y + 3, width, barHeight, 3, 3);
        
        // Draw task bar border
        gc.setStroke(Color.rgb(70, 120, 170));
        gc.setLineWidth(1);
        gc.strokeRoundRect(x, y + 3, width, barHeight, 3, 3);
    }
    
    /**
     * Draws grid lines
     */
    private void drawGrid(GraphicsContext gc) {
        gc.setStroke(GRID_COLOR);
        gc.setLineWidth(1);
        
        // Vertical grid lines (day separators)
        long dayCount = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        for (int i = 0; i <= dayCount; i++) {
            double x = i * COLUMN_WIDTH;
            gc.strokeLine(x, 0, x, getHeight());
        }
        
        // Horizontal grid lines (row separators)
        long visibleRows = taskRows.stream().filter(row -> row.visible).count();
        for (int i = 0; i <= visibleRows; i++) {
            double y = HEADER_HEIGHT + (i * ROW_HEIGHT);
            gc.strokeLine(0, y, getWidth(), y);
        }
        
        // Header separator (thicker)
        gc.setLineWidth(2);
        gc.strokeLine(0, HEADER_HEIGHT, getWidth(), HEADER_HEIGHT);
    }
    
    /**
     * Gets the row height for synchronization with TreeTableView
     */
    public static double getRowHeight() {
        return ROW_HEIGHT;
    }
    
    /**
     * Gets the header height for layout purposes
     */
    public static double getHeaderHeight() {
        return HEADER_HEIGHT;
    }
}
