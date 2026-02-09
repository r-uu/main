#!/bin/bash
# Script to remove deprecated gantt package (replaced by gantt2)
# Date: 2026-02-09

set -e

BASE_DIR="/home/r-uu/develop/github/main/root/app/jeeeraaah/frontend/ui/fx"

echo "🗑️  Removing deprecated gantt package..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Java source files
JAVA_DIR="$BASE_DIR/src/main/java/de/ruu/app/jeeeraaah/frontend/ui/fx/task/gantt"
if [ -d "$JAVA_DIR" ]; then
    echo "📂 Removing Java sources: $JAVA_DIR"
    rm -rf "$JAVA_DIR"
    echo "   ✅ Java sources removed"
else
    echo "   ⚠️  Directory not found: $JAVA_DIR"
fi

# FXML resources
FXML_DIR="$BASE_DIR/src/main/resources/de/ruu/app/jeeeraaah/frontend/ui/fx/task/gantt"
if [ -d "$FXML_DIR" ]; then
    echo "📂 Removing FXML resources: $FXML_DIR"
    rm -rf "$FXML_DIR"
    echo "   ✅ FXML resources removed"
else
    echo "   ⚠️  Directory not found: $FXML_DIR"
fi

# Test files
TEST_DIR="$BASE_DIR/src/test/java/de/ruu/app/jeeeraaah/frontend/ui/fx/task/gantt"
if [ -d "$TEST_DIR" ]; then
    echo "📂 Removing test files: $TEST_DIR"
    rm -rf "$TEST_DIR"
    echo "   ✅ Test files removed"
else
    echo "   ⚠️  Directory not found: $TEST_DIR"
fi

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ Cleanup complete!"
echo ""
echo "Files removed:"
echo "  • GanttApp.java (obsolete - replaced by Gantt2App)"
echo "  • GanttAppRunner.java"
echo "  • GanttController.java"
echo "  • GanttService.java"
echo "  • Gantt.java"
echo "  • TaskTreeTable.java"
echo "  • TaskTreeTableController.java"
echo "  • TaskTreeTableService.java"
echo "  • TaskTreeTableApp.java"
echo "  • TaskTreeTableAppRunner.java"
echo "  • TaskTreeTableColumn.java"
echo "  • TaskTreeTableDataItem.java"
echo "  • TaskTreeTableCellData.java"
echo "  • TaskTreeTableCellValueFactory.java"
echo "  • DataItemFactory.java"
echo "  • TaskFactory.java"
echo "  • TaskUtil.java"
echo "  • Gantt.fxml"
echo "  • TaskTreeTable.fxml"
echo "  • FXComponentBundleGenerator.java (test)"
echo ""
echo "Next steps:"
echo "  1. Compile to verify: mvn compile -DskipTests"
echo "  2. Run Gantt2AppRunner to test"
echo "  3. Commit changes: git add -A && git commit -m 'Remove deprecated gantt package'"

