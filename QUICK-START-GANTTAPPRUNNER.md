# 🚀 Quick Start: GanttAppRunner in IntelliJ
## ✅ PROBLEM GELÖST
Die IntelliJ Run Configuration für **GanttAppRunner** wurde repariert!
---
## 📍 Was zu tun ist:
### 1. IntelliJ IDEA neu starten
```
File → Exit
(IntelliJ neu öffnen)
```
### 2. Run Configuration auswählen
In der Toolbar oben rechts sollte jetzt zu sehen sein:
- **GanttAppRunner** ✅
- **DashAppRunner** ✅
### 3. Starten
Klicke auf den **grünen Play-Button** ▶️
---
## 🔍 Falls nicht sichtbar:
### Cache neu laden:
```
File → Invalidate Caches...
→ Invalidate and Restart
```
### Maven neu laden:
```
Maven Tool Window (rechts) → Reload All Maven Projects (Kreispfeil)
```
---
## 📋 Was wurde repariert?
Zwei neue JPMS-konforme Run Configurations erstellt:
- `.idea/runConfigurations/GanttAppRunner.xml`
- `.idea/runConfigurations/DashAppRunner.xml`
**JPMS-Features:**
- ✅ Module-Path korrekt gesetzt
- ✅ `--add-modules org.slf4j` für Logging
- ✅ `--add-opens` für JavaFX + CDI Reflection
- ✅ UTF-8 Encoding für Umlaute
- ✅ GraalVM JDK 25 konfiguriert
---
## 🐛 Falls Probleme:
### Maven als Fallback:
```bash
cd ~/develop/github/main/root/app/jeeeraaah/frontend/ui/fx
mvn clean compile exec:java \
  -Dexec.mainClass="de.ruu.app.jeeeraaah.frontend.ui.fx.task.gantt.GanttAppRunner"
```
---
## ✅ Ergebnis:
Nach IntelliJ-Neustart:
- **Run Configuration Dropdown** zeigt "GanttAppRunner"
- **Play-Button** startet die Anwendung
- **JPMS-konform** - alle Module korrekt geladen
**Viel Erfolg!** 🎉
---
**Siehe auch:** `GANTTAPPRUNNER-INTELLIJ-FIX.md` für Details
