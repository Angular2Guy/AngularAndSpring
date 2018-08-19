#!/bin/sh
docker build -t angular2guy/trader:latest --build-arg JAR_FILE=trader-0.0.1-SNAPSHOT.jar --no-cache .
docker run -p 8080:8080 --network="host" angular2guy/trader:latest