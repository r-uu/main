# JPMS IntelliJ Run Configuration - Schnellanleitung

## Problem gelöst ✅

Die IntelliJ Run Configuration für `DashAppRunner` verwendet jetzt **JPMS Module Path** statt Classpath.

## Was wurde geändert?

### 1. `.mvn/jvm.config` erweitert
```
root/app/jeeeraaah/frontend/ui/fx/.mvn/jvm.config
```
Diese Datei enthält alle JVM-Optionen und wird **automatisch** von Maven und IntelliJ gelesen.

### 2. ConfigHealthCheck korrigiert
Die fehlenden Property-Namen wurden ergänzt:
- `keycloak.test.user`
- `keycloak.test.password`

## Wie nutze ich es in IntelliJ?

### Option A: Automatisch (Empfohlen)
1. **Rechtsklick** auf `DashAppRunner.java`
2. **Run 'DashAppRunner.main()'**
3. ✅ **Fertig!** IntelliJ verwendet automatisch die JPMS-Konfiguration

### Option B: Manuell
1. **Run → Edit Configurations...**
2. **+ → Application**
3. **Name:** DashAppRunner
4. **Main class:** `de.ruu.app.jeeeraaah.frontend.ui.fx.dash.DashAppRunner`
5. **Use classpath of module:** `de.ruu.app.jeeeraaah.frontend.ui.fx`
6. **Build and run:** `<Default> (Module Path)` ← **Wichtig!**
7. **VM options:** Leer lassen (wird aus `.mvn/jvm.config` gelesen)
8. **OK**

## Was ist der Unterschied zu vorher?

### ❌ Vorher (falsch)
```
-cp <lange Liste von JARs>
--add-modules jakarta.annotation,jakarta.inject
```
- Verwendet **Classpath** statt Module Path
- Nicht JPMS-konform
- Schwer wartbar

### ✅ Jetzt (korrekt)
```
--module-path <module path>
--module de.ruu.app.jeeeraaah.frontend.ui.fx/de.ruu.app.jeeeraaah.frontend.ui.fx.dash.DashAppRunner
```
- Verwendet **Module Path**
- Voll JPMS-konform
- Alle Optionen in `.mvn/jvm.config`

## Vorteile

1. **Single Point of Truth:** Alle JVM-Optionen in **einer** Datei
2. **Wartbar:** Änderungen nur an einer Stelle
3. **Team-konsistent:** Funktioniert für alle Entwickler gleich
4. **JPMS-konform:** Nutzt Java Module System korrekt

## Troubleshooting

### "Module X not found"
➜ Ergänze Modul in `.mvn/jvm.config` unter `--add-modules`

### "Unable to make field accessible"
➜ Prüfe `--add-opens` in `.mvn/jvm.config`

### "Restricted method called"
➜ Prüfe `--enable-native-access` in `.mvn/jvm.config`

## Weitere Dokumentation
Siehe: `config/INTELLIJ-JPMS-RUN-CONFIG.md`
