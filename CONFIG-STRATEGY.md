# Konfigurationsstrategie

## 🎯 Prinzip: Zwei getrennte Konfigurationsebenen

### 1️⃣ Docker-Ebene (Infrastructure)
**Datei:** `/config/shared/docker/.env`
**Verwendet von:** Docker Compose
**Format:** Shell-Environment-Variablen (LF Line-Endings!)
**Im Git:** ✅ Ja (mit Development-Defaults)

```bash
postgres_jeeeraaah_user=jeeeraaah
postgres_jeeeraaah_password=jeeeraaah
keycloak_admin_user=admin
keycloak_admin_password=admin
```

### 2️⃣ Java-Ebene (Application)
**Datei:** `/testing.properties`
**Verwendet von:** MicroProfile Config
**Format:** Java Properties (robust gegen Line-Endings)
**Im Git:** ✅ Ja (mit Development-Defaults)

```properties
db.jeeeraaah.user=jeeeraaah
db.jeeeraaah.password=jeeeraaah
keycloak.admin.user=admin
keycloak.admin.password=admin
```

## 🔐 Lokale Secrets (nicht im Git)
**Datei:** `/local.properties` oder `**/local.properties`
**Im Git:** ❌ Nein (via `.gitignore`)

Überschreibt automatisch Werte aus `testing.properties`:

```properties
# Echte Credentials für lokales PostgreSQL/Keycloak
db.jeeeraaah.password=mein_sicheres_passwort
keycloak.admin.password=super_geheim
```

## ⚙️ MicroProfile Config - Priorität (höchste zuerst)

1. **System Properties** (`-Ddb.password=xyz`)
2. **Umgebungsvariablen** (`DB_PASSWORD=xyz`)
3. **`local.properties`** (lokale Overrides, nicht im Git)
4. **`testing.properties`** (Development-Defaults, im Git)
5. **`META-INF/microprofile-config.properties`** (selten benötigt)

## ✅ Was funktioniert jetzt

- ✅ Keine Line-Ending-Probleme mehr
- ✅ Kein Maven Resource Filtering (außer für Build-Metadaten)
- ✅ Einfaches Debugging (Werte sind direkt sichtbar)
- ✅ Getrennte Docker/Java-Konfiguration
- ✅ Lokale Overrides ohne Git-Konflikte
- ✅ Standard-konform (MicroProfile Config)

## 🚀 Schnellstart

### Docker-Umgebung starten
```bash
cd ~/develop/github/main/config/shared/docker
docker compose up -d
```

### Java-Anwendung mit Testing-Config
```bash
# Automatisch geladen: testing.properties
cd ~/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs
mvn liberty:dev
```

### Lokale Overrides (optional)
```bash
# Erstelle local.properties im Projekt-Root
cat > ~/develop/github/main/local.properties << EOF
db.jeeeraaah.password=mein_lokales_passwort
keycloak.url=http://mein-keycloak:8080
EOF
```

## 📝 Best Practices

1. **Docker-Secrets gehören in `.env`** (Infrastructure)
2. **Application-Config gehört in `testing.properties`** (Java)
3. **Echte Secrets gehören in `local.properties`** (nicht im Git!)
4. **Produktion nutzt Umgebungsvariablen** (Docker, K8s, Cloud)

## 🔧 Troubleshooting

### Problem: "Property nicht gefunden"
**Lösung:** Prüfe MicroProfile Config-Priorität (siehe oben)

```bash
# Zeige alle geladenen Properties
java -Dmp.config.debug=true -jar app.jar
```

### Problem: "Falscher Wert geladen"
**Lösung:** Lokale `local.properties` überschreibt `testing.properties`

```bash
# Entferne lokale Overrides
rm local.properties
```

### Problem: "Docker-Container verwendet falsche Credentials"
**Lösung:** Prüfe `/config/shared/docker/.env`

```bash
cd ~/develop/github/main/config/shared/docker
cat .env
# Dann: docker compose down && docker compose up -d
```

## 📚 Weitere Dokumentation
- [SIMPLIFIED-APPROACH.md](config/shared/docker/SIMPLIFIED-APPROACH.md) - Detaillierte Strategie-Beschreibung
- [MicroProfile Config Spec](https://github.com/eclipse/microprofile-config) - Offizielle Spezifikation
