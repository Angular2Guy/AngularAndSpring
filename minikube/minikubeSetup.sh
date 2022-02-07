#!/bin/sh
# execute helmCommand.sh

kubectl get services
minikube ip
http://<minikube ip>:<node-port>/

minikube config set memory 16384
minikube config set cpu 2
minikube config set driver docker
minikube addons list
minikube addons enable metrics-server
kubectl edit deployment -n kube-system metrics-server

kubectl logs --previous <pod-name>
kubectl exec --stdin --tty <mongodb-pod-name> -- /bin/bash
kubectl expose pod <mongodb-pod-name> --port=27017 --type="NodePort"
mongorestore -v --gzip mongodb://<minikube ip>:<exposed-port>

minikube pause
minikube unpause
