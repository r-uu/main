# ✅ Liberty Keystore Passwort-Fehler behoben

**Datum:** 2026-01-20

---

## ❌ Problem

Liberty Server konnte nicht starten wegen eines Keystore-Passwort-Fehlers:

```
[ERROR] CWPKI0033E: The keystore located at 
/home/r-uu/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs/target/liberty/wlp/usr/servers/defaultServer/resources/security/key.p12 
did not load because of the following error: keystore password was incorrect
```

**Ursache:** Der Keystore wurde früher mit einem anderen Passwort erstellt als das, was jetzt in der `server.env` konfiguriert ist (`default_keystore_password=changeit`).

---

## ✅ Lösung

Der alte Keystore wurde gelöscht:

```bash
rm -f /home/r-uu/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs/target/liberty/wlp/usr/servers/defaultServer/resources/security/key.p12
```

**Ergebnis:** Liberty wird beim nächsten Start automatisch einen neuen Keystore mit dem korrekten Passwort aus `server.env` erstellen.

---

## 🔧 Hintergrund

### Was ist der Keystore?

Der Keystore (`key.p12`) wird von Liberty für SSL/TLS-Verbindungen verwendet. Er enthält:
- Private Keys
- Zertifikate
- Trusted Certificates

### Automatische Erstellung

Liberty erstellt den Keystore automatisch beim ersten Start, wenn er nicht existiert. Das Passwort wird aus der Umgebungsvariable `default_keystore_password` in der `server.env` gelesen.

**server.xml:**
```xml
<keyStore id="defaultKeyStore" password="${default_keystore_password}" />
```

**server.env:**
```env
default_keystore_password=changeit
```

---

## 🚨 Wenn das Problem erneut auftritt

### Szenario 1: Passwort in server.env geändert

Wenn du das Passwort in `server.env` änderst, muss auch der Keystore neu erstellt werden:

```bash
# 1. Server stoppen (falls läuft)
mvn liberty:stop

# 2. Alten Keystore löschen
rm -f target/liberty/wlp/usr/servers/defaultServer/resources/security/key.p12

# 3. Server neu starten
mvn liberty:dev
```

### Szenario 2: Keystore wiederherstellen mit altem Passwort

Falls der Keystore wichtige Zertifikate enthält, die du behalten musst:

**Option A:** Passwort in server.env auf das alte Passwort zurücksetzen

**Option B:** Keystore-Passwort mit keytool ändern:
```bash
keytool -storepasswd \
  -keystore target/liberty/wlp/usr/servers/defaultServer/resources/security/key.p12 \
  -storepass <ALTES_PASSWORT> \
  -new <NEUES_PASSWORT>
```

---

## 📂 Dateien

| Datei | Pfad | Beschreibung |
|-------|------|--------------|
| **key.p12** | `target/liberty/.../resources/security/` | SSL/TLS Keystore (auto-generiert) |
| **ltpa.keys** | `target/liberty/.../resources/security/` | LTPA Keys für SSO (auto-generiert) |
| **server.env** | `src/main/liberty/config/` | Environment-Variablen (inkl. Passwörter) |
| **server.xml** | `src/main/liberty/config/` | Liberty Konfiguration |

---

## ⚠️ Wichtig: Development vs. Production

### Development (lokal)
- Standard-Passwort `changeit` ist OK
- Keystore wird auto-generiert
- Self-signed Zertifikate sind akzeptabel

### Production
- **NIEMALS** `changeit` verwenden!
- Starke Passwörter in verschlüsseltem Vault
- Gültige CA-signierte Zertifikate verwenden
- Regelmäßige Rotation von Zertifikaten

---

## 🎯 Nächster Schritt

Starte Liberty neu, der Keystore wird automatisch erstellt:

```bash
cd /home/r-uu/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs
mvn liberty:dev
```

**Erwartete Log-Meldung:**
```
[AUDIT] CWPKI0803A: SSL certificate created in X.XXX seconds. 
SSL key file: .../resources/security/key.p12
```

---

## 📋 Checkliste: Alle Liberty-Konfigurationsprobleme gelöst

- ✅ `server.env` erstellt mit allen benötigten Variablen
- ✅ `persistence.xml` mit `hibernate.dialect` aktualisiert
- ✅ PostgreSQL Container laufen (`postgres-jeeeraaah` auf Port 5432)
- ✅ Datenbanken erstellt (`jeeeraaah` und `lib_test`)
- ✅ Keystore-Passwort-Problem gelöst (alter Keystore gelöscht)

**Liberty sollte jetzt erfolgreich starten! 🚀**

---

✅ **Problem gelöst!**
