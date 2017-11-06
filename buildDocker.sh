#!/bin/sh
docker build -t angular2guy/trader:latest  .
docker run -p 8080:8080 --network="host" angular2guy/trader:latest