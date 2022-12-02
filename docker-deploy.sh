#!/usr/bin/env bash

#sbt clean compile

cp modules/zim-server/src/main/resources/application-docker.conf modules/zim-server/src/main/resources/application-docker-copy.conf
cp modules/zim-server/src/main/resources/application.conf modules/zim-server/src/main/resources/application-copy.conf
mv modules/zim-server/src/main/resources/application-docker.conf modules/zim-server/src/main/resources/application.conf

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

mv modules/zim-server/src/main/resources/application-copy.conf modules/zim-server/src/main/resources/application.conf
mv modules/zim-server/src/main/resources/application-docker-copy.conf modules/zim-server/src/main/resources/application-docker.conf
mv modules/zim-server/target/scala-2.13/classes/application-copy.conf modules/zim-server/target/scala-2.13/classes/application.conf


if [[ -d "/tmp/mysql/datadir/" ]];then
  rm -rf /tmp/mysql/datadir/*
else
  mkdir -p /tmp/mysql/datadir/
fi

if [[ -d "/tmp/mysql/datadir/" ]];then
    docker-compose -f docker-compose.yml up  -d
    zim_container_ip=`docker ps | grep liguobin/zim | awk '{print $1}' | xargs docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}'`
    echo "zim server container ip: $zim_container_ip"
else
  echo "Not found a folder named /tmp/mysql/datadir/ for docker mysql"
  exit -1
fi

# mac osx visit docker container network? see https://www.haoyizebo.com/posts/fd0b9bd8/  and close your VPN