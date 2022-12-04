#!/bin/bash


sbt clean

sbt universal:packageBin
# FIXME: To copy static file from prefix version.
version=$1
pre_version=$2

scp ~/Projects/zim/modules/zim-server/target/universal/zim-server-$version.zip root@your_ip:/home/zim/zim-server-$version.zip
# FIXME stop start
ssh root@your_ip "lsof -i:8989 | awk 'NR==2{print $2}' | xargs kill -9;"
ssh root@your_ip "cd /home/zim; unzip -o zim-server-$version.zip; nohup ./zim-server-$version/bin/zim-server -Xms512M > zim.log 2>&1 &"
# FIXME: Static files are in the service's current directory, and each deployment will use a new directory.
ssh root@your_ip "cp -r /home/zim/zim-server-$pre_version/bin/static/  /home/zim/zim-server-$version/bin/"