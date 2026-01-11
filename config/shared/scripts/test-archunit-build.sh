#!/bin/bash

# Test Build für archunit Modul nach Lombok Fix
# Datum: 2026-01-11

echo "========================================"
echo "Testing archunit build with Lombok fix"
echo "========================================"
echo ""

# Zum archunit Verzeichnis wechseln
cd /home/r-uu/develop/github/main/root/lib/archunit

echo "1. Cleaning previous build..."
mvn clean

echo ""
echo "2. Compiling main sources..."
mvn compile

echo ""
echo "3. Compiling test sources..."
mvn test-compile

echo ""
echo "4. Running full build..."
mvn clean install

echo ""
echo "========================================"
echo "Build test completed!"
echo "========================================"

