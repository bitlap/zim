#!/usr/bin/env bash

image=`docker ps | grep liguobin/zim | awk '{print $2}'`

docker push $image
