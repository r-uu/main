# App-Klassen Konsolidierung

## Übersicht

Alle JavaFX-App-Klassen wurden nach dem Muster von `DashApp` konsolidiert, um eine einheitliche Initialisierung und robuste Fehlerbehandlung zu gewährleisten.

## Konsolidierte App-Klassen

Die folgenden App-Klassen wurden aktualisiert:

1. **DashApp** (Referenz-Implementierung)
   - `root/app/jeeeraaah/frontend/ui/fx/src/main/java/de/ruu/app/jeeeraaah/frontend/ui/fx/dash/DashApp.java`

2. **GanttApp** 
   - `root/app/jeeeraaah/frontend/ui/fx/src/main/java/de/ruu/app/jeeeraaah/frontend/ui/fx/task/gantt/GanttApp.java`

3. **MainApp**
   - `root/app/jeeeraaah/frontend/ui/fx/src/main/java/de/ruu/app/jeeeraaah/frontend/ui/fx/MainApp.java`

4. **TaskManagementApp**
   - `root/app/jeeeraaah/frontend/ui/fx/src/main/java/de/ruu/app/jeeeraaah/frontend/ui/fx/task/TaskManagementApp.java`

5. **TaskGroupManagementApp**
   - `root/app/jeeeraaah/frontend/ui/fx/src/main/java/de/ruu/app/jeeeraaah/frontend/ui/fx/taskgroup/TaskGroupManagementApp.java`

## Gemeinsames Startup-Pattern

Alle App-Klassen folgen jetzt diesem einheitlichen Startup-Flow:

### 1. Configuration Health Check
```java
de.ruu.lib.util.config.mp.ConfigHealthCheck configCheck = new de.ruu.lib.util.config.mp.ConfigHealthCheck();
de.ruu.lib.util.config.mp.ConfigHealthCheck.Result configResult = configCheck.validate();
```

Validiert alle erforderlichen Konfigurationseigenschaften:
- Database-Konfiguration (jeeeraaah, lib_test)
- Keycloak-Konfiguration (Admin-Credentials, Test-Credentials)
- Service-URLs (Backend, JasperReports)

### 2. Docker Environment Health Check mit Auto-Fix
```java
de.ruu.lib.docker.health.HealthCheckRunner healthCheckRunner =
    de.ruu.lib.docker.health.HealthCheckProfiles.fullEnvironment();

de.ruu.lib.docker.health.fix.AutoFixRunner autoFix = 
    new de.ruu.lib.docker.health.fix.AutoFixRunner(healthCheckRunner);
autoFix.registerStrategy(new de.ruu.lib.docker.health.fix.DockerContainerStartStrategy());
autoFix.registerStrategy(new de.ruu.lib.docker.health.fix.KeycloakRealmSetupStrategy());

autoFix.runWithAutoFix();
```

Prüft und repariert automatisch:
- Docker Daemon Status
- PostgreSQL-Container (jeeeraaah, lib_test, keycloak)
- Keycloak-Container
- Keycloak-Realm Setup
- JasperReports-Service

### 3. Authentication
#### Testing Mode (automatischer Login)
```java
boolean testingMode = ConfigProvider.getConfig()
    .getOptionalValue("testing", Boolean.class)
    .orElse(false);

if (testingMode && !authService.isLoggedIn()) {
    // Auto-login mit keycloak.test.user und keycloak.test.password
    authService.login(testUsername, testPassword);
}
```

#### Interaktiver Login
```java
while (!authService.isLoggedIn()) {
    LoginDialog loginDialog = CDI.current().select(LoginDialog.class).get();
    boolean loginSuccessful = loginDialog.showAndWait(primaryStage);
    
    if (!loginSuccessful) {
        Platform.exit();
        return;
    }
}
```

### 4. UI Initialization
```java
super.start(primaryStage);
```

### 5. Cleanup beim Shutdown
```java
@Override
public void stop() throws Exception {
    if (authService != null && authService.isLoggedIn()) {
        authService.logout();
    }
    super.stop();
}
```

## Vorteile der Konsolidierung

### 1. Einheitlichkeit
- Alle Apps verwenden dieselbe Initialisierungslogik
- Gleiche Fehlerbehandlung überall
- Konsistente Logging-Ausgaben

### 2. Robustheit
- **Configuration Health Check**: Früherkennung von Konfigurationsproblemen
- **Docker Auto-Fix**: Automatische Behebung von Container-Problemen
- **Retry-Logik**: Bei fehlgeschlagenem Login kann Benutzer wiederholen
- **Null-Safety**: Prüfung von `authService != null` vor Verwendung

### 3. Testbarkeit
- **Testing Mode Support**: Automatischer Login für automatisierte Tests
- Konfigurierbar über `testing.properties`
- Keine manuelle Interaktion erforderlich

### 4. Debugging-Freundlichkeit
- Detaillierte Logging-Ausgaben auf jeder Stufe
- Emojis (✅ ❌) für schnelle visuelle Erkennung
- Instance-IDs für CDI-Bean-Tracking

### 5. Benutzerfreundlichkeit
- **Health Check Error Dialog**: Zeigt konkrete Probleme und Lösungsvorschläge
- **Auto-Fix**: Behebt viele Probleme automatisch
- **Retry-Möglichkeit**: Benutzer muss bei Login-Fehler nicht App neu starten

## Konfiguration

### Testing Properties (testing.properties)
```properties
# Testing mode (enables auto-login)
testing=true

# Test credentials for auto-login
keycloak.test.user=test
keycloak.test.password=test
```

### MicroProfile Config Properties
```properties
# Keycloak Configuration
keycloak.auth.server.url=http://localhost:8080
keycloak.realm=jeeeraaah-realm
keycloak.client-id=jeeeraaah-frontend

# Database Configuration
db.jeeeraaah.name=jeeeraaah
db.jeeeraaah.username=jeeeraaah
db.jeeeraaah.password=jeeeraaah

# ... weitere Konfigurationen
```

## Migration von alten App-Klassen

Falls weitere App-Klassen existieren, die noch nicht konsolidiert wurden:

1. **Imports hinzufügen**:
   ```java
   import org.eclipse.microprofile.config.ConfigProvider;
   ```

2. **@Inject entfernen**, stattdessen CDI.current() verwenden:
   ```java
   // Alt:
   @Inject
   private KeycloakAuthService authService;
   
   // Neu:
   private KeycloakAuthService authService;
   
   // In start():
   authService = CDI.current().select(KeycloakAuthService.class).get();
   ```

3. **Config Health Check einfügen** (vor Authentication)

4. **Docker Health Check mit Auto-Fix einfügen** (nach Config Check)

5. **Testing Mode Support einfügen** (in Authentication-Phase)

6. **Retry-Logik in while-Schleife** (statt einfachem if)

7. **showHealthCheckErrorDialog() Methode hinzufügen**

8. **stop() Methode aktualisieren** (null-check für authService)

## Weitere App-Klassen

Die folgenden App-Klassen wurden identifiziert, haben aber entweder keine Authentifizierung
oder sind leere Wrapper-Klassen, die vermutlich nur für Tests verwendet werden:

- TaskViewApp
- TaskListDirectNeighboursApp
- TaskHierarchySuccessorsApp
- TaskEditorApp
- TaskGroupSelectorApp
- TaskSelectorApp
- TaskDirectNeighbourSuperApp

Diese sollten bei Bedarf ebenfalls nach demselben Muster konsolidiert werden.

## Testing

Nach der Konsolidierung sollten folgende Szenarien getestet werden:

1. **Happy Path**: Alle Container laufen, Config korrekt → Login-Dialog → UI startet
2. **Testing Mode**: `testing=true` → Automatischer Login → UI startet
3. **Config-Fehler**: Fehlende Properties → Fehlerausgabe → App beendet
4. **Docker-Fehler**: Container nicht gestartet → Auto-Fix → UI startet
5. **Login-Abbruch**: Benutzer bricht Login ab → App beendet
6. **Login-Fehler**: Falsches Passwort → Retry-Dialog → Erneuter Versuch möglich

## Status

✅ **Abgeschlossen**:
- DashApp (Referenz)
- GanttApp
- MainApp
- TaskManagementApp
- TaskGroupManagementApp

⏳ **Optional** (bei Bedarf):
- Weitere Test/Helper-App-Klassen

---

*Letzte Aktualisierung: 2026-02-06*
