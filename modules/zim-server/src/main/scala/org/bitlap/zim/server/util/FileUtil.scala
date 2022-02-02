package org.bitlap.zim.server.util
import akka.actor.ActorSystem

import java.io.File
import java.net.URL
import java.nio.charset.Charset
import scala.io.Source

/**
 * @author 梦境迷离
 * @since 2022/1/23
 * @version 1.0
 */
object FileUtil {

  def readFile(file: URL): String = {
    val input = Source.fromFile(new File(file.getFile))
    val fileContent = new StringBuilder
    input.getLines().foreach(f => fileContent.append(new String(f.getBytes(), Charset.forName("utf8"))).append("\n"))
    fileContent.toString()
  }

  def getFileAndInjectData(file: String, source: String, target: String): String = {
    val f = classOf[ActorSystem].getClassLoader.getResource(file)
    FileUtil.readFile(f).replace(source, target)
  }

  def getFileAndInjectData(file: String, sourceTargets: (String, String)*): String = {
    val f = classOf[ActorSystem].getClassLoader.getResource(file)
    val fileContent = FileUtil.readFile(f)
    sourceTargets.foldLeft(fileContent)((e, op) => e.replace(op._1, op._2))
  }

}
