#!/bin/bash
# Keycloak Health Check Script
# Uses environment variables from container

set -e

# Test if Keycloak health endpoint responds
exec 3<>/dev/tcp/localhost/9000
echo -e "GET /health/ready HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n" >&3
grep -q '"status": "UP"' <&3
