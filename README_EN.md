# zim
[![Build](https://github.com/bitlap/zim/actions/workflows/ScalaCI.yml/badge.svg?branch=master)](https://github.com/bitlap/zim/actions/workflows/ScalaCI.yml)
[![codecov](https://codecov.io/gh/bitlap/zim/branch/master/graph/badge.svg?token=V95ZMWUUCE)](https://codecov.io/gh/bitlap/zim)


`zim` is a functional-style, asynchronous and streaming IM based on scala, zio, tapir, akka-http, circe, scalikejdbc and redis.

> If you are interested, you can pay attention to it and develop it together.

## Environment

* exec `./prepare.sh`, and modify `src/main/resources/application.conf`, replace database by yours.
* create a database named `zim`. You should pay attention to the driver version.
* use `resources/sql/schema.sql` to init db schema.
* use`resources/sql/data.sql` to init db data.(Optional)
* run `ZimServer.scala#run`.

## Where to start

- swagger-ui doc API: `http://localhost:9000/api/v1.0/docs`
- heartbeat API: `http://localhost:9000/api/v1.0/health`

## Technology stack

- Language：Scala 2.13.7
- Platform：Jvm (Jdk8、Jdk11)
- Frontend：LayIm 3.0
- Core：zio 1.x
- API：akka-http (classic)
- API Doc：tapir
- Database：redis、mysql
- Database jdbc：scalikejdbc-stream
- Serialize：circe
- WebSocket：akka-http (classic)
- Mail：simple-java-mail
- Conf：config
- Build：sbt

## Project structure

```
zim-master
├─ .github
│    └─ workflows   
│           └─ ScalaCI.yml                      -- GitHub action
│           └─ auto-approve.yml
│           └─ autoupdate.yml             
├─ .gitignore
├─ .jvmopts
├─ .mergify.yml                                
├─ prepare.sh                                  
├─ .scalafmt.conf                              
├─ LICENSE
├─ README.md
├─ build.sbt                                   
├─ project
│    ├─ BuildInfoSettings.scala                 -- It is used to generate the configuration of project global information during compilation
│    ├─ Dependencies.scala                       
│    ├─ build.properties                        
│    └─ plugins.sbt                             
└─ src
       └─ main
          ├─ resources
          │    ├─ application.conf.example      -- Database and service configuration templates
          │    └─ logback.xml                   
          └─ scala
             └─ org
                └─ bitlap
                   └─ zim
                      ├─ ZimServer.scala        -- The entry point to the program
                      ├─ api                    -- API base on tapir
                      ├─ actor                  -- actor and ws protocol
                      ├─ application            -- Service
                      ├─ cache                  -- Cache, use zio-redis
                      ├─ configuration        
                      ├─ domain                 
                         └─ model               -- Table model
                      └─ repository             -- Scalikejdbc
                      └─ util                   
       └─ test
          ├─ resources
          │    ├─ application.conf                      
          │    └─ logback-test.xml                      
          └─ scala
             └─ org
                └─ bitlap
                   └─ zim
                      ├─ RandomData.scala                -- Code for creating user table data
                      ├─ BaseData.scala                  -- Common parent class for unit test
                      ├─ infrastructure.repository       -- Repository unit test
                                          
```

## Project organization structure

![](./zim.jpeg)
