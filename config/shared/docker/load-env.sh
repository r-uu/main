#!/bin/bash
# Load environment variables from .env file
# This script is sourced by other scripts to ensure consistent variable loading
if [ -f ".env" ]; then
    export $(grep -v '^#' .env | xargs)
else
    echo "❌ ERROR: .env file not found!"
    exit 1
fi
