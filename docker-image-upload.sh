#!/usr/bin/env bash

tag=$1

sbt docker:publishLocal

docker push liguobin/zim:$tag