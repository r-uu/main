# PostgreSQL Init-Skripte

Dieses Verzeichnis enthält Initialisierungs-Skripte für PostgreSQL.

## Verwendung

Alle `.sh` und `.sql` Dateien in diesem Verzeichnis werden automatisch beim **ersten Start** der PostgreSQL-Datenbank ausgeführt.

### Reihenfolge

Skripte werden **alphabetisch** ausgeführt. Verwende Präfixe für die Reihenfolge:

```
01-init.sh        → Datenbanken und Benutzer erstellen
02-sample-data.sh → Testdaten laden
03-...            → Weitere Skripte
```

## Skripte anpassen

### 01-init.sh
- Erstellt Keycloak-Datenbank
- Erstellt weitere benötigte Datenbanken
- Aktiviert PostgreSQL Extensions

### 02-sample-data.sh
- Lädt Beispiel-/Testdaten
- Aktuell deaktiviert (auskommentiert)
- Passe an deine Bedürfnisse an

## Eigene Skripte hinzufügen

### Bash-Skript (.sh)

```bash
#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE TABLE my_table (
        id SERIAL PRIMARY KEY,
        name VARCHAR(255)
    );
EOSQL
```

### SQL-Datei (.sql)

```sql
-- 03-my-schema.sql
CREATE SCHEMA IF NOT EXISTS my_schema;

CREATE TABLE my_schema.my_table (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Neuinitialisierung

Um die Datenbank neu zu initialisieren (ACHTUNG: Daten gehen verloren!):

```bash
ruu-postgres-rebuild
# oder manuell:
docker-compose down -v
docker volume rm ruu-postgres-data
docker-compose up -d postgres
```

## Verfügbare Umgebungsvariablen

In Init-Skripten verfügbar:
- `$POSTGRES_DB` - Haupt-Datenbankname
- `$POSTGRES_USER` - Datenbank-Benutzer
- `$POSTGRES_PASSWORD` - Datenbank-Passwort

## Logs prüfen

Prüfe ob Initialisierung erfolgreich war:

```bash
ruu-postgres-logs
# Suche nach "[init]" Messages
```

## Best Practices

1. ✅ Verwende `set -e` in Bash-Skripten
2. ✅ Verwende `ON_ERROR_STOP=1` für psql
3. ✅ Verwende `IF NOT EXISTS` für CREATE-Statements
4. ✅ Logge Fortschritt mit echo
5. ✅ Benenne Skripte sprechend (01-, 02-, etc.)
6. ❌ Keine Secrets in Skripten speichern
7. ❌ Keine produktiven Daten committen

## Troubleshooting

### Skript wird nicht ausgeführt

Skripte werden nur beim **ersten Start** ausgeführt. Bei Änderungen:
```bash
ruu-postgres-rebuild
```

### Berechtigungsfehler

Stelle sicher, dass Skripte ausführbar sind:
```bash
chmod +x config/shared/docker/initdb/*.sh
```

### Syntax-Fehler

Prüfe Logs:
```bash
docker logs ruu-postgres 2>&1 | grep -A 10 "init"
```

