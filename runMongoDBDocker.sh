#!/bin/sh
docker pull mongo:3.6.6
docker run -p 27017:27017 mongo:3.6.6