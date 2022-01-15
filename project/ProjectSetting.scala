import sbt.CrossVersion
import sbt.Keys.{ exportJars, version, _ }
import sbt.{ Def, Tests, _ }
/**
 * @author 梦境迷离
 * @version 1.0,2022/1/11
 */
object ProjectSetting {

  lazy val scala212 = "2.12.15"
  lazy val scala213 = "2.13.8"

  lazy val supportedScalaVersions = List(scala212,scala213)

  def extraOptions(scalaVersion: String, optimize: Boolean): List[String] =
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, 13)) =>
        /** List("-Wunused:imports") ++ * */
        optimizerOptions(optimize)
      case Some((2, 12)) =>
        List(
          "-opt-warnings",
          "-Ywarn-extra-implicit",
          "-Ywarn-unused:_,imports",
          "-Ywarn-unused:imports",
          "-Ypartial-unification",
          "-Yno-adapted-args",
          "-Ywarn-inaccessible",
          "-Ywarn-infer-any",
          "-Ywarn-nullary-override",
          "-Ywarn-nullary-unit",
          "-Xfuture",
          "-Xsource:2.13",
          "-Xmax-classfile-name",
          "242"
        ) ++ std2xOptions ++ optimizerOptions(optimize)
      case _ => Nil
    }

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
    scalaVersion := scala213,
    scalacOptions := (stdOptions ++ extraOptions(scalaVersion.value, !isSnapshot.value)),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"), TestFrameworks.ScalaTest),
    autoAPIMappings := true,
    version := (ThisBuild / version).value,
    Test / parallelExecution := false, //see https://www.scalatest.org/user_guide/async_testing
    Global / cancelable := true,
    // OneJar
    exportJars := true
  )

  val noPublish: Seq[Def.Setting[_]] = Seq(
    publish / skip := true
  )

}
