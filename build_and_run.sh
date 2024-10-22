#!/bin/bash

echo "Building the project..."
./gradlew clean build -x test || { echo 'Gradle build failed' ; exit 1; }

echo "Building Docker images..."
docker-compose build || { echo 'Docker Compose build failed' ; exit 1; }

echo "Starting Docker containers..."
docker-compose up -d || { echo 'Docker Compose up failed' ; exit 1; }

echo "All services are up and running!"
