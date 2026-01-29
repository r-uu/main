#!/bin/bash
# Quick test to verify .env variables are loaded correctly

cd ~/develop/github/main/config/shared/docker

echo "════════════════════════════════════════"
echo "🔍 Testing Environment Variable Loading"
echo "════════════════════════════════════════"
echo ""

echo "1️⃣  .env file exists:"
ls -la .env

echo ""
echo "2️⃣  .env file contents (first 15 lines):"
head -15 .env

echo ""
echo "3️⃣  Testing docker compose config:"
docker compose config | head -50

echo ""
echo "════════════════════════════════════════"
echo "If you see actual values above (not \${...}), it works!"
echo "════════════════════════════════════════"
