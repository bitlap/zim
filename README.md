<img align="right" width="20%" height="10%" src="./qq_group.JPG" alt="https://dreamylost.cn">

# zim
[![Build](https://github.com/bitlap/zim/actions/workflows/ScalaCI.yml/badge.svg?branch=master)](https://github.com/bitlap/zim/actions/workflows/ScalaCI.yml)

基于scala、zio、tapir、akka-http、circe、scalikejdbc、redis实现的纯异步、函数式、流式API的LayIM。

> 感兴趣的可关注一下，也可以一起开发。本项目旨在学习。  交流群 =====>

## 开发环境准备

* 执行 `./prepare.sh` 脚本, 然后修改 `src/main/resources/application.conf` 中相关数据库的信息
* 在本地MySQL中创建数据库`zim`，注意驱动的版本
* 使用`resources/sql/schema.sql`初始化表结构
* 使用`resources/sql/data.sql`初始化数据（可选）
* 启动Main方法`ZimServer.scala#run`

## 上手接口

- API文档： `http://localhost:9000/api/v1.0/docs`
- 心跳：`http://localhost:9000/api/v1.0/health`

## 技术栈

- zio
- tapir
- akka
- scalikejdbc
- circe
- config

## 项目结构

```
zim-master
├─ .github
│    └─ workflows   
│           └─ ScalaCI.yml              -- GitHub action 配置
│           └─ auto-approve.yml
│           └─ autoupdate.yml             
├─ .gitignore
├─ .jvmopts
├─ prepare.sh                           -- 辅助脚本
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
              │    ├─ application.conf.example                      -- 数据库和服务配置模板
              │    └─ logback.xml                                   -- 日志配置
              └─ scala
                     └─ org
                            └─ bitlap
                                   └─ zim
                                          ├─ ZimServer.scala        -- 程序入口，项目启动的main方法
                                          ├─ api                    -- 基于tapir的API（akka http实现）和Endpoint定义
                                          ├─ application            -- zio项目模块管理，聚合了service
                                          ├─ configuration          -- zio集成各框架的项目配置
                                          ├─ domain                 -- 领域对象或其他简单的样例类
                                          └─ repository             -- scalikejdbc的dao层实现（scalikejdbc stream和zio stream实现）
                                          └─ util                   -- 工具类，经过zio包装
                                          
```

## 项目组织结构

> 粗略图

![](./zim.jpeg)