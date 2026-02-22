import 'dart:convert';

import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

/// Keycloak authentication service.
///
/// Mirrors `KeycloakAuthService` + `AuthorizationHeaderFilter` from the Java
/// module `api.client/ws.rs`.
///
/// The service performs the OAuth 2.0 Resource Owner Password Credentials Grant
/// (direct access grant) – the same flow used by the JavaFX client.
class AuthService {
  AuthService({
    required this.keycloakBaseUrl,
    required this.realm,
    required this.clientId,
    this.clientSecret,
  });

  final String keycloakBaseUrl;
  final String realm;
  final String clientId;
  final String? clientSecret;

  static const _storage = FlutterSecureStorage();
  static const _accessTokenKey = 'jeeeraaah_access_token';
  static const _refreshTokenKey = 'jeeeraaah_refresh_token';

  String? _accessToken;
  String? _refreshToken;

  // ---------------------------------------------------------------------------
  // Public API
  // ---------------------------------------------------------------------------

  /// Returns the current access token, or null when the user is not logged in.
  /// Reads from secure storage on first call so that tokens survive app restarts.
  Future<String?> get accessToken async {
    _accessToken ??= await _storage.read(key: _accessTokenKey);
    return _accessToken;
  }

  /// Async login-state check that reads from secure storage when needed.
  Future<bool> get isLoggedIn async => (await accessToken) != null;

  /// Authenticate with username + password (Resource Owner Password Grant).
  Future<void> login(String username, String password) async {
    final uri = Uri.parse(
        '$keycloakBaseUrl/realms/$realm/protocol/openid-connect/token');

    final body = {
      'grant_type': 'password',
      'client_id': clientId,
      if (clientSecret != null) 'client_secret': clientSecret!,
      'username': username,
      'password': password,
    };

    final response = await http.post(
      uri,
      headers: {'Content-Type': 'application/x-www-form-urlencoded'},
      body: body,
    );

    if (response.statusCode != 200) {
      throw Exception('Login failed (${response.statusCode}): ${response.body}');
    }

    final json = jsonDecode(response.body) as Map<String, dynamic>;
    await _storeTokens(
      json['access_token'] as String,
      json['refresh_token'] as String?,
    );
  }

  /// Exchange the stored refresh token for a new access token.
  Future<void> refresh() async {
    _refreshToken ??= await _storage.read(key: _refreshTokenKey);
    if (_refreshToken == null) throw Exception('No refresh token available');

    final uri = Uri.parse(
        '$keycloakBaseUrl/realms/$realm/protocol/openid-connect/token');

    final body = {
      'grant_type': 'refresh_token',
      'client_id': clientId,
      if (clientSecret != null) 'client_secret': clientSecret!,
      'refresh_token': _refreshToken!,
    };

    final response = await http.post(
      uri,
      headers: {'Content-Type': 'application/x-www-form-urlencoded'},
      body: body,
    );

    if (response.statusCode != 200) {
      await logout();
      throw Exception(
          'Token refresh failed (${response.statusCode}): ${response.body}');
    }

    final json = jsonDecode(response.body) as Map<String, dynamic>;
    await _storeTokens(
      json['access_token'] as String,
      json['refresh_token'] as String?,
    );
  }

  Future<void> logout() async {
    _accessToken = null;
    _refreshToken = null;
    await _storage.delete(key: _accessTokenKey);
    await _storage.delete(key: _refreshTokenKey);
  }

  // ---------------------------------------------------------------------------
  // Private helpers
  // ---------------------------------------------------------------------------

  Future<void> _storeTokens(String access, String? refresh) async {
    _accessToken = access;
    _refreshToken = refresh;
    await _storage.write(key: _accessTokenKey, value: access);
    if (refresh != null) {
      await _storage.write(key: _refreshTokenKey, value: refresh);
    }
  }
}
