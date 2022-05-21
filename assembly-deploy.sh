#!/bin/bash

sbt assembly

version=$1

scp ~/Projects/zim/modules/zim-server/target/scala-2.13/zim-server-assembly-$version.jar root@your_ip:/home/zim/zim-server-assembly-$version.jar

ssh root@your_ip "ps -ef | grep -w java | grep -v grep | awk '{print $2}' | xargs kill -9; sleep 2; cd /home/zim; nohup java -jar -Xms512M zim-server-assembly-$version.jar > zim.log 2>&1 &"
