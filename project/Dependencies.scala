import sbt._

/**
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
object Dependencies {

  object Version {
    val zio = "1.0.13"
    val `zio-logging` = "0.5.14"
    val tapir = "0.19.4"
    val `akka-http` = "10.2.9"
    val akka = "2.6.19"
    val circe = "0.14.1"
    val scalikejdbc = "3.5.0"
    val logback = "1.2.11"
    val config = "1.4.1"
    val mysql = "8.0.28"
    val `zio-interop-reactiveStreams` = "1.3.9"
    val `simple-java-mail` = "7.1.1"
    val h2 = "2.1.210"
    val scalaTest = "3.2.11"
    val `zio-actors` = "0.0.9"
    val refined = "0.9.28"
    val `zio-schema` = "0.1.9"
    val `akka-http-session` = "0.6.1"
    val `smt-cacheable` = "0.4.2"
  }

  lazy val redis = "dev.zio" %% "zio-redis" % "0.0.0+381-86c20614-SNAPSHOT" // 实验性质的
  lazy val config = "com.typesafe" % "config" % Version.config
  lazy val `schema-derivation` = "dev.zio" %% "zio-schema-derivation" % Version.`zio-schema`
  lazy val zio = "dev.zio" %% "zio" % Version.zio
  lazy val `zio-interop-reactivestreams` =
    "dev.zio" %% "zio-interop-reactivestreams" % Version.`zio-interop-reactiveStreams`
  lazy val `akka-actor` = "com.typesafe.akka" %% "akka-actor-typed" % Version.akka

  lazy val `zio-actors` = Seq(
    "dev.zio" %% "zio-actors-akka-interop" % Version.`zio-actors`,
    "dev.zio" %% "zio-actors" % Version.`zio-actors`
  )
  lazy val `zio-schema` = Seq(
    "dev.zio" %% "zio-schema" % Version.`zio-schema`,
    "dev.zio" %% "zio-schema-protobuf" % Version.`zio-schema`
  )

  lazy val smtDeps = Seq(
    "org.bitlap" %% "smt-cacheable-caffeine" % Version.`smt-cacheable`
  )

  lazy val zioDeps = Seq(
    zio,
    `zio-interop-reactivestreams`,
    "dev.zio" %% "zio-logging" % Version.`zio-logging`,
    "dev.zio" %% "zio-test" % Version.zio % Test,
    "dev.zio" %% "zio-test-sbt" % Version.zio % Test,
    "dev.zio" %% "zio-crypto" % "0.0.0+92-5672c642-SNAPSHOT", // 实验性质的
    redis
  )

  lazy val tapirDeps = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core" % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % Version.tapir
  )

  lazy val `tapir-async-doc` = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-asyncapi-docs" % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-asyncapi-circe-yaml" % Version.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-asyncapi-circe" % Version.tapir
  )

  lazy val akkaDeps = Seq(
    `akka-actor`,
    "com.typesafe.akka" %% "akka-http" % Version.`akka-http`,
    "com.typesafe.akka" %% "akka-stream" % Version.akka,
    "com.typesafe.akka" %% "akka-slf4j" % Version.akka,
    "com.typesafe.akka" %% "akka-stream-testkit" % Version.akka % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % Version.`akka-http` % Test
  )

  lazy val circeDeps = Seq(
    "io.circe" %% "circe-generic" % Version.circe,
    "io.circe" %% "circe-generic-extras" % Version.circe,
    "io.circe" %% "circe-parser" % Version.circe
  )

  lazy val otherDeps = Seq(
    "org.scalikejdbc" %% "scalikejdbc-streams" % Version.scalikejdbc,
    config,
    "ch.qos.logback" % "logback-classic" % Version.logback,
    "mysql" % "mysql-connector-java" % Version.mysql,
    "org.simplejavamail" % "simple-java-mail" % Version.`simple-java-mail`,
    "com.h2database" % "h2" % Version.h2 % Test,
    "org.scalatest" %% "scalatest" % Version.scalaTest % Test
  )

  lazy val serverDeps: Seq[ModuleID] =
    domainDeps ++ akkaDeps ++ otherDeps ++ zioDeps ++
      tapirDeps ++ `zio-actors` ++ `tapir-async-doc` ++ smtDeps

  lazy val tapirApiDeps: Seq[ModuleID] = Seq(zio, `zio-interop-reactivestreams`) ++ akkaDeps ++ tapirDeps ++ domainDeps

  // 基础依赖 domain使用
  lazy val domainDeps: Seq[ModuleID] = Seq(
    "org.scalikejdbc" %% "scalikejdbc" % Version.scalikejdbc % Compile,
    "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % Version.scalikejdbc % Compile,
    "dev.zio" %% "zio-streams" % Version.zio % Compile,
    "eu.timepit" %% "refined" % Version.refined,
    `schema-derivation`,
    `akka-actor`
  ) ++ circeDeps ++ `zio-schema`

  lazy val cacheDeps = Seq(config, redis, zio) ++ `zio-schema`
}
