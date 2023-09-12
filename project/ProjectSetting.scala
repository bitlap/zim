import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile

import ProjectSetting.optimizerOptions
import sbt.{CrossVersion, Def, _}
import sbt.Keys._

/** @author
 *    梦境迷离
 *  @version 1.0,2022/1/11
 */
object ProjectSetting {

  lazy val scala213 = "2.13.12"

  def extraOptions(optimize: Boolean): List[String] = List("-Wunused:imports") ++ optimizerOptions(optimize)

  def optimizerOptions(optimize: Boolean): List[String] =
    if (optimize) List("-opt:l:inline", "-opt-inline-from:zio.internal.**") else Nil

  lazy val stdOptions = List("-deprecation", "-encoding", "UTF-8", "-feature", "-unchecked", "-Xfatal-warnings")

  lazy val std2xOptions =
    List(
      "-language:higherKinds",
      "-language:existentials",
      "-explaintypes",
      "-Yrangepos",
      "-Xlint:_,-missing-interpolator,-type-parameter-shadow",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard"
    )

  val value: Seq[Def.Setting[_]] = Seq(
    scalaVersion             := scala213,
    scalacOptions            := (stdOptions ++ extraOptions(!isSnapshot.value)),
    testFrameworks           := Seq(new TestFramework("zio.test.sbt.ZTestFramework"), TestFrameworks.ScalaTest),
    autoAPIMappings          := true,
    version                  := (ThisBuild / version).value,
    Test / parallelExecution := false, // see https://www.scalatest.org/user_guide/async_testing
    Global / cancelable      := true,
    // OneJar
    exportJars        := true,
    scalafmtOnCompile := true
  )

  val noPublish: Seq[Def.Setting[_]] = Seq(
    publish / skip := true
  )

}
