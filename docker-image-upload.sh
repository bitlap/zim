#!/usr/bin/env bash

tag=$1

sbt docker:publishLocal

docker login -u liguobin 
docker push liguobin/zim:$tag