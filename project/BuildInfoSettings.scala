import com.typesafe.sbt.GitPlugin.autoImport.git
import sbt.Keys._
import sbt.{ Compile, Def, SettingKey }
import sbtbuildinfo.BuildInfoKeys.buildInfoKeys
import sbtbuildinfo.BuildInfoPlugin.autoImport._

object BuildInfoSettings {

  private val gitCommitString = SettingKey[String]("gitCommit")

  val value: Seq[Def.Setting[_]] = Seq(
    buildInfoObject := "ZimBuildInfo",
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, gitCommitString) ++ Seq[BuildInfoKey](Compile / libraryDependencies),
    buildInfoPackage := s"${organization.value}.zim",
    buildInfoOptions ++= Seq(BuildInfoOption.ToJson, BuildInfoOption.BuildTime),
    gitCommitString := git.gitHeadCommit.value.getOrElse("Not Set")
  )

}