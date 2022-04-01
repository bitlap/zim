# zim

[![Build](https://github.com/bitlap/zim/actions/workflows/ScalaCI.yml/badge.svg?branch=master)](https://github.com/bitlap/zim/actions/workflows/ScalaCI.yml)
[![codecov](https://codecov.io/gh/bitlap/zim/branch/master/graph/badge.svg?token=V95ZMWUUCE)](https://codecov.io/gh/bitlap/zim)

[在线预览地址](http://im.dreamylost.cn:8989)

## 模块

- `zim-auth` zim 的登录鉴权，目前由 cookie 实现并对外提供“鉴权缓存”函数，具体实现由`zim-server`完成。
- `zim-cache` zim 的缓存，目前由 zio-redis 实现。
- `zim-domain` 所有领域对象，包括数据库、http、websocket 等，还包括 circe 和 scalikejdbc 所需的隐式对象。
- `zim-server` zim 服务端的主要实现，包括 zio 依赖管理、领域对象的 crud 实现、基于 akka-http 的 api 实现、基于 tapir 的 api 具体实现。
- `zim-tapir` zim api 的端点描述定义，具体实现由`zim-server`完成。

## 环境

- scala 2.12/2.13
- java 8/11
- redis 4/5/6
- mysql 8

## 技术栈

- 开发语言：scala2
- 平台：jvm
- 前端：layim 3.0
- 主体框架：zio 1.x
- API server：akka-http
- API 文档化工具：tapir
- 数据库：redis、mysql
- 数据操作：scalikejdbc-streams
- 内存缓存：smt-cacheable-caffeine
- 定时任务：zio-actors
- 序列化：circe
- 加密工具 zio-crypto
- 日志：zio-logging
- 细化类型：refined
- WebSocket：akka-http、akka-actor-typed
- 邮件：simple-java-mail
- 配置：config
- 构建工具：sbt

## 详细介绍和博客

前往 >>> [bitlap官网](https://bitlap.org/zh-CN/lab/zim)

前往 >>>  [csdn 博客](https://blog.csdn.net/qq_34446485/category_11720549.html?spm=1001.2014.3001.5482)

- [x] 如何快速开始
- [x] zio 基本介绍
- [x] zio1.x 模块模式之 1.0
- [x] zio1.x 模块模式之 2.0
- [x] zio-streams、与 scalikejdbc-streams 的集成
- [x] zio 中的依赖注入
- [x] zio-streams 与 akka-stream 的集成
- [ ] zio-actors 与 akka-actor-typed 通信的集成
- [ ] zio 如何构建一个可重用模块
- [ ] zio 应用如何测试
- [ ] zio-schema 应用
- [ ] 如何编写 tapir 应用
- [ ] 如何编写 akka-http 接口
