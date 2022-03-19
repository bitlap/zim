import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport.headerCreateAll
import sbt.Keys._
import sbt.librarymanagement.License
import sbt.{ url, Compile, Def, Developer, ScmInfo }

/**
 * @author 梦境迷离
 * @since 2022/1/15
 * @version 1.0
 */
object Information {

  val value: Seq[Def.Setting[_]] = Seq(
    Compile / compile := (Compile / compile).dependsOn(Compile / headerCreateAll).value,
    organization := "org.bitlap",
    organizationName := "bitlap",
    startYear := Some(2021),
    description := "zim is a functional-style, asynchronous and streaming IM based on scala and zio",
    homepage := Some(url(s"https://github.com/bitlap/zim")),
    licenses += License.Apache2,
    developers := List(
      Developer(
        "jxnu-liguobin",
        "梦境迷离",
        "dreamylost@outlook.com",
        url("https://github.com/jxnu-liguobin")
      )
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/bitlap/zim"),
        "scm:git@github.com:bitlap/zim.git"
      )
    )
  )

}
