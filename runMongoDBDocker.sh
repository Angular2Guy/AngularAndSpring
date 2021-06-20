#!/bin/sh
docker pull mongo:3.6
docker run --name local-mongo -p 27017:27017 mongo:3.6
#docker start local-mongo
#docker stop local-mongo