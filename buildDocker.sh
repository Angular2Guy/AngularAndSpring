#!/bin/sh
docker build -t angular2guy/angularandspring:latest --build-arg JAR_FILE=angularandspring-0.0.1-SNAPSHOT.jar --no-cache .
docker run -p 8080:8080 --network="host" angular2guy/angularandspring:latest