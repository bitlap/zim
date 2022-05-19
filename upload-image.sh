#!/usr/bin/env bash

# It can only be executed after `docker-deploy.sh`
image=`docker images | grep liguobin/zim | awk '{print $1}'`
tag=`docker images | grep liguobin/zim | awk '{print $2}'`

docker push $image:$tag