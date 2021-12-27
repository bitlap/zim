import sbt._

/**
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
object Dependencies {

  object Version {
    val zio = "1.0.13"
    val `zio-logging` = "0.5.14"
    val tapir = "0.17.20"
    val `tapir-swagger` = "0.18.3"
    val `akka-http` = "10.1.10"
    val akka = "2.5.32"
    val circe = "0.14.1"
    val scalikejdbc = "3.5.0"
    val logback = "1.2.10"
    val config = "1.4.1"
    val mysql = "8.0.25"
    val `zio-interop-reactiveStreams` = "1.3.8"
  }

  lazy val zioDeps = Seq(
    "dev.zio" %% "zio" % Version.zio,
    "dev.zio" %% "zio-streams" % Version.zio,
    "dev.zio" %% "zio-interop-reactivestreams" % Version.`zio-interop-reactiveStreams`,
    "dev.zio" %% "zio-logging" % Version.`zio-logging`,
    "dev.zio" %% "zio-test" % Version.zio % Test,
    "dev.zio" %% "zio-test-sbt" % Version.zio % Test
  )

  lazy val tapirDeps = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core" % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % Version.tapir exclude ("com.typesafe.akka", "akka-stream_2.13"),
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % Version.tapir
  )

  lazy val commonDeps = Seq(
    "com.typesafe.akka" %% "akka-http" % Version.`akka-http`,
    "com.typesafe.akka" %% "akka-actor" % Version.akka,
    "com.typesafe.akka" %% "akka-stream" % Version.akka,
    "com.typesafe.akka" %% "akka-slf4j" % Version.akka,
    "org.scalikejdbc" %% "scalikejdbc" % Version.scalikejdbc,
    "org.scalikejdbc" %% "scalikejdbc-streams" % Version.scalikejdbc,
    "io.circe" %% "circe-generic" % Version.circe,
    "io.circe" %% "circe-generic-extras" % Version.circe,
    "io.circe" %% "circe-parser" % Version.circe,
    "com.typesafe" % "config" % Version.config,
    "ch.qos.logback" % "logback-classic" % Version.logback,
    "mysql" % "mysql-connector-java" % Version.mysql
  )
}
