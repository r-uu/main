# jeeeraaah Flutter – Windows Desktop Client

Flutter-Port des JavaFX-Frontends für die jeeeraaah-Aufgabenverwaltung.

## Warum Flutter?

| Kriterium | JavaFX (bisher) | Flutter (neu) |
|-----------|-----------------|---------------|
| Zielplattform | Desktop (Windows/Linux/macOS) | Desktop **+** Web **+** Mobile aus einem Code-Basis |
| Toolchain | JDK + Maven + OpenJFX | Flutter SDK (Dart) |
| UI-Paradigma | FXML + Controller-Klassen | Declarative Widget-Tree |
| Hotreload | Nein | Ja (sehr schnell) |
| Native Windows-App | Ja (JAR + JRE) | Ja (.exe, kein JRE nötig) |

## Projektstruktur

```
flutter/
├── lib/
│   ├── main.dart                    # Einstiegspunkt (ersetzt *AppRunner.java)
│   ├── app.dart                     # App-Root, DI-Setup, Router
│   ├── models/
│   │   ├── task.dart                # Dart-Pendant zu TaskDTO / TaskBean
│   │   └── task_group.dart          # Dart-Pendant zu TaskGroupDTO / TaskGroupBean
│   ├── services/
│   │   ├── api_client.dart          # HTTP-Client (ersetzt Jersey JAX-RS Client)
│   │   ├── auth_service.dart        # Keycloak ROPG (ersetzt KeycloakAuthService)
│   │   ├── task_service.dart        # REST-Calls /task/** (ersetzt TaskServiceClient)
│   │   └── task_group_service.dart  # REST-Calls /taskgroup/** (ersetzt TaskGroupServiceClient)
│   ├── screens/
│   │   ├── login_screen.dart        # Entspricht LoginDialog.java
│   │   ├── dashboard_screen.dart    # Entspricht DashApp / DashController
│   │   ├── task_management_screen.dart       # Entspricht TaskManagementController
│   │   └── task_group_management_screen.dart # Entspricht TaskGroupManagementController
│   └── widgets/
│       ├── task_group_selector.dart # Entspricht TaskGroupSelector-Komponente
│       └── task_tree_view.dart      # Entspricht TreeView / TreeItem-Hierarchien
└── test/
    └── model_test.dart              # Unit-Tests für Task / TaskGroup-Modelle
```

## Voraussetzungen

1. **Flutter SDK installieren** (≥ 3.19):
   ```powershell
   # Windows: winget oder chocolatey
   winget install FlutterDev.Flutter
   # oder manuell: https://docs.flutter.dev/get-started/install/windows
   ```
2. **Windows Desktop aktivieren:**
   ```powershell
   flutter config --enable-windows-desktop
   ```
3. **Visual Studio 2022** mit „Desktop development with C++" Workload (für den nativen Windows-Build).

## Einrichten & Starten

```powershell
# Ins Flutter-Verzeichnis wechseln
cd root/app/jeeeraaah/flutter

# Abhängigkeiten installieren
flutter pub get

# App als Windows-Desktop-App starten
flutter run -d windows
```

## Konfiguration

Die Verbindungsparameter können zur Build-Zeit per `--dart-define` übergeben
werden (entspricht `microprofile-config.properties` im Java-Modul):

```powershell
flutter run -d windows \
  --dart-define=API_HOST=localhost \
  --dart-define=API_PORT=9080 \
  --dart-define=KEYCLOAK_BASE_URL=http://localhost:8180 \
  --dart-define=KEYCLOAK_REALM=jeeeraaah \
  --dart-define=KEYCLOAK_CLIENT_ID=jeeeraaah-client
```

## Tests ausführen

```powershell
flutter test
```

## Release-Build (Windows .exe)

```powershell
flutter build windows --release
# Ergebnis: build/windows/x64/runner/Release/jeeeraaah.exe
```

## Mapping: Java → Flutter

| Java (JavaFX) | Flutter (Dart) |
|---------------|----------------|
| `JavaFX Application` | `runApp(JeeeraaahApp())` |
| `FXML + Controller` | `StatefulWidget` |
| `CDI `@Inject` | `Provider.of<T>(context)` |
| `ObservableValue<T>` | `setState()` / `ChangeNotifier` |
| `REST via Jersey` | `http.get/post/put/delete` |
| `KeycloakAuthService` | `AuthService` (ROPG via `http`) |
| `TaskServiceClient` | `TaskService` |
| `TaskGroupServiceClient` | `TaskGroupService` |
| `TaskDTO / TaskBean` | `Task` (Dart model) |
| `TaskGroupDTO / TaskGroupBean` | `TaskGroup` (Dart model) |
| `TreeView<TaskBean>` | `TaskTreeView` widget |
| `ComboBox<TaskGroupBean>` | `TaskGroupSelector` widget |

## Backend

Das Flutter-Frontend kommuniziert mit demselben Open-Liberty-Backend wie die
JavaFX-App. Der Backend-Server muss laufen, bevor die Flutter-App gestartet wird:

```bash
# Im Projekt-Root
mvn liberty:dev -pl root/app/jeeeraaah/backend/api/open-liberty
```

Siehe [GETTING-STARTED.md](../../../../GETTING-STARTED.md) für die vollständige
Setup-Anleitung inklusive PostgreSQL und Keycloak.
