# IntelliJ IDEA Run Configuration für JPMS-Module

## Problem
Bei einem JPMS-Projekt sollten wir **nicht** mit Classpath (`-cp`) und `--add-modules` arbeiten, 
sondern mit dem **Module Path** (`-p` oder `--module-path`).

## Lösung: Automatische Konfiguration über `.mvn/jvm.config`

Die Datei `.mvn/jvm.config` im Modul-Verzeichnis wird **automatisch** von Maven und IntelliJ IDEA 
gelesen und angewendet. Dadurch benötigen wir **keine manuellen Run Configuration Anpassungen**.

### Standort
```
root/app/jeeeraaah/frontend/ui/fx/.mvn/jvm.config
```

### Inhalt
```properties
# Add automatic modules (libraries without module-info.java)
--add-modules jakarta.annotation,jakarta.inject,jakarta.enterprise.cdi,org.slf4j,org.jboss.logging

# Required for Weld CDI to access JDK internals for proxy generation
--add-opens java.base/java.lang=org.jboss.weld.se.shaded
--add-opens java.base/java.util=org.jboss.weld.se.shaded

# Enable native access for JavaFX native libraries
--enable-native-access=javafx.graphics
```

## IntelliJ Run Configuration erstellen

### 1. Application Configuration erstellen
- **Right-click** auf `DashAppRunner.java`
- **Run 'DashAppRunner.main()'**
- IntelliJ erstellt automatisch eine Run Configuration

### 2. Die Configuration nutzt automatisch
- ✅ **Module Path** (nicht Classpath) - konfiguriert durch Maven/JPMS
- ✅ **JVM Options** aus `.mvn/jvm.config`
- ✅ **Module Dependencies** aus `module-info.java`

### 3. Manuelle Anpassungen (falls nötig)
Falls IntelliJ die Configuration nicht korrekt erstellt:

1. **Run → Edit Configurations...**
2. **Use classpath of module:** `de.ruu.app.jeeeraaah.frontend.ui.fx`
3. **Build and run:** `<Default> (Module Path)`
4. **Add VM options:** sollte leer sein (wird aus `.mvn/jvm.config` gelesen)

## Vorteile dieser Lösung

### ✅ Single Point of Truth
- Alle JVM-Optionen in **einer** Datei: `.mvn/jvm.config`
- Gilt für Maven **und** IntelliJ
- Keine doppelte Pflege

### ✅ JPMS-konform
- Verwendet **Module Path** statt Classpath
- Automatische Module werden explizit deklariert
- Proper module resolution

### ✅ Wartbar
- Änderungen an JVM-Optionen nur an **einer** Stelle
- Versionskontrolle durch Git
- Team-weit konsistent

## Debugging

### Module Resolution prüfen
Uncomment in `.mvn/jvm.config`:
```properties
--show-module-resolution
```

### Module Graph visualisieren
```bash
jdeps --module-path target/classes --print-module-deps --multi-release 17 target/classes
```

## Häufige Fehler

### Error: "Module X not found"
- **Ursache:** Automatisches Modul fehlt in `--add-modules`
- **Lösung:** Ergänze in `.mvn/jvm.config`

### Error: "Unable to make field accessible"
- **Ursache:** CDI/Reflection benötigt Zugriff auf JDK-Internals
- **Lösung:** Ergänze `--add-opens` in `.mvn/jvm.config`

### Error: "Restricted method called"
- **Ursache:** JavaFX native Zugriff
- **Lösung:** `--enable-native-access=javafx.graphics` in `.mvn/jvm.config`

## Weitere Informationen
- [JPMS Dokumentation](https://openjdk.org/projects/jigsaw/quick-start)
- [Maven JVM Config](https://maven.apache.org/configure.html)
- [IntelliJ JPMS Support](https://www.jetbrains.com/help/idea/java-platform-module-system.html)
