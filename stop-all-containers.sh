#!/bin/bash

# Record the start time
start_time=$(date +%s)

echo "Shutting down..."

# Stop the Docker containers
docker-compose -f src/main/docker/postgresql.yml down
docker-compose -f src/main/docker/redis.yml down
docker-compose -f synonym/docker-compose.yml down
docker-compose -f similarity/docker-compose.yml down
docker-compose -f minio/docker-compose.yml stop
docker-compose -f cola/docker-compose.yml down
docker-compose -f meta/docker-compose.yml down
docker-compose -f transcribe/docker-compose.yml down
docker-compose -f transcribe2/docker-compose.yml down

# Kill the background process started with 'ollama run'
# Note: Replace 'dolphin-mixtral' with the actual process name if different
pkill -f "ollama run dolphin-mixtral:latest"

echo "All services stopped."

# Record the end time
end_time=$(date +%s)

# Calculate duration
duration=$((end_time - start_time))

echo "Total shutdown duration: $duration seconds."
