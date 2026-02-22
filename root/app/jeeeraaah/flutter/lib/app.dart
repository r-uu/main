import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';

import 'screens/dashboard_screen.dart';
import 'screens/login_screen.dart';
import 'screens/task_group_management_screen.dart';
import 'screens/task_management_screen.dart';
import 'services/api_client.dart';
import 'services/auth_service.dart';
import 'services/task_group_service.dart';
import 'services/task_service.dart';

/// Application root widget with dependency injection and declarative routing.
///
/// Service wiring mirrors the CDI injection graph in the JavaFX frontend:
///   AuthService     → KeycloakAuthService
///   ApiClient       → JAX-RS Jersey client + AuthorizationHeaderFilter
///   TaskService     → TaskServiceClient
///   TaskGroupService → TaskGroupServiceClient
class JeeeraaahApp extends StatelessWidget {
  JeeeraaahApp({super.key});

  // ── Service layer ──────────────────────────────────────────────────────────

  final _authService = AuthService(
    // Mirror defaults from microprofile-config.properties and keycloak config.
    // Override at build time via --dart-define or environment-specific flavours.
    keycloakBaseUrl: const String.fromEnvironment(
      'KEYCLOAK_BASE_URL',
      defaultValue: 'http://localhost:8180',
    ),
    realm: const String.fromEnvironment(
      'KEYCLOAK_REALM',
      defaultValue: 'jeeeraaah',
    ),
    clientId: const String.fromEnvironment(
      'KEYCLOAK_CLIENT_ID',
      defaultValue: 'jeeeraaah-client',
    ),
  );

  late final _apiClient = ApiClient(
    scheme: const String.fromEnvironment('API_SCHEME', defaultValue: 'http'),
    host: const String.fromEnvironment('API_HOST', defaultValue: 'localhost'),
    port: int.tryParse(
        const String.fromEnvironment('API_PORT', defaultValue: '9080')) ??
        9080,
    tokenProvider: _authService.accessToken,
  );

  late final _taskService = TaskService(_apiClient);
  late final _taskGroupService = TaskGroupService(_apiClient);

  // ── Router ─────────────────────────────────────────────────────────────────

  late final _router = GoRouter(
    initialLocation: '/login',
    redirect: (context, state) async {
      final token = await _authService.accessToken;
      final loggedIn = token != null;
      final onLogin = state.matchedLocation == '/login';
      if (!loggedIn && !onLogin) return '/login';
      if (loggedIn && onLogin) return '/dashboard';
      return null;
    },
    routes: [
      GoRoute(
        path: '/login',
        builder: (_, __) => const LoginScreen(),
      ),
      GoRoute(
        path: '/dashboard',
        builder: (_, __) => const DashboardScreen(),
      ),
      GoRoute(
        path: '/tasks',
        builder: (_, __) => const TaskManagementScreen(),
      ),
      GoRoute(
        path: '/taskgroups',
        builder: (_, __) => const TaskGroupManagementScreen(),
      ),
    ],
  );

  // ── Build ──────────────────────────────────────────────────────────────────

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        Provider<AuthService>.value(value: _authService),
        Provider<ApiClient>.value(value: _apiClient),
        Provider<TaskService>.value(value: _taskService),
        Provider<TaskGroupService>.value(value: _taskGroupService),
      ],
      child: MaterialApp.router(
        title: 'jeeeraaah',
        routerConfig: _router,
        theme: ThemeData(
          colorScheme: ColorScheme.fromSeed(seedColor: Colors.indigo),
          useMaterial3: true,
        ),
      ),
    );
  }
}
