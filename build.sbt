import Dependencies._

Global / onLoad := {
  val GREEN = "\u001b[32m"
  val RESET = "\u001b[0m"
  println(s"""$GREEN
             |$GREEN                                 ____
             |$GREEN                ,--,           ,'  , `.
             |$GREEN        ,----,,--.'|        ,-+-,.' _ |
             |$GREEN      .'   .`||  |,      ,-+-. ;   , ||
             |$GREEN   .'   .'  .'`--'_     ,--.'|'   |  ||
             |$GREEN ,---, '   ./ ,' ,'|   |   |  ,', |  |,
             |$GREEN ;   | .'  /  '  | |   |   | /  | |--'
             |$GREEN `---' /  ;--,|  | :   |   : |  | ,
             |$GREEN   /  /  / .`|'  : |__ |   : |  |/
             |$GREEN ./__;     .' |  | '.'||   | |`-'
             |$GREEN ;   |  .'    ;  :    ;|   ;/
             |$GREEN `---'        |  ,   / '---'
             |$GREEN               ---`-'
             |$RESET        v.${version.value}
             |""".stripMargin)
  (Global / onLoad).value
}

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
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"), TestFrameworks.ScalaTest),
    Test / parallelExecution  := false //see https://www.scalatest.org/user_guide/async_testing
  )
  .enablePlugins(GitVersioning, BuildInfoPlugin)

// Aliases
addCommandAlias("rel", "reload")
addCommandAlias("com", "all compile test:compile it:compile")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")

(Compile / compile) := ((Compile / compile) dependsOn scalafmtAll).value
