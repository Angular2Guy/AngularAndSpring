#!/bin/sh
docker build -f src/docker/Dockerfile -t angular2guy:trader  .
docker run -p 8080:8080 --network="host" angular2guy:trader