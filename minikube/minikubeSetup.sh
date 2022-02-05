#!/bin/sh
# execute helmCommand.sh

kubectl get services
minikube ip

minikube config set memory 16384
minikube config set cpu 2
minikube config set driver docker
minikube addons list
minikube addons enable metrics-server
kubectl edit deployment -n kube-system metrics-server

kubectl exec --stdin --tty <mongodb-pod-name> -- /bin/bash
kubectl expose pod <pod-name> --port=27017 --type="NodePort"
mongorestore --gzip mongodb://192.168.49.2:<exposed-port>


minikube pause
minikube unpause
