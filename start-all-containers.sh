start_time=$(date +%s)

echo "Starting up..."
docker-compose -f src/main/docker/postgresql.yml up -d
docker-compose -f synonym/docker-compose.yml up -d
docker-compose -f similarity/docker-compose.yml up -d
docker-compose -f minio/docker-compose.yml up -d
docker-compose -f cola/docker-compose.yml up -d
ollama run dolphin-mixtral:latest &

curl http://localhost:11434/api/generate -d '{
  "model": "dolphin-mixtral",
  "prompt": "2+2", "stream" : false
}'

echo "Done!"

end_time=$(date +%s)

# Calculate duration
duration=$((end_time - start_time))

echo "Total duration: $duration seconds."
