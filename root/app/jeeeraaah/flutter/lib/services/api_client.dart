import 'dart:convert';
import 'dart:io';

import 'package:http/http.dart' as http;

/// Thrown when an HTTP call returns a non-2xx status code.
class ApiException implements Exception {
  const ApiException(this.statusCode, this.message);

  final int statusCode;
  final String message;

  @override
  String toString() => 'ApiException($statusCode): $message';
}

/// Low-level HTTP client that mirrors the Jersey/JAX-RS client used in the
/// JavaFX frontend.
///
/// Configuration mirrors `microprofile-config.properties` in the Java module
/// `api.client/ws.rs`:
///   jeeeraaah.rest-api.scheme=http
///   jeeeraaah.rest-api.host=localhost
///   jeeeraaah.rest-api.port=9080
class ApiClient {
  ApiClient({
    String scheme = 'http',
    String host = 'localhost',
    int port = 9080,
    this.tokenProvider,
  }) : _base = Uri(scheme: scheme, host: host, port: port);

  final Uri _base;

  /// Called before every request to obtain the current Bearer token.
  /// Supply an [AuthService] callback here.
  final Future<String?> Function()? tokenProvider;

  // -------------------------------------------------------------------------
  // helpers
  // -------------------------------------------------------------------------

  Uri _uri(String path) => _base.replace(path: path);

  Future<Map<String, String>> _headers() async {
    final headers = <String, String>{
      HttpHeaders.contentTypeHeader: 'application/json',
      HttpHeaders.acceptHeader: 'application/json',
    };
    if (tokenProvider != null) {
      final token = await tokenProvider!();
      if (token != null) {
        headers[HttpHeaders.authorizationHeader] = 'Bearer $token';
      }
    }
    return headers;
  }

  void _checkStatus(http.Response response) {
    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw ApiException(response.statusCode, response.body);
    }
  }

  // -------------------------------------------------------------------------
  // HTTP verbs
  // -------------------------------------------------------------------------

  Future<dynamic> get(String path) async {
    final response = await http.get(_uri(path), headers: await _headers());
    _checkStatus(response);
    return jsonDecode(response.body);
  }

  Future<dynamic> post(String path, Object body) async {
    final response = await http.post(
      _uri(path),
      headers: await _headers(),
      body: jsonEncode(body),
    );
    _checkStatus(response);
    if (response.body.isEmpty) return null;
    return jsonDecode(response.body);
  }

  Future<dynamic> put(String path, Object body) async {
    final response = await http.put(
      _uri(path),
      headers: await _headers(),
      body: jsonEncode(body),
    );
    _checkStatus(response);
    if (response.body.isEmpty) return null;
    return jsonDecode(response.body);
  }

  Future<void> delete(String path) async {
    final response =
        await http.delete(_uri(path), headers: await _headers());
    _checkStatus(response);
  }
}
