#!/bin/sh

## Remove the CPIs generated for this deployment.
rm corDappCpiUploadStatus.json
rm notaryCpiUploadStatus.json 
rm currentCPI.json

## Remove all the docker containers for this deployment.
docker-compose -f ../setup1-environment/ClusterDeployment.yaml down

rm -r ../workspace