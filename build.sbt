import sbt.{ Def, Test }

lazy val zioDeps = Seq(
  "dev.zio" %% "zio" % Version.zio,
  "dev.zio" %% "zio-test" % Version.zio % "test",
  "dev.zio" %% "zio-test-sbt" % Version.zio % "test"
)

lazy val tapirDeps = Seq(
  "com.softwaremill.sttp.tapir" %% "tapir-core" % Version.tapir,
  "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % Version.tapir exclude("com.typesafe.akka", "akka-stream_2.13"),
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % Version.tapir,
)

lazy val commonDeps = Seq(
  "com.typesafe.akka" %% "akka-actor" % Version.`akka-actor`,
  "org.scalikejdbc" %% "scalikejdbc" % Version.scalikejdbc,
  "io.circe" %% "circe-generic" % Version.circe,
  "com.typesafe" % "config" % Version.config,
  "ch.qos.logback" % "logback-classic" % Version.logback
)

lazy val root = (project in file("."))
  .settings(
    organization := "org.bitlap",
    name := "zim",
    version := "0.0.1",
    scalaVersion := "2.13.6",
    libraryDependencies ++= zioDeps ++ tapirDeps ++ commonDeps,
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )

// Aliases
addCommandAlias("rel", "reload")
addCommandAlias("com", "all compile test:compile it:compile")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")
