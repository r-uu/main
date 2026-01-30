# Veraltete Dateien - Zu entfernende Inhalte

**Datum:** 2026-01-30

Diese Datei listet alle veralteten Dateien auf, die sicher entfernt werden können.

---

## ❌ Zu entfernende Skripte

### config/shared/scripts/

```bash
# Diese Skripte sind deprecated - ersetzt durch modernere Alternativen
rm config/shared/scripts/startup-complete.sh          # → Ersetzt durch docker/startup-and-setup.sh
```

### config/shared/docker/

```bash
# Diese Skripte sind deprecated oder doppelt vorhanden
rm config/shared/docker/clean-environment.sh          # → Ersetzt durch reset-docker-environment.sh
rm config/shared/docker/complete-reset.sh             # → Umbenannt zu reset-docker-environment.sh (falls duplicate)
rm config/shared/docker/reset-all-containers.sh       # → Ersetzt durch reset-docker-environment.sh
```

---

## ❌ Zu entfernende/archivierende Dokumentation

### Hauptverzeichnis

```bash
# Veraltete Status-Dateien
rm CLEANUP-COMPLETE.md           # Veraltet, nicht mehr relevant
rm CONFIG-STRATEGY.md            # In config/SINGLE-POINT-OF-TRUTH.md integriert
rm PROJEKT-STATUS.md             # Duplikat von PROJECT-STATUS.md (Deutsch/Englisch)
rm SCHNELL-ANLEITUNG-DOCKER.md   # Ersetzt durch STARTUP-QUICK-GUIDE.md
```

### config/ Verzeichnis

```bash
# Doppelte/veraltete Dokumentation
rm config/DASHAPPRUNNER-FIX-APPLIED.md        # Veraltet, Fix bereits integriert
rm config/DASHAPPRUNNER-SCHNELLANLEITUNG.md   # Ersetzt durch STARTUP-QUICK-GUIDE.md
rm config/DOCKER-CREDENTIALS-OVERVIEW.md       # In CREDENTIALS-OVERVIEW.md integriert
rm config/DOCKER-CREDENTIALS-STATUS.md         # Veraltet
rm config/DOCKER-ENV-SETUP.md                  # In shared/docker/README.md integriert
rm config/JAKARTA-MODULE-FIX.md                # Fix bereits integriert
rm config/KEYCLOAK-RESET-ANLEITUNG.md          # In TROUBLESHOOTING.md integriert
rm config/POSTGRESQL-DB-PROBLEM-GELÖST.md      # Veraltet, Problem gelöst
rm config/SINGLE-SOURCE-OF-TRUTH-STATUS.md     # In SINGLE-POINT-OF-TRUTH.md integriert

# Doppelte IntelliJ Run Config Dokumentation (3 ähnliche Dateien!)
rm config/INTELLIJ-JPMS-RUN-CONFIG.md         # Duplikat
rm config/INTELLIJ-JPMS-RUN-CONFIGURATION.md  # Duplikat
# BEHALTEN: config/INTELLIJ-APPLICATION-RUN-CONFIG.md (aktuellste Version)

# Doppelte Keycloak Admin Dokumentation
rm config/KEYCLOAK-ADMIN.md                   # Duplikat
# BEHALTEN: config/KEYCLOAK-ADMIN-CONSOLE.md (aktuellste Version)

# Doppelte JWT/Auth Dokumentation
# BEHALTEN: config/AUTHENTICATION-CREDENTIALS.md (Haupt-Doku)
# BEHALTEN: config/JWT-TROUBLESHOOTING.md (Troubleshooting)
```

### config/shared/docker/

```bash
# Doppelte Container-Namen Dokumentation
# In CONTAINER-NAMES.md ist alles konsolidiert
rm config/shared/docker/CONTAINER-PROBLEM-SOLVED.md  # Veraltet
```

### root/app/jeeeraaah/frontend/ui/fx/

```bash
# Doppelte Startup-Dokumentation (4 ähnliche Dateien!)
rm root/app/jeeeraaah/frontend/ui/fx/INTELLIJ-START-FIX.md    # Veraltet
rm root/app/jeeeraaah/frontend/ui/fx/START-DASHAPPRUNNER.md   # Veraltet
rm root/app/jeeeraaah/frontend/ui/fx/README-STARTUP.md        # Veraltet
# BEHALTEN: root/app/jeeeraaah/frontend/ui/fx/INTELLIJ-RUN-CONFIG.md (falls noch aktuell)
# ODER: Alles ersetzen durch Verweis auf config/INTELLIJ-APPLICATION-RUN-CONFIG.md
```

---

## 📦 Zu archivierende Dokumentation

Diese Dateien sollten nach `config/archive/docs-YYYYMMDD/` verschoben werden:

```bash
mkdir -p config/archive/docs-20260130

# Bereits gelöste Probleme
mv config/DASHAPPRUNNER-FIX-APPLIED.md config/archive/docs-20260130/
mv config/JAKARTA-MODULE-FIX.md config/archive/docs-20260130/
mv config/KEYCLOAK-RESET-ANLEITUNG.md config/archive/docs-20260130/
mv config/POSTGRESQL-DB-PROBLEM-GELÖST.md config/archive/docs-20260130/
mv config/shared/docker/CONTAINER-PROBLEM-SOLVED.md config/archive/docs-20260130/
```

---

## ✅ Konsolidierungs-Empfehlungen

### 1. IntelliJ Run Configuration

**Aktion:** Merge in eine einzige Datei

**Dateien:**
- `config/INTELLIJ-JPMS-RUN-CONFIG.md`
- `config/INTELLIJ-JPMS-RUN-CONFIGURATION.md`
- `config/INTELLIJ-APPLICATION-RUN-CONFIG.md`
- `root/app/jeeeraaah/frontend/ui/fx/INTELLIJ-RUN-CONFIG.md`

**Ziel:** `config/INTELLIJ-RUN-CONFIGURATION.md` (eine konsolidierte Datei)

### 2. Keycloak Admin

**Aktion:** Merge in eine einzige Datei

**Dateien:**
- `config/KEYCLOAK-ADMIN.md`
- `config/KEYCLOAK-ADMIN-CONSOLE.md`

**Ziel:** `config/KEYCLOAK-ADMIN.md` (konsolidiert)

### 3. Docker Credentials

**Aktion:** Merge in eine einzige Datei

**Dateien:**
- `config/CREDENTIALS-OVERVIEW.md`
- `config/DOCKER-CREDENTIALS-OVERVIEW.md`
- `config/shared/docker/CREDENTIALS.md`

**Ziel:** `config/CREDENTIALS.md` (konsolidiert)

### 4. Startup Documentation

**Aktion:** Alle Verweise auf eine zentrale Anleitung

**Dateien:**
- `SCHNELL-ANLEITUNG-DOCKER.md`
- `config/DASHAPPRUNNER-SCHNELLANLEITUNG.md`
- Diverse `START-*.md` Dateien

**Ziel:** Alles verweist auf `STARTUP-QUICK-GUIDE.md`

---

## 🛠️ Automatisches Cleanup-Skript

**Datei:** `config/shared/scripts/cleanup-deprecated.sh`

```bash
#!/bin/bash
# Entfernt alle veralteten Dateien

PROJECT_ROOT="/home/r-uu/develop/github/main"
ARCHIVE_DIR="$PROJECT_ROOT/config/archive/docs-20260130"

echo "🧹 Cleanup veralteter Dateien..."
echo ""

# Archive-Verzeichnis erstellen
mkdir -p "$ARCHIVE_DIR"

# Veraltete Dateien archivieren
echo "📦 Archiviere gelöste Probleme..."
mv "$PROJECT_ROOT/config/DASHAPPRUNNER-FIX-APPLIED.md" "$ARCHIVE_DIR/" 2>/dev/null || true
mv "$PROJECT_ROOT/config/JAKARTA-MODULE-FIX.md" "$ARCHIVE_DIR/" 2>/dev/null || true
mv "$PROJECT_ROOT/config/POSTGRESQL-DB-PROBLEM-GELÖST.md" "$ARCHIVE_DIR/" 2>/dev/null || true

# Veraltete Skripte entfernen
echo "🗑️  Entferne veraltete Skripte..."
rm "$PROJECT_ROOT/config/shared/scripts/startup-complete.sh" 2>/dev/null || true
rm "$PROJECT_ROOT/config/shared/docker/clean-environment.sh" 2>/dev/null || true
rm "$PROJECT_ROOT/config/shared/docker/reset-all-containers.sh" 2>/dev/null || true

# Doppelte Dokumentation entfernen
echo "📝 Entferne doppelte Dokumentation..."
rm "$PROJECT_ROOT/CLEANUP-COMPLETE.md" 2>/dev/null || true
rm "$PROJECT_ROOT/CONFIG-STRATEGY.md" 2>/dev/null || true
rm "$PROJECT_ROOT/SCHNELL-ANLEITUNG-DOCKER.md" 2>/dev/null || true

echo ""
echo "✅ Cleanup abgeschlossen!"
echo "📦 Archivierte Dateien: $ARCHIVE_DIR"
```

---

## ⚠️ Wichtige Hinweise

1. **Vor dem Löschen:** Prüfen ob Dateien noch referenziert werden (grep)
2. **Git Commit:** Separate Commits für Archivierung und Löschung
3. **Backup:** `config/archive/` bleibt im Git
4. **Dokumentation:** Nach Cleanup `DOCUMENTATION-INDEX.md` aktualisieren

---

## 📋 Ausführungsplan

```bash
# 1. Backup erstellen
cd /home/r-uu/develop/github/main
git status  # Stelle sicher, alles committed ist

# 2. Archivierung
bash config/shared/scripts/cleanup-deprecated.sh

# 3. Git Commit
git add .
git commit -m "docs: Archiviere veraltete Dokumentation (2026-01-30)"

# 4. Dokumentation konsolidieren (manuell)
# - IntelliJ Run Config
# - Keycloak Admin
# - Docker Credentials

# 5. Git Commit
git add .
git commit -m "docs: Konsolidiere Dokumentation"

# 6. DOCUMENTATION-INDEX.md aktualisieren
git add DOCUMENTATION-INDEX.md
git commit -m "docs: Aktualisiere Dokumentations-Index"
```

---

**Nach dem Cleanup sollten folgende Dateien gelöscht/archiviert sein:**

- ❌ 15+ veraltete Dokumentationsdateien
- ❌ 3-4 veraltete Skripte
- ✅ Alles konsolidiert in sinnvolle, aktuelle Dokumentation
- ✅ Klare Trennung zwischen aktiv und archiviert

**Geschätztes Ergebnis:** ~20 Dateien weniger, ~30% weniger Verwirrung! 🎉
