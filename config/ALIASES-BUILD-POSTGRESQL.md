# 🚀 Neue Aliase - build-all & PostgreSQL

**Datum:** 2026-01-11  
**Status:** ✅ Aliase hinzugefügt!

---

## ⚡ Neue Aliase

### Build-Aliase

| Alias | Befehl | Beschreibung |
|-------|--------|--------------|
| `build-all` | `$RUU_HOME/config/shared/scripts/build-all.sh` | **Kompletter Build** (empfohlen!) |
| `ruu-build` | `build-all` | Alias für build-all |
| `ruu-build-all` | `build-all` | Alias für build-all |

### PostgreSQL-Aliase

| Alias | Befehl | Beschreibung |
|-------|--------|--------------|
| `ruu-postgres-setup` | `sudo .../setup-postgresql.sh` | **PostgreSQL Setup** |
| `ruu-postgres-service-start` | `sudo service postgresql start` | Service starten |
| `ruu-postgres-service-stop` | `sudo service postgresql stop` | Service stoppen |
| `ruu-postgres-service-status` | `sudo service postgresql status` | Service Status |
| `ruu-psql` | `PGPASSWORD=... psql -h localhost -U r_uu -d lib_test` | psql Client |

---

## 🎯 Verwendung

### 1. Aliase aktivieren

```bash
source ~/develop/github/main/config/shared/wsl/aliases.sh
```

**Oder** permanent in `~/.bashrc` hinzufügen:

```bash
# r-uu Projekt Aliase
if [ -f ~/develop/github/main/config/shared/wsl/aliases.sh ]; then
    source ~/develop/github/main/config/shared/wsl/aliases.sh
fi
```

Dann:
```bash
source ~/.bashrc
```

### 2. Aliase verwenden

```bash
# PostgreSQL Setup (einmalig)
ruu-postgres-setup

# Build ausführen
build-all

# PostgreSQL Status prüfen
ruu-postgres-service-status

# PostgreSQL Client öffnen
ruu-psql
```

---

## 📊 Alle Build-Aliase

| Alias | Beschreibung |
|-------|--------------|
| `build-all` | ⭐ Kompletter Build (BOM + Root, mit PostgreSQL-Check) |
| `ruu-build` | Wie build-all |
| `ruu-clean` | Maven clean |
| `ruu-install` | Maven clean install |
| `ruu-install-fast` | Maven clean install -DskipTests |
| `ruu-bom-install` | Nur BOM bauen |
| `ruu-root-install` | Nur Root bauen |
| `ruu-lib-install` | Nur Lib-Module bauen |

---

## 📊 Alle PostgreSQL-Aliase

| Alias | Beschreibung |
|-------|--------------|
| `ruu-postgres-setup` | ⭐ Komplettes PostgreSQL Setup |
| `ruu-postgres-service-start` | Service starten |
| `ruu-postgres-service-stop` | Service stoppen |
| `ruu-postgres-service-status` | Service Status prüfen |
| `ruu-postgres-service-restart` | Service neustarten |
| `ruu-psql` | psql Client (localhost, r_uu, lib_test) |

---

## 🎓 Beispiel-Workflow

```bash
# Terminal öffnen

# 1. Aliase aktivieren (wenn nicht in .bashrc)
source ~/develop/github/main/config/shared/wsl/aliases.sh

# 2. Zum Projekt navigieren
cdruu
# oder: ruu-home

# 3. PostgreSQL Setup (nur beim ersten Mal)
ruu-postgres-setup

# 4. Build ausführen
build-all

# 5. PostgreSQL prüfen (optional)
ruu-postgres-service-status

# 6. Datenbank öffnen (optional)
ruu-psql
```

---

## ✅ Vorteile der Aliase

| Vorher | Mit Alias | Ersparnis |
|--------|-----------|-----------|
| `cd /home/r-uu/develop/github/main` | `cdruu` | 33 Zeichen |
| `sudo .../setup-postgresql.sh` | `ruu-postgres-setup` | ~40 Zeichen |
| `./config/shared/scripts/build-all.sh` | `build-all` | 28 Zeichen |
| `PGPASSWORD=... psql -h localhost -U r_uu -d lib_test` | `ruu-psql` | ~45 Zeichen |

**Gesamt:** ~150 Zeichen weniger pro Workflow! ⚡

---

## 📚 Alle Aliase anzeigen

```bash
# Alle r-uu Aliase
ruu-help

# Nur Build-Aliase
ruu-help | grep -E "build|install|clean"

# Nur PostgreSQL-Aliase
ruu-help | grep postgres

# Nur Navigation
ruu-help-nav
```

---

## 🔧 Aliase in .bashrc dauerhaft aktivieren

```bash
# Füge zu ~/.bashrc hinzu:
cat >> ~/.bashrc << 'EOF'

# ═══════════════════════════════════════════════════════════════════
# r-uu Projekt Aliase
# ═══════════════════════════════════════════════════════════════════
if [ -f ~/develop/github/main/config/shared/wsl/aliases.sh ]; then
    source ~/develop/github/main/config/shared/wsl/aliases.sh
fi
EOF

# Neu laden
source ~/.bashrc
```

Jetzt sind die Aliase in jedem neuen Terminal automatisch verfügbar! 🎉

---

**Status:** ✅ Aliase hinzugefügt und dokumentiert!

Verwende ab jetzt einfach:
```bash
build-all
```

Statt:
```bash
cd /home/r-uu/develop/github/main
./config/shared/scripts/build-all.sh
```

