#!/usr/bin/env bash

# it can only executed for local pc, and used local image
IFS=$'\n'
maybeOld=`docker ps | grep liguobin/zim | awk '{print $1}'`
if [[ -n "$maybeOld" ]];then
  for line in `cat version.sbt`
  do
    docker image rm liguobin/zim:$line -f
  done
fi  


sbt docker:publishLocal

if [[ -d "/opt/homebrew/var/mysql/datadir/" ]];then
  rm -rf /opt/homebrew/var/mysql/datadir/*
else
  mkdir -p /opt/homebrew/var/mysql/datadir/
fi

if [[ -d "/opt/homebrew/var/mysql/datadir/" ]];then
    docker-compose -f docker-compose.yml up  -d
    zim_container_ip=`docker ps | grep liguobin/zim | awk '{print $1}' | xargs docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}'`
    echo "zim server container ip: $zim_container_ip"
else
  echo "Not found a folder named /opt/homebrew/var/mysql/datadir/ for docker mysql"
  exit -1
fi

# mac osx visit docker container network? see https://www.haoyizebo.com/posts/fd0b9bd8/  and close your VPN