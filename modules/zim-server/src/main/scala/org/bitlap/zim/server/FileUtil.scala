/*
 * Copyright 2022 bitlap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bitlap.zim.server

import org.bitlap.zim.domain.ZimError.BusinessException
import org.bitlap.zim.domain.{ SystemConstant, ZimError }
import org.bitlap.zim.infrastructure.util.{ DateUtil, UuidUtil }
import sttp.model.Part
import sttp.tapir.TapirFile
import zio.{ IO, ZIO }

import java.io._
import java.nio.charset.Charset
import scala.collection.mutable
import scala.io.Source
import scala.util.Using

/** @author
 *    梦境迷离
 *  @since 2022/1/23
 *  @version 1.0
 */
object FileUtil {

  def readFile(file: InputStream): String = {
    val input       = Source.fromInputStream(file)
    val fileContent = new mutable.StringBuilder
    input.getLines().foreach(f => fileContent.append(new String(f.getBytes(), Charset.forName("utf8"))).append("\n"))
    fileContent.toString()
  }

  def getFileAndInjectData(file: String, source: String, target: String): String =
    // 从jar中读取只能使用 getResourceAsStream
    FileUtil.readFile(this.getClass.getClassLoader.getResourceAsStream(file)).replace(source, target)

  def getFileAndInjectData(file: String, sourceTargets: (String, String)*): String = {
    val fileContent = FileUtil.readFile(this.getClass.getClassLoader.getResourceAsStream(file))
    sourceTargets.foldLeft(fileContent)((e, op) => e.replace(op._1, op._2))
  }

  /** 文件保存服务器
   *
   *  @param `type`
   *    文件类型/upload/image 或 /upload/file
   *  @param path
   *    文件绝对路径地址
   *  @param file
   *    二进制文件
   *  @return
   *    文件的相对路径地址
   */
  def upload(`type`: String, path: String, file: Part[TapirFile]): ZIO[Any, ZimError, String] = {
    val name   = file.fileName.getOrElse(file.name)
    val paths  = path + `type` + DateUtil.getDateString() + "/"
    val result = `type` + DateUtil.getDateString() + "/"
    if (SystemConstant.IMAGE_PATH.equals(`type`) || SystemConstant.GROUP_AVATAR_PATH.equals(`type`)) {
      UuidUtil.getUuid32.map(_ + name.substring(name.indexOf("."))).map { nn =>
        copyInputStreamToFile(new FileInputStream(file.body), new File("." + paths, nn))
        result + nn
      }
    } else if (SystemConstant.FILE_PATH.equals(`type`)) {
      // 如果是文件，则区分目录
      UuidUtil.getUuid32.map { pp =>
        copyInputStreamToFile(new FileInputStream(file.body), new File("." + paths + pp, name))
        result + pp + "/" + name
      }
    } else {
      ZIO.fail(BusinessException(msg = "文件上传失败"))
    }
  }

  /** 用户更新头像
   *
   *  @param realpath
   *    服务器绝对路径地址
   *  @param file
   *    文件
   *  @return
   *    相对路径
   */
  def upload(realpath: String, file: Part[TapirFile]): IO[Throwable, String] =
    for {
      prefix <- UuidUtil.getUuid32
      n    = file.fileName.getOrElse(file.name)
      name = prefix + n.substring(n.indexOf("."))
      _    = copyInputStreamToFile(new FileInputStream(file.body), new File("." + realpath, name))
    } yield realpath + name

  def copyInputStreamToFile(inputStream: InputStream, file: File): Unit = {
    if (file.exists) {
      if (file.isDirectory) throw new IOException(s"File $file exists but is a directory")
      if (!file.canWrite) throw new IOException(s"File $file cannot be written to")
    } else {
      val parent = file.getParentFile
      if (parent != null) if (!parent.mkdirs && !parent.isDirectory) {
        throw new IOException(s"Directory $parent could not be created")
      }
    }
    Using.resources(inputStream, new FileOutputStream(file)) { (in, out) =>
      val data = Iterator.continually(in.read()).takeWhile(_ != -1).map(_.toByte).toArray
      out.write(data)
    }
  }

}
