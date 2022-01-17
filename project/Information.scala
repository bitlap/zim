import sbt.Keys._
import sbt.{ url, Def, Developer, ScmInfo }

/**
 * @author 梦境迷离
 * @since 2022/1/15
 * @version 1.0
 */
object Information {

  val value: Seq[Def.Setting[_]] = Seq(
    name := "zim",
    organization := "org.bitlap",
    description := "zim is a functional-style, asynchronous and streaming IM based on scala and zio",
    homepage := Some(url(s"https://github.com/bitlap/zim")),
    licenses := List("APACHE LICENSE 2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
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
