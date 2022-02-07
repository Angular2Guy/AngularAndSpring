#!/bin/sh
docker pull mongo:4.4
docker run --name local-mongo -p 27017:27017 --cpus=2.0 --memory=3g mongo:4.4 --wiredTigerCacheSizeGB 2.0
#docker run --name local-mongo -p 27017:27017 --cpus=2.0 --memory=3g -v <mongo-dir>:/data/db mongo:4.4 --wiredTigerCacheSizeGB 2.0
#docker start local-mongo
#docker stop local-mongo
#docker exec -it local-mongo bash