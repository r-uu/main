# Gantt2 UI-Verbesserungen - Änderungsprotokoll

**Datum:** 2026-02-09  
**Modul:** `de.ruu.app.jeeeraaah.frontend.ui.fx` (gantt2 Package)

## Umgesetzte Anforderungen

### ✅ 1. Checkboxen durch Collapse/Expand-Buttons ersetzen

**Datei:** `GanttTableController.java`

**Vorher:**
```java
private final CheckBox checkBox = new CheckBox();
checkBox.setSelected(row.isExpanded());
```

**Nachher:**
```java
private final Button expandButton = new Button();
expandButton.setText(row.isExpanded() ? "▼" : "▶");
expandButton.setStyle("""
    -fx-background-color: transparent;
    -fx-border-color: transparent;
    -fx-padding: 0;
    -fx-font-size: 14px;
    -fx-cursor: hand;
    """);
```

**Symbole:**
- `▶` - Collapsed (kann expandiert werden)
- `▼` - Expanded (kann kollabiert werden)

**Verhalten:**
- Button ist transparent mit Hand-Cursor
- Nur bei Tasks mit Kindern sichtbar
- Click togglet Expand/Collapse-Status

---

### ✅ 2. Fette Spaltenköpfe normalisieren

**Datei:** `GanttTableController.java` - `initialize()` Methode

**Hinzugefügt:**
```java
// Remove bold font from column headers
ganttTable.setStyle("-fx-font-weight: normal;");
```

**Effekt:**
- Alle Spaltenüberschriften (Task, 01, 02, 03, ...) werden normal dargestellt
- Kein Fettdruck mehr

---

### ✅ 3. Fette Zellen in erster Spalte normalisieren

**Datei:** `GanttTableController.java` - `createColumns()` Methode

**Vorher:**
```java
if (level == 0) {
    // Main tasks: bold
    setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
} else {
    // Subtasks: normal
    setStyle("");
}
```

**Nachher:**
```java
// Normal style for all rows
setStyle("");
```

**Effekt:**
- Alle Task-Namen (Level 0, 1, 2, ...) werden normal dargestellt
- Kein Fettdruck mehr
- Keine Hintergrundfarbe mehr
- Nur Einrückung zur Hierarchie-Darstellung

---

### ✅ 4. Abstand zwischen Group- und Filter-Bereich

**Datei:** `Gantt2.fxml`

**Vorher:**
```xml
<VBox fx:id="vBxForSelector" maxHeight="1.7976931348623157E308" prefWidth="200.0"
   HBox.hgrow="NEVER" />
```

**Nachher:**
```xml
<VBox fx:id="vBxForSelector" maxHeight="1.7976931348623157E308" prefWidth="200.0"
   HBox.hgrow="NEVER">
   <HBox.margin>
      <Insets right="10.0" />
   </HBox.margin>
</VBox>
```

**Effekt:**
- 10 Pixel Abstand zwischen Task Group Selector und Filter-Controls
- Bessere visuelle Trennung

---

## Build-Status

```
✅ BUILD SUCCESS (2026-02-09 18:22:42)

[INFO] r-uu.app.jeeeraaah.frontend.ui.fx .................. SUCCESS [  5.551 s]
[INFO] ------------------------------------------------------------------------
```

## Visuelle Vorher/Nachher

### Expand/Collapse-Spalte

**Vorher:**
```
☑ Feature Set 1
☑ Feature Set 2
  ☑ Feature 2.1
  ☐ Task 2.1.1
```

**Nachher:**
```
▼ Feature Set 1
▼ Feature Set 2
  ▼ Feature 2.1
    Task 2.1.1
```

### Task-Spalte

**Vorher:**
```
**Feature Set 1**      ← Fett, grauer Hintergrund
  Feature 1.1          ← Normal
    Task 1.1.1         ← Normal
```

**Nachher:**
```
Feature Set 1          ← Normal
  Feature 1.1          ← Normal
    Task 1.1.1         ← Normal
```

### Spaltenköpfe

**Vorher:**
```
|   | **Task**  | **01** | **02** | ...
```

**Nachher:**
```
|   | Task  | 01 | 02 | ...
```

### Layout

**Vorher:**
```
[Task Group Selector][Filter Controls       ]
```

**Nachher:**
```
[Task Group Selector]  [Filter Controls      ]
                     ↑ 10px Abstand
```

---

## Testing

**Gantt2AppRunner starten:**
1. In IntelliJ: Rechtsklick auf `Gantt2AppRunner.java` → Run
2. Login: `admin` / `admin`
3. Task Group auswählen: "project jeeeraaah"

**Erwartetes Verhalten:**
- ✅ Expand-Buttons zeigen ▶ wenn collapsed
- ✅ Expand-Buttons zeigen ▼ wenn expanded
- ✅ Click auf Button expandiert/kollabiert Hierarchie
- ✅ Alle Texte normal (nicht fett)
- ✅ Spaltenköpfe normal (nicht fett)
- ✅ Sichtbarer Abstand zwischen Selector und Filter

---

## Geänderte Dateien

### Java
```
root/app/jeeeraaah/frontend/ui/fx/src/main/java/de/ruu/app/jeeeraaah/frontend/ui/fx/task/gantt2/
  └─ GanttTableController.java
     - Zeile 88-148: Checkbox → Button mit ▶/▼
     - Zeile 59: TableView Style für normale Headers
     - Zeile 177-186: Normale Styles für alle Task-Zellen
```

### FXML
```
root/app/jeeeraaah/frontend/ui/fx/src/main/resources/de/ruu/app/jeeeraaah/frontend/ui/fx/task/gantt2/
  └─ Gantt2.fxml
     - Zeile 28-32: Margin zwischen Selector und Filter
```

---

## Zusammenfassung

🎉 **Alle Anforderungen erfolgreich umgesetzt!**

- ✅ **Checkboxen** → **▶/▼ Buttons**
- ✅ **Fette Spaltenköpfe** → **Normal**
- ✅ **Fette Zellen** → **Normal**
- ✅ **Abstand** → **10px zwischen Bereichen**

**Status:** BEREIT ZUM TESTEN 🚀

