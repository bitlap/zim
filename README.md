# zim
[![Build](https://github.com/bitlap/zim/actions/workflows/ScalaCI.yml/badge.svg?branch=master)](https://github.com/bitlap/zim/actions/workflows/ScalaCI.yml)

基于scala、zio、tapir、akka-http、scalikejdbc、redis实现的纯异步、函数式、流式API的LayIM。

> 感兴趣的关注一下，也可以一起开发。本项目旨在学习。

## 默认API 

JSON文档： `http://localhost:9000/api/v1.0/docs`
心跳：`http://localhost:9000/api/v1.0/health`

## 项目结构

```
zim-master
├─ .github
│    └─ workflows   
│           └─ ScalaCI.yml              -- GitHub action 配置
├─ .gitignore
├─ .jvmopts
├─ .scalafmt.conf                       -- scalafmt格式化插件配置
├─ LICENSE
├─ README.md
├─ build.sbt                            -- sbt项目基础构建配置
├─ project
│    ├─ BuildInfoSettings.scala         -- 用于编译期间生成项目全局信息的配置
│    ├─ Dependencies.scala              -- 项目依赖和版本号配置
│    ├─ build.properties                -- sbt版本
│    └─ plugins.sbt                     -- 项目所依赖的插件配置
└─ src
       └─ main
              ├─ resources
              │    ├─ application.conf  -- 数据库和服务配置
              │    └─ logback.xml       -- 日志配置
              └─ scala
                     └─ org
                            └─ bitlap
                                   └─ zim
                                          ├─ ZimServer.scala        -- 程序入口，项目启动的main方法
                                          ├─ api                    -- 基于tapir的API（基于akka http实现）和Endpoint定义
                                          ├─ application            -- zio项目模块管理，聚合了service
                                          ├─ configuration          -- zio集成各框架的项目配置
                                          ├─ domain                 -- 领域对象或其他简单的样例类
                                          └─ repository             -- scalikejdbc的dao层实现（基于scalikejdbc stream和zio stream实现）
```

## 技术栈

- zio
  - zio-streams
  - zio-logging
  - zio-interop-reactivestreams
  - zio-test
- tapir
  - tapir-core
  - tapir-akka-http-server
  - tapir-json-circe
  - tapir-openapi-docs
  - tapir-openapi-circe-yaml
- akka
  - akka-http
  - akka-steam
  - akka-actor
  - akka-slf4j
- jdbc
  - scalikejdbc 
  - scalikejdbc-streams
- json 
  - circe-generic
  - circe-generic-extras
  - circe-parser
- config
  - typesafe config 

## TODO

搬砖中。。。