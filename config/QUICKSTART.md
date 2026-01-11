# 🚀 Schnellstart - Entwicklungsumgebung

Kurze Anleitung für die Einrichtung deiner lokalen Entwicklungsumgebung.

## 1️⃣ Setup ausführen

```bash
cd /home/r-uu/develop/github/main/config/shared/scripts
./setup-dev-env.sh
```

Das Skript erstellt:
- `config/local/` Verzeichnisse
- Lokale Konfigurationsdateien aus Templates

## 2️⃣ Konfiguration anpassen

### Docker Credentials

Bearbeite `config/local/docker/.env.local`:

```bash
nano config/local/docker/.env.local
```

Ändere mindestens:
```env
POSTGRES_PASSWORD=DEIN_SICHERES_PASSWORT
```

### Shell-Aliase aktivieren

Füge zu deiner `~/.bashrc` hinzu:

```bash
# r-uu Projekt Konfiguration
source ~/develop/github/main/config/shared/wsl/aliases.sh
source ~/develop/github/main/config/local/wsl/aliases.sh
```

Lade die Shell neu:
```bash
source ~/.bashrc
```

## 3️⃣ Docker starten

```bash
ruu-docker-up
```

## 4️⃣ Maven Build

```bash
ruu-install
```

## ✅ Verfügbare Aliase

Zeige alle verfügbaren Aliase:
```bash
ruu-help
```

Wichtigste Aliase:
- `cdruu` - Navigate zum Projekt-Root
- `ruu-install` - Maven Clean Install
- `ruu-docker-up` - Docker Container starten
- `ruu-docker-down` - Docker Container stoppen
- `ruu-status` - Git Status

## 📚 Weitere Dokumentation

- [config/readme.md](readme.md) - Vollständige Dokumentation
- [config/shared/ai-prompts/](shared/ai-prompts/) - AI-Prompt Bibliothek

## 🆘 Probleme?

Bei Problemen:
1. Prüfe ob alle Pfade korrekt sind
2. Stelle sicher, dass Docker läuft
3. Überprüfe Berechtigungen der Skripte
4. Lies die ausführliche Dokumentation

