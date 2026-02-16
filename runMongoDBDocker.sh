#!/bin/sh
docker pull mongo:8.2
docker run --name local-mongo -p 27017:27017 --cpus=2.0 --memory=4g mongo:8.2 --wiredTigerCacheSizeGB 3.0
#docker run --name local-mongo -p 27017:27017 --cpus=1.0 --memory=2g -v <mongo-dir>:/data/db mongo:6.0 --wiredTigerCacheSizeGB 1.0
#docker start local-mongo
#docker stop local-mongo
#docker exec -it local-mongo bash