#!/bin/sh

docker build -t flow-management-ui .

docker-compose -f ClusterDeployment.yaml up 