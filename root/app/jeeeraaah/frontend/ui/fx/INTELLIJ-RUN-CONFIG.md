# IntelliJ IDEA Run-Konfiguration für DashAppRunner

## Problem

Beim Start von `DashAppRunner` aus IntelliJ IDEA kann folgender Fehler auftreten:

```
Error occurred during initialization of boot layer
java.lang.module.FindException: Module jakarta.annotation not found, required by de.ruu.app.jeeeraaah.frontend.ui.fx
```

## Ursache

IntelliJ IDEA platziert manchmal Jakarta-API-JARs (`jakarta.annotation-api`, `jakarta.inject-api`) im **Classpath** statt im **Modulpfad**. Dies führt zu einem Konflikt mit dem Java Platform Module System (JPMS).

## Automatische Lösung (Empfohlen)

Das Projekt enthält bereits die notwendigen Konfigurationen:

1. **Maven JVM Config**: `.mvn/jvm.config` mit `--add-modules` Argumenten
2. **IntelliJ Run Configuration**: `.idea/runConfigurations/DashAppRunner.xml` mit VM-Optionen

Nach einem `git pull` sollte die Run-Configuration automatisch in IntelliJ verfügbar sein.

### Manuelle Aktivierung (falls nötig)

Falls die Konfiguration nicht automatisch erscheint:

1. Öffnen Sie **Run** → **Edit Configurations...**
2. Klicken Sie auf **+** → **Application**
3. Name: `DashAppRunner`
4. Module: `r-uu.app.jeeeraaah.frontend.ui.fx`
5. Main class: `de.ruu.app.jeeeraaah.frontend.ui.fx.dash.DashAppRunner`
6. VM options: `--add-modules jakarta.annotation,jakarta.inject -Dglass.gtk.uiScale=1.5`

## Alternative: Maven exec:java verwenden

Starten Sie die Anwendung über Maven:

```bash
cd ~/develop/github/main/root/app/jeeeraaah/frontend/ui/fx
mvn exec:java
```

Dies garantiert, dass alle Module korrekt konfiguriert sind.

## Verifikation

Nach erfolgreicher Konfiguration sollte der Start folgende Ausgabe zeigen:

```
════════════════════════════════════════════════════════════
🏥 Docker Environment Health Check
════════════════════════════════════════════════════════════
```

## Hinweis zu Dependencies

Die `jakarta.annotation-api` und `jakarta.inject-api` sind explizit im POM als Dependencies definiert, um sicherzustellen, dass sie im Modulpfad verfügbar sind.
