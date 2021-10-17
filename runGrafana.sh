#!/bin/sh
#docker pull grafana/grafana
MYID=`id -u`
#data will be stored in ~/tmp/data
#mkdir ~/tmp/data
MYDATA=echo ~/tmp/data
docker run --user "$MYID" --volume "$MYID:$MYDATA" --network "host" grafana/grafana