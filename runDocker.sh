#!/bin/sh
docker pull mongo:4.4
docker run --name local-mongo -p 27017:27017 --cpus=2.0 --volume tmp/mongodb --memory=3g mongo:4.4 --wiredTigerCacheSizeGB 2.0
docker run -p 8080:8080 --memory="1g" --network="host" angular2guy/angularandspring:latest