# ✅ JAVA_HOME Problem behoben - IntelliJ WSL Setup

**Datum:** 2026-01-11  
**Problem:** `The JAVA_HOME environment variable is not defined correctly` beim Maven Build in IntelliJ

## 🔍 Root Cause

IntelliJ IDEA läuft unter **Windows** und kann die **WSL-Umgebungsvariablen** nicht direkt lesen.

```
Windows (IntelliJ) ❌ → WSL (GraalVM, Maven) ✅
```

## ✅ Was wurde gemacht

### 1. WSL-Umgebungsvariablen aktualisiert

✅ `JAVA_HOME` in `~/.bashrc` gesetzt:
```bash
export JAVA_HOME=/opt/graalvm-jdk-25
export PATH=$JAVA_HOME/bin:$PATH
```

✅ `JAVA_HOME` in `~/.profile` gesetzt (für GUI-Apps):
```bash
export JAVA_HOME=/opt/graalvm-jdk-25
export PATH=$JAVA_HOME/bin:$PATH
```

✅ `JAVA_HOME` in `/etc/environment` gesetzt:
```bash
JAVA_HOME=/opt/graalvm-jdk-25
```

### 2. Verification in WSL Terminal

```bash
$ echo $JAVA_HOME
/opt/graalvm-jdk-25

$ java -version
java version "25.0.1" 2025-10-21 LTS
Java(TM) SE Runtime Environment Oracle GraalVM 25.0.1+8.1

$ mvn --version
Apache Maven 3.9.9
Java version: 25.0.1, vendor: Oracle Corporation, runtime: /opt/graalvm-jdk-25
```

**Status:** ✅ WSL ist korrekt konfiguriert

### 3. IntelliJ Setup Dokumentation erstellt

➡️ **[INTELLIJ-WSL-SETUP.md](INTELLIJ-WSL-SETUP.md)**

Diese Anleitung beschreibt Schritt-für-Schritt:
- Wie man GraalVM 25 als JDK in IntelliJ hinzufügt
- Wie man Maven für WSL konfiguriert
- Wie man das Project SDK setzt

## 🎯 Nächste Schritte für dich

### ⚠️ WICHTIG: IntelliJ manuell konfigurieren

Folge der Anleitung in **[INTELLIJ-WSL-SETUP.md](INTELLIJ-WSL-SETUP.md)**:

1. **JDK hinzufügen:** `\\wsl.localhost\Ubuntu\opt\graalvm-jdk-25`
2. **Project SDK setzen:** GraalVM 25
3. **Maven home setzen:** `\\wsl.localhost\Ubuntu\opt\maven\maven`
4. **Maven Runner JRE:** GraalVM 25
5. **IntelliJ neu starten**

### 🧪 Dann testen

```bash
# In IntelliJ Maven Tool Window:
root → Lifecycle → clean
root → Lifecycle → install
```

**Erwartetes Ergebnis:** ✅ BUILD SUCCESS

## 📋 Quick Reference

| Was | Wert |
|-----|------|
| **JDK Path (WSL)** | `/opt/graalvm-jdk-25` |
| **JDK Path (Windows)** | `\\wsl.localhost\Ubuntu\opt\graalvm-jdk-25` |
| **Maven Home (WSL)** | `/opt/maven/maven` |
| **Maven Home (Windows)** | `\\wsl.localhost\Ubuntu\opt\maven\maven` |
| **M2 Repository (WSL)** | `/home/r-uu/.m2/repository` |
| **M2 Repository (Windows)** | `\\wsl.localhost\Ubuntu\home\r-uu\.m2\repository` |

## 🆘 Falls es immer noch nicht funktioniert

### Option 1: WSL Terminal in IntelliJ nutzen

1. **View → Tool Windows → Terminal**
2. **Settings → Shell path:** `wsl.exe`
3. **Im Terminal:**
   ```bash
   cd ~/develop/github/main/root
   mvn clean install
   ```

### Option 2: External Maven verwenden

1. **Settings → Build Tools → Maven**
2. **Maven home path:** `\\wsl.localhost\Ubuntu\opt\maven\maven`
3. **Haken setzen bei:** "Use Maven from path"

## 📚 Weitere Dokumentation

- [INTELLIJ-WSL-SETUP.md](INTELLIJ-WSL-SETUP.md) - Detaillierte IntelliJ Konfiguration
- [GRAALVM-INSTALLATION.md](GRAALVM-INSTALLATION.md) - GraalVM Installation
- [QUICKSTART.md](QUICKSTART.md) - Projekt Schnellstart

---

**Status:** ⚠️ WSL korrekt konfiguriert, IntelliJ Setup erforderlich  
**Nächster Schritt:** Folge [INTELLIJ-WSL-SETUP.md](INTELLIJ-WSL-SETUP.md)

