#!/bin/sh
docker run -p 27017:27017 mongo:3.6.6
docker run -p 8080:8080 --network="host" angular2guy/angularandspring:latest