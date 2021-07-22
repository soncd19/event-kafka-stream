#!/usr/bin/env bash

# build project
mvn clean install -Dmaven.test.skip=true

# build docker image with image name
imageName=vnevent-stream
sudo docker build -t $imageName/$imageName -f Dockerfile .

# stop docker compose before start
sudo docker-compose -f docker-compose.yml down

# run docker compose
sudo docker-compose -f docker-compose.yml up --build
