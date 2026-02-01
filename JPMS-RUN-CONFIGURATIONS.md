# JPMS Run-Konfigurationen - Dokumentation

## Problem

Beim Versuch, Java-Anwendungen mit JPMS (Java Platform Module System) aus IntelliJ zu starten, traten Fehler auf:
```
Error occurred during initialization of boot layer
java.lang.module.FindException: Module jakarta.annotation not found, required by jakarta.cdi
```

## Ursache

1. **Falscher Scope in POMs**: `jakarta.annotation-api` war in vielen POMs mit `<scope>provided</scope>` deklariert. Dies funktioniert nicht mit JPMS, da das Modul zur Laufzeit benötigt wird.

2. **Classpath vs. Module Path**: IntelliJ verwendete teilweise den Classpath anstatt den Module Path, was zu Inkonsistenzen führte.

## Lösung

### 1. POM-Dateien korrigiert

Der Scope `provided` wurde von `jakarta.annotation-api` in folgenden Modulen entfernt:
- `r-uu.app.jeeeraaah.frontend.api.client.ws.rs`
- `r-uu.app.jeeeraaah.common.api.domain`
- `r-uu.app.jeeeraaah.common.api.bean`
- `r-uu.app.jeeeraaah.frontend.ui.fx.model`
- `r-uu.lib.util`
- `r-uu.lib.mapstruct`
- `r-uu.lib.junit`

Jetzt ist die Abhängigkeit eine normale compile-Abhängigkeit:
```xml
<dependency>
    <groupId>jakarta.annotation</groupId>
    <artifactId>jakarta.annotation-api</artifactId>
</dependency>
```

### 2. module-info.java aktualisiert

In `de.ruu.app.jeeeraaah.frontend.api.client.ws.rs/module-info.java`:
```java
// external libraries jakarta
requires jakarta.annotation;  // Jetzt als normale requires-Anweisung
requires jakarta.inject;
requires jakarta.cdi;
requires jakarta.ws.rs;
```

### 3. JPMS-konforme Run-Konfigurationen erstellt

Zwei neue IntelliJ Run-Konfigurationen wurden im `.run` Ordner erstellt:

#### DashAppRunner (JPMS).run.xml
- Startet die Haupt-GUI-Anwendung
- Verwendet konsequent den Module Path
- Keine --add-modules Parameter mehr nötig
- VM-Parameter: `-Dglass.gtk.uiScale=1.5`

#### DBClean (JPMS).run.xml
- Startet das DB-Clean-Tool
- Verwendet konsequent den Module Path
- Keine --add-modules Parameter mehr nötig

### 4. Verwendung

In IntelliJ:
1. Run > Edit Configurations
2. Wähle "DashAppRunner (JPMS)" oder "DBClean (JPMS)"
3. Klicke "Run" oder "Debug"

Die Konfigurationen sind git-versioniert und sollten automatisch für alle Team-Mitglieder verfügbar sein.

## Wichtige Prinzipien für JPMS

1. **Keine `provided` Scope für Module-Abhängigkeiten**: Wenn ein Modul in `module-info.java` mit `requires` deklariert ist, muss die entsprechende Maven-Abhängigkeit `compile` Scope haben.

2. **Kein --add-modules wenn möglich**: Besser ist es, Module explizit als Abhängigkeiten zu deklarieren.

3. **Module Path über Classpath**: JPMS-Anwendungen sollten konsequent den Module Path verwenden.

4. **IntelliJ Module-Einstellung**: Stelle sicher, dass "Use classpath of module" deaktiviert ist für JPMS-Konfigurationen.

## Validierung

Build erfolgreich mit:
```bash
cd /home/r-uu/develop/github/main/root
mvn clean install -DskipTests
```

Ergebnis: BUILD SUCCESS (03:36 min)

## Zukünftige Änderungen

Falls weitere Module `jakarta.annotation` benötigen:
1. Füge `jakarta.annotation-api` als normale (compile) Abhängigkeit in der POM hinzu
2. Füge `requires jakarta.annotation;` in module-info.java hinzu
3. Baue das Projekt neu
4. Keine VM-Parameter oder --add-modules nötig

Stand: 2026-02-01
