# IntelliJ Maven Setup Guide

**Datum:** 2026-01-11  
**Problem:** Maven Build funktioniert nicht richtig in IntelliJ  
**Lösung:** Maven Wrapper aktivieren und Build an Maven delegieren

---

## 🎯 Schritt-für-Schritt Anleitung

### 1. Maven Home auf Maven Wrapper setzen

**Navigation:**
```
File → Settings → Build, Execution, Deployment → Build Tools → Maven
```

**Einstellungen:**
- **Maven home path:** Wähle `Use Maven wrapper` aus dem Dropdown
  - Oder gib manuell ein: `\\wsl.localhost\Ubuntu\home\r-uu\develop\github\main\.mvn`

### 2. Build Actions an Maven delegieren

**Navigation:**
```
File → Settings → Build, Execution, Deployment → Build Tools → Maven → Runner
```

**Einstellungen:**
- ☑️ **Delegate IDE build/run actions to Maven** aktivieren
- **JRE:** Wähle `GraalVM 25` (falls verfügbar)

### 3. Maven User Settings (WSL-Pfade)

**Navigation:**
```
File → Settings → Build, Execution, Deployment → Build Tools → Maven
```

**Einstellungen:**
- **User settings file:** `\\wsl.localhost\Ubuntu\home\r-uu\.m2\settings.xml` (falls vorhanden)
- **Local repository:** `\\wsl.localhost\Ubuntu\home\r-uu\.m2\repository`

### 4. Annotation Processing aktivieren

**Navigation:**
```
File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
```

**Einstellungen:**
- ☑️ **Enable annotation processing** aktivieren
- **Obtain processors from project classpath** auswählen

### 5. Lombok Plugin installieren (falls nicht vorhanden)

**Navigation:**
```
File → Settings → Plugins
```

**Aktion:**
- Suche nach "Lombok"
- Installiere "Lombok" Plugin
- IntelliJ neustarten

### 6. Maven Projekte neu laden

**Aktion:**
- Rechtsklick auf `root/pom.xml` → **Maven → Reload Project**
- Oder: Maven Tool Window → Reload Button (🔄)

---

## ✅ Validierung

### Nach der Konfiguration sollte folgendes funktionieren:

1. **Maven Build aus IntelliJ:**
   ```
   Maven Tool Window → r-uu.root → Lifecycle → clean → install
   ```

2. **Terminal Build (in WSL):**
   ```bash
   cd /home/r-uu/develop/github/main/root
   mvn clean install
   ```

3. **Lombok sollte funktionieren:**
   - `@Slf4j` generiert `log` Feld
   - `@Getter` / `@Setter` generieren Getter/Setter
   - `@Accessors(fluent = true)` generiert fluent style Methoden

---

## 🔍 Troubleshooting

### Problem: "JAVA_HOME not defined correctly"

**Lösung in WSL:**
```bash
# Prüfe JAVA_HOME
echo $JAVA_HOME

# Sollte zeigen:
/usr/lib/graalvm/graalvm-jdk-25

# Falls nicht, füge zu ~/.bashrc hinzu:
export JAVA_HOME=/usr/lib/graalvm/graalvm-jdk-25
export PATH=$JAVA_HOME/bin:$PATH

# Dann:
source ~/.bashrc
```

### Problem: Lombok funktioniert nicht

**Lösung:**
1. Prüfe ob Lombok Plugin installiert ist
2. Prüfe ob Annotation Processing aktiviert ist
3. Maven Projekt neu laden
4. IntelliJ Cache löschen: `File → Invalidate Caches → Invalidate and Restart`

### Problem: Build Fehler "cannot find symbol: variable log"

**Ursache:** Lombok @Slf4j wird nicht verarbeitet

**Lösung:**
1. Stelle sicher, dass Maven Compiler Plugin die Lombok Annotation Processor Paths hat (siehe `bom/pom.xml`)
2. Maven clean install vom Terminal aus:
   ```bash
   cd /home/r-uu/develop/github/main/root
   mvn clean install
   ```
3. Wenn das funktioniert, ist es ein IntelliJ-Problem → siehe oben

### Problem: IntelliJ verwendet altes JDK

**Lösung:**
```
File → Project Structure → Project Settings → Project
```
- **SDK:** Wähle GraalVM 25
- **Language level:** 25 (Preview)

---

## 📋 Checkliste

Nach der Konfiguration sollten alle Punkte ✅ sein:

- [ ] Maven Wrapper ist konfiguriert
- [ ] "Delegate IDE build/run actions to Maven" ist aktiviert
- [ ] Annotation Processing ist aktiviert
- [ ] Lombok Plugin ist installiert
- [ ] Maven Projekte wurden neu geladen
- [ ] `mvn clean install` funktioniert im Terminal (WSL)
- [ ] `mvn clean install` funktioniert in IntelliJ Maven Tool Window
- [ ] Keine Compiler-Fehler in `r-uu.lib.archunit`

---

## 🚀 Empfohlene IntelliJ Maven Settings (Vollständig)

### Settings → Build Tools → Maven

```
Maven home path: Use Maven wrapper
User settings file: \\wsl.localhost\Ubuntu\home\r-uu\.m2\settings.xml
Local repository: \\wsl.localhost\Ubuntu\home\r-uu\.m2\repository
☑️ Use settings from .mvn/maven.config
☑️ Work offline (optional, für schnelleren Build)
```

### Settings → Build Tools → Maven → Runner

```
☑️ Delegate IDE build/run actions to Maven
JRE: GraalVM 25
VM Options: -Xmx2g (optional, für größeren Heap)
```

### Settings → Compiler → Annotation Processors

```
☑️ Enable annotation processing
Processor path: Obtain processors from project classpath
Generated sources directory: target/generated-sources/annotations
```

---

## 📚 Weitere Informationen

- **Maven Wrapper Dokumentation:** [maven-wrapper](https://github.com/takari/maven-wrapper)
- **Lombok IntelliJ Plugin:** [lombok-plugin](https://plugins.jetbrains.com/plugin/6317-lombok)
- **GraalVM 25 Migration:** `config/GRAALVM-25-MIGRATION.md`

---

**Hinweis:** Nach diesen Änderungen sollte der Build sowohl im Terminal als auch in IntelliJ funktionieren!

