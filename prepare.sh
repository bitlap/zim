#!/usr/bin/env bash

## 将 application.conf.example 拷贝一份为 application.conf
for i in `find . -type f -name 'application.conf.example'`
do
    cp -n $i `echo $i | sed -e 's/.example//'`
done
