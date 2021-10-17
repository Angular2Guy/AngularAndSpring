#!/bin/sh
#docker pull prom/prometheus
LOCALDIR=pwd
docker run --network "host" -v "$LOCALDIR/prometheus-local.yml:/etc/prometheus/prometheus.yml" prom/prometheus