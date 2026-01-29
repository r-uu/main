# DashAppRunner Start-Optionen

## Übersicht

Es gibt **drei Möglichkeiten**, den DashAppRunner zu starten:

### 1. IntelliJ Run Configuration (JPMS-konform)

**Datei**: `.idea/runConfigurations/DashAppRunner__JPMS_.xml`

**Vorteile**:
- ✅ Direktes Debugging in IntelliJ
- ✅ Breakpoints funktionieren perfekt
- ✅ Hot Swap möglich
- ✅ Schneller Start

**Nachteile**:
- ⚠️ IntelliJ fügt automatisch `-cp` hinzu (Hybrid-Modus)
- ⚠️ Nicht 100% reines JPMS

**Verwendung**:
1. IntelliJ öffnen
2. Run → Edit Configurations
3. "DashAppRunner (JPMS)" auswählen
4. Run/Debug Button klicken

**Generierter Befehl** (vereinfacht):
```bash
java \
  -cp <alle-dependencies> \
  --add-modules org.slf4j \
  --add-reads de.ruu.app.jeeeraaah.frontend.ui.fx=ALL-UNNAMED \
  -Dglass.gtk.uiScale=1.5 \
  de.ruu.app.jeeeraaah.frontend.ui.fx.dash.DashAppRunner
```

### 2. Maven Exec Plugin

**Datei**: `.idea/runConfigurations/DashAppRunner__Maven_Exec_.xml`

**Vorteile**:
- ✅ Vollständig JPMS-konform
- ✅ Konsistent mit CI/CD
- ✅ Keine IDE-spezifischen Konfigurationen

**Nachteile**:
- ⚠️ Langsamerer Start (Maven Overhead)
- ⚠️ Debugging komplizierter

**Verwendung in IntelliJ**:
1. Run → Edit Configurations
2. "DashAppRunner (Maven Exec)" auswählen
3. Run/Debug Button klicken

**Verwendung in Terminal**:
```bash
cd /home/r-uu/develop/github/main/root/app/jeeeraaah/frontend/ui/fx
mvn exec:java
```

**Maven Configuration** (in pom.xml):
```xml
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>exec-maven-plugin</artifactId>
  <configuration>
    <mainClass>de.ruu.app.jeeeraaah.frontend.ui.fx.dash.DashAppRunner</mainClass>
  </configuration>
</plugin>
```

### 3. Direkter Java-Aufruf (manuell)

**Für Experten**: Vollständige Kontrolle über alle Parameter

**Verwendung**:
```bash
cd /home/r-uu/develop/github/main

# Stelle sicher, dass alles kompiliert ist
mvn -f root/pom.xml clean install -DskipTests

# Starte mit vollständigem Module Path
java \
  --module-path <kompletter-module-path> \
  --add-modules org.slf4j \
  --add-reads de.ruu.app.jeeeraaah.frontend.ui.fx=ALL-UNNAMED \
  -Dglass.gtk.uiScale=1.5 \
  -Dfile.encoding=UTF-8 \
  -m de.ruu.app.jeeeraaah.frontend.ui.fx/de.ruu.app.jeeeraaah.frontend.ui.fx.dash.DashAppRunner
```

## Empfehlung

### Für Development/Debugging:
→ **IntelliJ Run Configuration (JPMS)**

### Für Testing/CI:
→ **Maven Exec Plugin**

### Für Produktion:
→ **Direkter Java-Aufruf** mit jlink/jpackage

## Warum verwendet IntelliJ `-cp`?

IntelliJ IDEA verwendet automatisch den Classpath, weil:

1. **Bessere IDE-Integration**: Code Completion, Hot Swap, etc.
2. **Maven/Gradle Dependencies**: Einfachere Dependency-Auflösung
3. **Mixed-Mode Support**: Viele Projekte mischen Classpath und Module Path

**Aber**: Die `--add-modules` und `--add-reads` Parameter sorgen dafür, dass JPMS korrekt funktioniert!

## Debugging

### IntelliJ Run Configuration
1. Setze Breakpoints
2. Klicke Debug Button
3. Fertig!

### Maven Exec Plugin
1. Füge in `pom.xml` hinzu:
```xml
<configuration>
  <arguments>
    <argument>-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005</argument>
  </arguments>
</configuration>
```
2. Starte: `mvn exec:java`
3. In IntelliJ: Run → Attach to Process → Port 5005

## Bekannte Probleme

### "Module org.slf4j not found"
→ Lösung: `--add-modules org.slf4j` in VM-Parametern

### "Package ... does not exist"
→ Lösung: `--add-reads <dein-modul>=ALL-UNNAMED` in VM-Parametern

### Configuration Validation Failed
→ Lösung: `testing.properties` im Projekt-Root vorhanden?

## Weitere Informationen

- [INTELLIJ-JPMS-RUN-CONFIGURATION.md](INTELLIJ-JPMS-RUN-CONFIGURATION.md) - Detaillierte technische Dokumentation
- [JEP 261: Module System](https://openjdk.org/jeps/261) - Java Module System Spezifikation
- [IntelliJ IDEA - Java 9+ Support](https://www.jetbrains.com/help/idea/java-9-support.html)

## Quick Start

**Schnellster Weg zum Starten**:

1. IntelliJ öffnen
2. `Shift + Shift` drücken
3. "DashAppRunner (JPMS)" tippen
4. Enter drücken
5. ✅ Fertig!
