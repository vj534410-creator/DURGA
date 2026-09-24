#!/usr/bin/env bash

echo "=========================================="
echo "STARTING ALL DURGA PLATFORM SERVICES"
echo "=========================================="

# 1. Start Backend REST API
echo "Starting Backend REST API on port 3000..."
export PORT=3000
node backend/dist/index.js &
BACKEND_PID=$!

# 2. Start Admin Panel
echo "Starting Admin Panel on port 3001..."
export ADMIN_PORT=3001
node admin-panel/server.js &
ADMIN_PID=$!

# 3. Start Officer Portal
echo "Starting Police Officer Portal on port 3002..."
export OFFICER_PORT=3002
node officer-portal/server.js &
OFFICER_PID=$!

echo "DURGA Platform Services are running:"
echo " - Backend API:    http://localhost:3000 (Health: http://localhost:3000/health)"
echo " - Admin Panel:    http://localhost:3001"
echo " - Officer Portal: http://localhost:3002"
echo ""
echo "Press CTRL+C to stop all services."

trap "kill $BACKEND_PID $ADMIN_PID $OFFICER_PID 2>/dev/null; exit" SIGINT SIGTERM
wait
