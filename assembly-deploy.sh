#!/bin/bash

sbt clean

sbt assembly

version=$1

scp ~/Projects/zim/modules/zim-server/target/scala-2.13/zim-server-assembly-$version.jar root@your_ip:/home/zim/zim-server-assembly-$version.jar

# FIXME stop start
ssh root@your_ip "lsof -i:8989 | awk 'NR==2{print $2}' | xargs kill -9;"
ssh root@your_ip "cd /home/zim; nohup java -jar -Xms512M zim-server-assembly-$version.jar > zim.log 2>&1 &"
