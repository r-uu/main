# ✅ GraalVM 25 Migration abgeschlossen

**Datum:** 2026-01-11  
**Status:** ✅ Erfolgreich abgeschlossen

## 📋 Durchgeführte Änderungen

### 1. GraalVM 25 Installation

- **Installationsverzeichnis:** `/opt/graalvm-jdk-25`
- **Version:** Oracle GraalVM 25.0.1+8.1 (LTS)
- **Download:** Automatisch via `install-graalvm.sh`

### 2. Alte GraalVM 24 entfernt

- Entfernt: `/usr/lib/jvm/graalvm-community-openjdk-24.0.1+9.1`
- Symlink entfernt: `/usr/lib/jvm/graalvm-ce-java24`

### 3. System-Konfiguration aktualisiert

#### `/etc/environment`
```bash
JAVA_HOME=/opt/graalvm-jdk-25
M2_HOME=/opt/apache-maven-3.9.9
PATH="/opt/graalvm-jdk-25/bin:/opt/apache-maven-3.9.9/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
```

#### Java Alternativen
```bash
sudo update-alternatives --install /usr/bin/java java /opt/graalvm-jdk-25/bin/java 1100
sudo update-alternatives --install /usr/bin/javac javac /opt/graalvm-jdk-25/bin/javac 1100
```

### 4. Shell-Konfiguration

#### `~/.bashrc` erweitert
```bash
# r-uu Projekt Konfiguration
if [ -f ~/develop/github/main/config/shared/wsl/aliases.sh ]; then
    source ~/develop/github/main/config/shared/wsl/aliases.sh
fi

if [ -f ~/develop/github/main/config/local/wsl/aliases.sh ]; then
    source ~/develop/github/main/config/local/wsl/aliases.sh
fi
```

#### `install-graalvm.sh` Berechtigungen
```bash
chmod +x /home/r-uu/develop/github/main/config/shared/scripts/install-graalvm.sh
```

## ✅ Verifizierung

### Java Version
```bash
$ java --version
java 25.0.1 2025-10-21 LTS
Java(TM) SE Runtime Environment Oracle GraalVM 25.0.1+8.1 (build 25.0.1+8-LTS-jvmci-b01)
Java HotSpot(TM) 64-Bit Server VM Oracle GraalVM 25.0.1+8.1 (build 25.0.1+8-LTS-jvmci-b01, mixed mode, sharing)
```

### GraalVM-Version Alias
```bash
$ ruu-graalvm-version
GraalVM: java 25.0.1 2025-10-21 LTS
Path: /opt/graalvm-jdk-25
```

### Maven Version
```bash
$ mvn --version
Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937)
Maven home: /opt/maven/maven
Java version: 25.0.1, vendor: Oracle Corporation, runtime: /opt/graalvm-jdk-25
```

### Verfügbare r-uu Aliase
```bash
$ alias | grep "ruu-" | wc -l
61
```

## 🔄 Nächste Schritte nach WSL-Neustart

Nach jedem Neustart oder neuer Terminal-Session:

1. **Shell neu laden** (optional, da automatisch):
   ```bash
   source ~/.bashrc
   ```

2. **Java-Version prüfen:**
   ```bash
   ruu-graalvm-version
   ```

3. **Maven-Build testen:**
   ```bash
   ruu-install-fast
   ```

## 📝 Wichtige Hinweise

### Warum Version 24 noch sichtbar war

Das Problem war, dass GraalVM 24 in `/etc/environment` konfiguriert war:
- `/etc/environment` wird beim System-Boot geladen
- Der PATH dort hatte höhere Priorität als die `aliases.sh`
- Nach der Aktualisierung von `/etc/environment` und Entfernung von GraalVM 24 funktioniert alles korrekt

### Installation für andere Entwickler

Andere Entwickler können GraalVM 25 einfach installieren:

```bash
cd ~/develop/github/main/config/shared/scripts
sudo ./install-graalvm.sh
```

Das Skript:
- ✅ Erkennt existierende Installationen
- ✅ Lädt automatisch GraalVM 25 herunter
- ✅ Konfiguriert Java-Alternativen
- ✅ Aktualisiert System-Pfade

Danach nur noch die `.bashrc` aktualisieren (siehe Schritt 4 oben).

## 🎯 Zusammenfassung

| Komponente | Version | Pfad |
|------------|---------|------|
| GraalVM | 25.0.1+8.1 (LTS) | `/opt/graalvm-jdk-25` |
| Maven | 3.9.9 | `/opt/maven/maven` |
| Aliase | 61 aktiv | `config/shared/wsl/aliases.sh` |

**Status:** ✅ System bereit für Entwicklung mit GraalVM 25

## 🔗 Verwandte Dokumentation

- [GRAALVM-INSTALLATION.md](GRAALVM-INSTALLATION.md) - Vollständige Installations-Anleitung
- [QUICKSTART.md](QUICKSTART.md) - Schnellstart für neue Entwickler
- [config/shared/scripts/install-graalvm.sh](shared/scripts/install-graalvm.sh) - Installations-Skript

