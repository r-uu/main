# 🔧 Fix: IntelliJ Run Configurations funktionieren nicht mehr

**Problem:** `Cannot run program "//wsl.localhost/Ubuntu/opt/graalvm-jdk-25/bin/java"`

## 🎯 Root Cause

IntelliJ verwendet **Windows-Pfade** für WSL-JDK, was beim exec-maven-plugin zu Fehlern führt.

## ✅ Schnelle Lösung (5 Minuten)

### Option 1: JDK für Run Configurations korrigieren

**Für ALLE Run Configurations:**

1. **Run** → **Edit Configurations...**
2. Wähle **Templates** → **Application** (im linken Baum ganz unten)
3. **JRE:** Wähle das GraalVM 25 JDK (sollte als WSL JDK angezeigt werden)
4. **Apply** klicken

**Dann für bestehende Configs:**

1. Wähle deine Run Configuration (z.B. `InvoiceExample`)
2. **JRE:** Auf `Project default` setzen ODER explizit GraalVM 25 wählen
3. **Apply** → **OK**

### Option 2: Maven Delegation deaktivieren (EMPFOHLEN für dieses Problem)

**Dies verhindert, dass IntelliJ überhaupt das exec-plugin verwendet:**

1. **File** → **Settings**
2. **Build, Execution, Deployment** → **Build Tools** → **Maven** → **Runner**
3. **DEAKTIVIERE:** ☐ **Delegate IDE build/run actions to Maven**
4. **Apply** → **OK**
5. **IntelliJ neu starten**

**Dann:**
- Application Run Configurations verwenden die JVM direkt (kein Maven exec)
- Schneller und zuverlässiger

## 🔍 Detaillierte Diagnose

### Prüfe aktuelles IntelliJ JDK Setup:

1. **File** → **Project Structure** (Strg+Alt+Shift+S)
2. **Platform Settings** → **SDKs**
3. Prüfe ob GraalVM 25 korrekt eingerichtet ist:
   - **JDK home path:** Sollte `\\wsl.localhost\Ubuntu\opt\graalvm-jdk-25` sein
   - NICHT `/opt/graalvm-jdk-25` (das ist WSL-intern)

### Prüfe Project SDK:

1. **Project Settings** → **Project**
2. **Project SDK:** Sollte GraalVM 25 sein
3. **Project language level:** 25

## 🎯 Empfohlene Einrichtung

### 1. Application Run Configuration verwenden (statt Maven exec)

**Bereits erstellt:** `.idea/runConfigurations/InvoiceExample.xml`

**Vorteile:**
- ✅ Kein Maven exec-plugin Problem
- ✅ Direktes Debugging
- ✅ Schneller Start
- ✅ Funktioniert immer, unabhängig von Maven-Konfiguration

### 2. Maven Settings korrekt setzen

**File** → **Settings** → **Build Tools** → **Maven**

- **Maven home path:** `Use Maven wrapper` (NICHT den Windows-Pfad!)
- **User settings file:** `\\wsl.localhost\Ubuntu\home\r-uu\.m2\settings.xml`
- **Local repository:** `\\wsl.localhost\Ubuntu\home\r-uu\.m2\repository`

**Maven** → **Runner**

- **JRE:** `Project JDK` oder explizit `GraalVM 25`
- **VM Options:** `-Xmx2g` (optional)

## 🧪 Testen

### Test 1: Application Run Config

1. Wähle **InvoiceExample** Run Configuration
2. Klicke **Run** (grüner Pfeil)
3. **Sollte funktionieren** ohne Maven-Fehler

### Test 2: Maven Goal im Terminal

```bash
cd root/sandbox/office/microsoft/word/jasperreports
mvn exec:java
```

**Sollte funktionieren**, da es direkt in WSL läuft.

## 🔧 Wenn nichts hilft: IntelliJ Cache löschen

```
File → Invalidate Caches → Invalidate and Restart
```

Dann alle obigen Schritte wiederholen.

## 📋 Zusammenfassung

Das Problem tritt auf, wenn IntelliJ versucht, Windows-Pfade an Maven-Plugins in WSL zu übergeben.

**Die beste Lösung:**
1. ☑️ Maven Delegation deaktivieren
2. ☑️ Application Run Configurations verwenden
3. ☑️ Für Maven Goals: Terminal nutzen

**Warum passiert das?**
- IntelliJ läuft unter Windows
- Maven/Java laufen in WSL
- Path-Translation funktioniert nicht bei allen Plugins

---

**Erstellt:** 2026-01-16  
**Problem:** exec-maven-plugin kann Windows-Pfade nicht verarbeiten  
**Status:** ✅ Lösbar durch Maven Delegation deaktivieren

