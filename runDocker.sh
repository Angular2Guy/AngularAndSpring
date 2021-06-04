#!/bin/sh
docker run -p 27017:27017 --memory="2g" mongo:3.6
docker run -p 8080:8080 --memory="1g" --network="host" angular2guy/angularandspring:latest