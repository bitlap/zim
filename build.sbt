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

ThisBuild / resolvers  ++= Seq(
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("public"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.typesafeIvyRepo("releases")
)

lazy val configurationPublish: Project => Project =
  _.settings(Information.value)
    .settings(ProjectSetting.value)
    .settings(crossScalaVersions := ProjectSetting.supportedScalaVersions)

lazy val configurationNoPublish: Project => Project =
  _.settings(Information.value)
    .settings(ProjectSetting.value)
    .settings(ProjectSetting.noPublish)
    .settings(commands ++= Commands.value)

lazy val zim = (project in file("."))
  .aggregate(`zim-server`, `zim-domain`)
  .configure(configurationNoPublish)

lazy val `zim-server` = (project in file("modules/zim-server"))
  .settings(BuildInfoSettings.value)
  .settings(libraryDependencies ++= Dependencies.serverDeps)
  .configure(configurationNoPublish)
  .enablePlugins(GitVersioning, BuildInfoPlugin, ScalafmtPlugin)
  .dependsOn(`zim-domain`)

lazy val `zim-domain` = (project in file("modules/zim-domain"))
  .settings(libraryDependencies ++= Dependencies.domainDeps)
  .configure(configurationPublish)
  .enablePlugins(ScalafmtPlugin)