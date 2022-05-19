#!/usr/bin/env bash

# it only executed for local pc, and used local image
IFS=$'\n'
maybeOld=`docker ps | grep liguobin/zim | awk '{print $1}'`
if [[ -n "$maybeOld" ]];then
  for line in `cat version.sbt`
  do
    docker image rm liguobin/zim:$line -f
  done
fi  


sbt docker:publishLocal

fold=`pwd`

cd /opt/homebrew/var/mysql/datadir/

rm -rf ./*

cd $fold

docker-compose -f docker-compose.yml up  -d

zim_container_ip=`docker ps | grep liguobin/zim | awk '{print $1}' | xargs docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}'`
echo "zim server container ip: $zim_container_ip"


# mac osx visit docker container network? see https://www.haoyizebo.com/posts/fd0b9bd8/  and close your VPN