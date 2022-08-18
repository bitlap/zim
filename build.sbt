import de.heikoseeberger.sbtheader.HeaderPlugin

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

ThisBuild / resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("public"),
  Resolver.sonatypeRepo("snapshots"),
  "New snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots/"
)

// sbt-assembly, not support build docker image by sbt task, so we use sbt-native-packager
//lazy val assemblySettings = Seq(
//  ThisBuild / assemblyMergeStrategy := {
//    case "application.conf"                              => MergeStrategy.concat
//    case x if x.endsWith(".txt") || x.endsWith(".proto") => MergeStrategy.first
//    case x if x.endsWith("module-info.class")            => MergeStrategy.first
//    case x if x.endsWith(".properties")                  => MergeStrategy.deduplicate
//    case x =>
//      val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
//      oldStrategy(x)
//  }
//)

lazy val commonConfiguration: Project => Project =
  _.settings(Information.value)
    .settings(ProjectSetting.value)
    .settings(ProjectSetting.noPublish)
    .settings(commands ++= Commands.value)
//    .settings(assemblySettings)
    .settings(
      semanticdbEnabled := true, // enable SemanticDB
      semanticdbVersion := scalafixSemanticdb.revision,
      scalafixOnCompile := true
    )

lazy val zim = (project in file("."))
  .settings(name := "zim")
  .aggregate(
    `zim-server`,
    `zim-domain`,
    `zim-cache-api`,
    `zim-api`,
    `zim-auth`,
    `zim-infra`,
    `zim-cache-redis4cats`,
    `zim-cache-redis4zio`
  )
  .configure(commonConfiguration)

lazy val `zim-server` = (project in file("modules/zim-server"))
  .settings(
    libraryDependencies ++= Dependencies.serverDeps,
    Compile / scalacOptions ++= List("-Ymacro-annotations"),
    Docker / packageName := "liguobin/zim",
    Docker / version     := version.value,
    dockerBaseImage      := "openjdk",
    dockerExposedVolumes ++= Seq("/opt/docker"),
    dockerExposedPorts  := Seq(9000),
    Compile / mainClass := Some("org.bitlap.zim.server.ZimServer"),
    dockerEntrypoint := Seq(
      "/opt/docker/bin/zim-server",
      "--privileged=true"
    )
  )
//  .settings(assembly / mainClass := Some("org.bitlap.zim.server.ZimServer"))
  .configure(commonConfiguration)
  .enablePlugins(ScalafmtPlugin, HeaderPlugin, JavaServerAppPackaging, DockerPlugin)
  .dependsOn(`zim-api`, `zim-auth`, `zim-infra`, `zim-cache-redis4cats`, `zim-cache-redis4zio`)

lazy val `zim-infra` = (project in file("modules/zim-infra"))
  .settings(
    libraryDependencies ++= Dependencies.infrastructureDeps
  )
  .configure(commonConfiguration)
  .enablePlugins(ScalafmtPlugin, HeaderPlugin)
  .dependsOn(`zim-domain`)

lazy val `zim-domain` = (project in file("modules/zim-domain"))
  .settings(BuildInfoSettings.value)
  .settings(libraryDependencies ++= Dependencies.domainDeps)
  .configure(commonConfiguration)
  .enablePlugins(GitVersioning, BuildInfoPlugin, ScalafmtPlugin, HeaderPlugin)

lazy val `zim-cache-api` = (project in file("modules/zim-cache-api"))
  .settings(libraryDependencies ++= Dependencies.cacheRedis4zioDeps)
  .configure(commonConfiguration)
  .enablePlugins(ScalafmtPlugin, HeaderPlugin)

lazy val `zim-api` = (project in file("modules/zim-api"))
  .settings(libraryDependencies ++= Dependencies.tapirApiDeps)
  .configure(commonConfiguration)
  .enablePlugins(ScalafmtPlugin, HeaderPlugin)
  .dependsOn(`zim-domain`)

lazy val `zim-auth` = (project in file("modules/zim-auth"))
  .settings(libraryDependencies ++= Dependencies.domainDeps)
  .configure(commonConfiguration)
  .enablePlugins(ScalafmtPlugin, HeaderPlugin)
  .dependsOn(`zim-domain`)

lazy val `zim-cache-redis4cats` = (project in file("modules/zim-cache-redis4cats"))
  .settings(libraryDependencies ++= Dependencies.cacheRedis4catsDeps)
  .configure(commonConfiguration)
  .enablePlugins(ScalafmtPlugin, HeaderPlugin)
  .dependsOn(`zim-cache-api`)

lazy val `zim-cache-redis4zio` = (project in file("modules/zim-cache-redis4zio"))
  .settings(libraryDependencies ++= Dependencies.domainDeps)
  .configure(commonConfiguration)
  .enablePlugins(ScalafmtPlugin, HeaderPlugin)
  .dependsOn(`zim-cache-api`)
