# ✅ DashAppRunner Start - Problem gelöst!

## Das Problem

IntelliJ liest **`.mvn/jvm.config` NICHT** bei Java Application Run Configurations!

Fehler:
```
Error occurred during initialization of boot layer
java.lang.module.FindException: Module org.slf4j not found
```

## Die Lösung

### ✅ **Option 1: Maven Run Configuration (EMPFOHLEN)**

Eine vorkonfigurierte Maven Run Configuration wurde erstellt:

**📁 Datei**: `.run/DashAppRunner (Maven).run.xml`

**So starten Sie DashAppRunner**:
1. IntelliJ: **Run → Run...** (oder `Alt+Shift+F10`)
2. Wähle: **"DashAppRunner (Maven)"**
3. ✅ Fertig!

**Vorteile**:
- ✅ Liest automatisch `.mvn/jvm.config`
- ✅ Keine manuellen VM-Options erforderlich
- ✅ Funktioniert Out-of-the-Box

---

### ⚠️ **Option 2: Java Application Run Configuration (NICHT empfohlen)**

**Problem**: VM-Options müssen **manuell** konfiguriert werden!

Falls Sie trotzdem eine Application Run Config wollen:

1. **Run → Edit Configurations...**
2. Ihre bestehende DashAppRunner Configuration öffnen
3. **VM options** manuell setzen:
   ```
   --add-modules jakarta.annotation,jakarta.inject,jakarta.enterprise.cdi,org.slf4j,org.jboss.logging
   --add-opens java.base/java.lang=org.jboss.weld.se.shaded
   --add-opens java.base/java.util=org.jboss.weld.se.shaded
   -Dglass.gtk.uiScale=1.5
   ```
4. **OK**

**Nachteil**: Muss synchron mit `.mvn/jvm.config` gehalten werden!

---

### 🚀 **Option 3: Maven CLI**

```bash
cd ~/develop/github/main/root/app/jeeeraaah/frontend/ui/fx
mvn exec:java
```

---

## Technischer Hintergrund

### Warum das Problem auftrat

1. **JPMS Module System**: Das Projekt nutzt Java Platform Module System
2. **Automatische Module**: Einige Jakarta-Bibliotheken sind "automatische Module" (kein echtes JPMS)
3. **IntelliJ Limitation**: IntelliJ liest `.mvn/jvm.config` nur bei Maven-basierten Runs

### Was `.mvn/jvm.config` enthält

```
--add-modules jakarta.annotation,jakarta.inject,jakarta.enterprise.cdi,org.slf4j,org.jboss.logging
--add-opens java.base/java.lang=org.jboss.weld.se.shaded
--add-opens java.base/java.util=org.jboss.weld.se.shaded
```

- **`--add-modules`**: Fügt automatische Module zum Module Path hinzu
- **`--add-opens`**: Erlaubt Weld CDI, Java-interne Klassen via Reflection zu modifizieren

---

## ✅ Was wurde gefixt

1. ✅ `.mvn/jvm.config` mit allen benötigten Modulen erstellt
2. ✅ Maven Run Configuration `.run/DashAppRunner (Maven).run.xml` erstellt
3. ✅ Dokumentation erstellt (`START-DASHAPPRUNNER.md`)
4. ✅ `module-info.java` korrigiert (`org.slf4j` als required)

---

## Empfehlung

**Verwenden Sie die Maven Run Configuration "DashAppRunner (Maven)"** - sie ist vorkonfiguriert und wartungsfrei!

Falls sie in IntelliJ nicht erscheint:
1. File → Invalidate Caches → Invalidate and Restart
2. Nach Neustart sollte die Run Config automatisch erkannt werden

Sie finden die Config unter: `.run/DashAppRunner (Maven).run.xml`
