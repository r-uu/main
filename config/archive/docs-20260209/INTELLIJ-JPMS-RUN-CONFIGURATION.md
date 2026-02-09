# JPMS Run Configuration für IntelliJ IDEA

## Häufige Frage: Ist `-cp` bei JPMS normal?

**JA, das ist völlig normal!**

IntelliJ aktiviert automatisch "Use classpath of module" und fügt `-cp` Parameter hinzu. Dies ist **korrekt** für moderne Java-Projekte, die:
- Eigene JPMS-Module verwenden (module-info.java)
- Externe Libraries nutzen, die noch nicht vollständig modularisiert sind

### Warum IntelliJ `-cp` automatisch hinzufügt

Die Kombination von `-cp` (Classpath) und `--module-path` ist der **Standard-Hybrid-Modus**:
- **Module Path**: Enthält modularisierte JARs
- **Classpath**: Enthält nicht-modularisierte JARs (Automatic Modules)

IntelliJ verwendet **beides** gleichzeitig - das ist **korrekt**.

### Wie prüfe ich, ob JPMS aktiv ist?

Prüfen Sie die Startzeile in der Console:
```bash
java --module-path <path> --add-modules ... -cp <classpath> -m <module>/<mainclass>
```

✅ **JPMS ist aktiv**, wenn Sie sehen:
- `--module-path` oder `-p`
- `--add-modules`
- `-m` oder `--module`

❌ **Nur Classpath** (falsch):
```bash
java -cp <classpath> <mainclass>
```

**Fazit:** Die Configuration läuft konsequent unter JPMS. Die automatische `-cp` Aktivierung ist normal und kein Grund zur Sorge.

## Problem
IntelliJ IDEA erstellt automatisch Classpath-basierte Run Configurations, selbst wenn das Projekt JPMS (Java Platform Module System) verwendet. Dies führt zu `--add-modules` Parametern in Kombination mit `-cp`, was aber wie oben erklärt **korrekt** ist.

## Lösung

### 1. Run Configuration XML erstellen

Erstellen Sie die Datei `.idea/runConfigurations/DashAppRunner__JPMS_.xml` mit folgendem Inhalt:

```xml
<component name="ProjectRunConfigurationManager">
  <configuration default="false" name="DashAppRunner (JPMS)" type="Application" factoryName="Application">
    <option name="ALTERNATIVE_JRE_PATH" value="25" />
    <option name="ALTERNATIVE_JRE_PATH_ENABLED" value="true" />
    <option name="MAIN_CLASS_NAME" value="de.ruu.app.jeeeraaah.frontend.ui.fx.dash.DashAppRunner" />
    <module name="r-uu.app.jeeeraaah.frontend.ui.fx" />
    <option name="VM_PARAMETERS" value="--add-modules org.slf4j --add-reads de.ruu.app.jeeeraaah.frontend.ui.fx=ALL-UNNAMED -Dglass.gtk.uiScale=1.5 -Dfile.encoding=UTF-8" />
    <option name="WORKING_DIRECTORY" value="$PROJECT_DIR$" />
    <extension name="coverage">
      <pattern>
        <option name="PATTERN" value="de.ruu.app.jeeeraaah.frontend.ui.fx.*" />
        <option name="ENABLED" value="true" />
      </pattern>
    </extension>
    <method v="2">
      <option name="Make" enabled="true" />
    </method>
  </configuration>
</component>
```

### 2. JVM Config für Maven erstellen

Erstellen Sie die Datei `root/app/jeeeraaah/frontend/ui/fx/.mvn/jvm.config`:

```
--add-modules org.slf4j
--add-reads de.ruu.app.jeeeraaah.frontend.ui.fx=ALL-UNNAMED
-Dglass.gtk.uiScale=1.5
```

### 3. IntelliJ Projekt-Einstellungen

IntelliJ verwendet standardmäßig den Classpath für Module. Um dies zu ändern:

1. **File → Project Structure → Project Settings → Modules**
2. Wählen Sie `r-uu.app.jeeeraaah.frontend.ui.fx`
3. Stellen Sie sicher, dass "Module SDK" auf Java 25 gesetzt ist

### 4. Warum IntelliJ -cp verwendet

IntelliJ IDEA fügt automatisch `-cp` hinzu, weil:

1. **Maven/Gradle Dependencies**: IntelliJ versucht, alle Dependencies auf dem Classpath bereitzustellen
2. **IDE-Kompatibilität**: Für bessere IDE-Features (Code Completion, Debugging)
3. **Mixed-Mode**: Viele Projekte mischen Classpath und Module Path

### 5. Workaround: Maven Exec Plugin verwenden

Die beste Lösung für vollständiges JPMS ist, Maven zu verwenden:

**Alternative Run Configuration** (.idea/runConfigurations/DashAppRunner__Maven_.xml):

```xml
<component name="ProjectRunConfigurationManager">
  <configuration default="false" name="DashAppRunner (Maven)" type="MavenRunConfiguration" factoryName="Maven">
    <MavenSettings>
      <option name="myGeneralSettings" />
      <option name="myRunnerSettings" />
      <option name="myRunnerParameters">
        <MavenRunnerParameters>
          <option name="profiles">
            <set />
          </option>
          <option name="goals">
            <list>
              <option value="exec:java" />
            </list>
          </option>
          <option name="pomFileName" value="pom.xml" />
          <option name="profilesMap">
            <map />
          </option>
          <option name="resolveToWorkspace" value="false" />
          <option name="workingDirPath" value="$PROJECT_DIR$/root/app/jeeeraaah/frontend/ui/fx" />
        </MavenRunnerParameters>
      </option>
    </MavenSettings>
    <method v="2" />
  </configuration>
</component>
```

### 6. Testen

Nach Erstellung der Run Configuration:

1. **IntelliJ neu starten** (wichtig!)
2. Run → Edit Configurations
3. Wählen Sie "DashAppRunner (JPMS)"
4. Prüfen Sie unter "Configuration" den generierten Befehl
5. Starten Sie die Anwendung

### 7. Erwartetes Verhalten

IntelliJ wird immer noch `-cp` generieren, ABER:
- Die `--add-modules` und `--add-reads` Parameter werden korrekt gesetzt
- Die Anwendung läuft mit voller JPMS-Unterstützung
- Alle Module sind korrekt aufgelöst

### 8. Vollständig ohne Classpath (Fortgeschritten)

Für ein Setup OHNE `-cp`:

1. Verwenden Sie ausschließlich das Maven Exec Plugin
2. Konfigurieren Sie im `pom.xml`:

```xml
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>exec-maven-plugin</artifactId>
  <configuration>
    <executable>java</executable>
    <arguments>
      <argument>--module-path</argument>
      <modulepath/>
      <argument>--add-modules</argument>
      <argument>org.slf4j</argument>
      <argument>-m</argument>
      <argument>de.ruu.app.jeeeraaah.frontend.ui.fx/de.ruu.app.jeeeraaah.frontend.ui.fx.dash.DashAppRunner</argument>
    </arguments>
  </configuration>
</plugin>
```

3. Verwenden Sie dann die Maven Run Configuration

## Fazit

- **IntelliJ Application**: Verwendet `-cp` + `--add-modules` (Hybrid-Modus, gut für Debugging)
- **Maven Exec**: Vollständig JPMS-konform (besser für Produktion)
- **Empfehlung**: Beide Konfigurationen behalten - Maven für Tests, IntelliJ für Development

## Referenzen

- [JEP 261: Module System](https://openjdk.org/jeps/261)
- [IntelliJ IDEA Module Support](https://www.jetbrains.com/help/idea/java-9-support.html)
- [Maven Exec Plugin](https://www.mojohaus.org/exec-maven-plugin/)
