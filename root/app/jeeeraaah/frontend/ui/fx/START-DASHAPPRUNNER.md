# DashAppRunner Start-Anleitung

## ✅ **Empfohlene Methode: Maven Run Configuration in IntelliJ**

### Automatisch (vorkonfiguriert)
Die Maven Run Configuration ist bereits vorbereitet:

1. **Run → Run...** (oder `Alt+Shift+F10`)
2. Wähle: **"DashAppRunner (Maven)"**
3. Fertig! ✓

Diese Konfiguration:
- ✅ Liest automatisch `.mvn/jvm.config`
- ✅ Fügt alle benötigten Module hinzu (`--add-modules`)
- ✅ Konfiguriert alle JVM-Opens (`--add-opens`)
- ✅ Setzt UI-Skalierung (`glass.gtk.uiScale=1.5`)
- ✅ Nutzt MicroProfile Config für Umgebungsvariablen

### Manuell (falls Run Configuration fehlt)
1. **Run → Edit Configurations...**
2. **+ → Maven**
3. Name: `DashAppRunner (Maven)`
4. Working directory: `root/app/jeeeraaah/frontend/ui/fx`
5. Command line: `exec:java`
6. **OK**

---

## ⚠️ **NICHT empfohlen: Java Application Run Configuration**

**Problem**: IntelliJ liest `.mvn/jvm.config` nicht bei Java Application Runs!

Falls Sie trotzdem eine Application Run Configuration verwenden wollen:

1. **Run → Edit Configurations...**
2. **+ → Application**
3. Name: `DashAppRunner`
4. Main class: `de.ruu.app.jeeeraaah.frontend.ui.fx.dash.DashAppRunner`
5. **VM options** (manuell eingeben - wird NICHT aus .mvn/jvm.config gelesen):
   ```
   --add-modules jakarta.annotation,jakarta.inject,jakarta.enterprise.cdi,org.slf4j,org.jboss.logging
   --add-opens java.base/java.lang=org.jboss.weld.se.shaded
   --add-opens java.base/java.util=org.jboss.weld.se.shaded
   -Dglass.gtk.uiScale=1.5
   ```
6. **OK**

**Nachteil**: VM-Options müssen manuell synchron mit `.mvn/jvm.config` gehalten werden!

---

## 🚀 **Alternative: Maven CLI**

```bash
cd ~/develop/github/main/root/app/jeeeraaah/frontend/ui/fx
mvn exec:java
```

Dies liest automatisch `.mvn/jvm.config`.

---

## 📝 **Hintergrund**

### Warum brauchen wir `--add-modules`?

Einige Jakarta-Module sind **automatische Module** (nicht richtige JPMS-Module):
- `jakarta.annotation`
- `jakarta.inject`
- `jakarta.enterprise.cdi`
- `org.slf4j`

Diese müssen explizit mit `--add-modules` hinzugefügt werden.

### Warum `--add-opens`?

Weld CDI (Dependency Injection) nutzt Reflection, um Java-interne Klassen zu modifizieren:
- `java.base/java.lang` → für Proxy-Generierung
- `java.base/java.util` → für Collection-Manipulation

Ohne `--add-opens` schlägt CDI-Initialisierung fehl.

### Konfigurationsdateien

- **`.mvn/jvm.config`**: Wird von Maven gelesen (exec:java, mvn clean install, etc.)
- **`pom.xml`** (exec-maven-plugin): Definiert Main-Klasse und Systemproperties
- **`.run/*.run.xml`**: IntelliJ Run Configuration (nutzt Maven → liest jvm.config)

---

## ✅ **Zusammenfassung**

**Beste Lösung**: Verwenden Sie die vorkonfigurierte **"DashAppRunner (Maven)" Run Configuration**.

Diese liest automatisch:
- `.mvn/jvm.config` → JVM-Argumente
- `pom.xml` (exec-maven-plugin) → Main-Klasse und System Properties
- `testing.properties` (via MicroProfile Config) → Umgebungsvariablen

**Kein manuelles Konfigurieren notwendig!** ✨
