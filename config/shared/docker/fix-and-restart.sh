#!/bin/bash
set -e

LOGFILE="/tmp/docker-fix-restart-$(date +%Y%m%d-%H%M%S).log"

# Function to log to both console and file
log() {
    echo "$@" | tee -a "$LOGFILE"
}

log "═══════════════════════════════════════════════════════════"
log "🔧 Docker Environment Fix & Restart"
log "═══════════════════════════════════════════════════════════"
log "📝 Logfile: $LOGFILE"
log ""

cd "$(dirname "$0")"

# Step 1: Fix line endings
log "Step 1: Fix .env line endings..."
sed -i 's/\r$//' .env 2>&1 | tee -a "$LOGFILE"
log "✅ Line-Endings korrigiert (CRLF → LF)"
log ""

# Step 2: Validate .env file
log "Step 2: Validate .env file..."
if grep -q $'\r' .env; then
    log "❌ ERROR: .env still has CRLF line endings!"
    exit 1
fi
log "✅ .env has correct LF line endings"
log ""

# Step 3: Show .env content
log "Step 3: Current .env configuration:"
log "────────────────────────────────────────────────────────────"
cat .env | grep -v "^#" | grep -v "^$" | tee -a "$LOGFILE"
log "────────────────────────────────────────────────────────────"
log ""

# Step 4: Stop all containers and remove volumes
log "Step 4: Stopping all containers and removing volumes..."
docker compose down -v 2>&1 | tee -a "$LOGFILE"
log "✅ All containers stopped and volumes removed"
log ""

# Step 5: Start Docker environment
log "Step 5: Starting Docker environment..."
docker compose up -d 2>&1 | tee -a "$LOGFILE"
log "✅ Docker environment started"
log ""

# Step 6: Wait for containers to initialize
log "Step 6: Waiting for containers to initialize (30s)..."
for i in {1..30}; do
    echo -n "." | tee -a "$LOGFILE"
    sleep 1
done
log " done!"
log ""

# Step 7: Show container status
log "Step 7: Container Status:"
log "═══════════════════════════════════════════════════════════"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" 2>&1 | tee -a "$LOGFILE"
log "═══════════════════════════════════════════════════════════"
log ""

# Step 8: Check PostgreSQL databases
log "Step 8: Checking PostgreSQL databases..."
log ""
log "📊 postgres-jeeeraaah databases:"
docker exec postgres-jeeeraaah psql -U jeeeraaah -d jeeeraaah -c "\l" 2>&1 | grep -E "jeeeraaah|lib_test" | tee -a "$LOGFILE" || log "⚠️  Could not verify databases yet (container may still be initializing)"
log ""

log "📊 postgres-keycloak database:"
docker exec postgres-keycloak psql -U keycloak -d keycloak -c "\l" 2>&1 | grep keycloak | tee -a "$LOGFILE" || log "⚠️  Could not verify database yet (container may still be initializing)"
log ""

# Step 9: Setup Keycloak realm
log "Step 9: Setting up Keycloak realm..."
log ""
# Step 9: Setup Keycloak realm
log "Step 9: Setting up Keycloak realm..."
log ""
cd /home/r-uu/develop/github/main/root/lib/keycloak.admin
mvn exec:java -Dexec.mainClass="de.ruu.lib.keycloak.admin.setup.KeycloakRealmSetup" -q 2>&1 | tee -a "$LOGFILE"
log ""

# Step 10: Final health check
log "Step 10: Final health check..."
cd /home/r-uu/develop/github/main/config/shared/docker
log ""
log "Container Health Status:"
docker ps --format "table {{.Names}}\t{{.Status}}" 2>&1 | tee -a "$LOGFILE"
log ""
log "═══════════════════════════════════════════════════════════"
log "✅ Docker Environment Setup Complete!"
log "═══════════════════════════════════════════════════════════"
log ""
log "Next steps:"
log "  1. Start Liberty: cd ~/develop/github/main/root/app/jeeeraaah/backend/api/ws.rs && mvn liberty:dev"
log "  2. Run DashAppRunner from IntelliJ"
log ""
log "📝 Full log available at: $LOGFILE"
