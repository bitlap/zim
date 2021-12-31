import Dependencies._

resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("public"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.typesafeIvyRepo("releases")
)

lazy val root = (project in file("."))
  .settings(BuildInfoSettings.value)
  .settings(
    organization := "org.bitlap",
    name := "zim",
    version := "0.0.1",
    scalaVersion := "2.13.7",
    libraryDependencies ++= zioDeps ++ tapirDeps ++ commonDeps ++ akkaDeps ++ circeDeps,
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )
  .enablePlugins(GitVersioning, BuildInfoPlugin)

// Aliases
addCommandAlias("rel", "reload")
addCommandAlias("com", "all compile test:compile it:compile")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")

(Compile / compile) := ((Compile / compile) dependsOn scalafmtAll).value
