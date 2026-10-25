#!/bin/zsh
set -e

# Fast check: If MySQL is already listening on port 3306, exit immediately with zero delay.
if nc -z 127.0.0.1 3306 2>/dev/null; then
  echo "✔ MySQL is already running on port 3306."
  exit 0
fi

echo "⚡ MySQL is not running on port 3306. Attempting to start MySQL..."

# Attempt starting through brew services first, then fall back to mysql.server
if command -v brew >/dev/null 2>&1; then
  echo "Starting MySQL via 'brew services start mysql'..."
  brew services start mysql
elif command -v mysql.server >/dev/null 2>&1; then
  echo "Starting MySQL via 'mysql.server start'..."
  mysql.server start
elif [ -x /opt/homebrew/bin/mysql.server ]; then
  echo "Starting MySQL via '/opt/homebrew/bin/mysql.server start'..."
  /opt/homebrew/bin/mysql.server start
else
  echo "❌ Error: Could not locate 'brew' or 'mysql.server' to start MySQL."
  exit 1
fi

# Poll until port 3306 is open and accepting TCP connections (15-second timeout)
echo "Waiting for MySQL to accept connections on port 3306..."
TIMEOUT=15
COUNTER=0
while ! nc -z 127.0.0.1 3306 2>/dev/null; do
  sleep 1
  COUNTER=$((COUNTER + 1))
  if [ $COUNTER -ge $TIMEOUT ]; then
    echo "❌ Error: MySQL failed to start within ${TIMEOUT} seconds."
    exit 1
  fi
done

echo "✔ MySQL is now running and accepting connections on port 3306."
exit 0
