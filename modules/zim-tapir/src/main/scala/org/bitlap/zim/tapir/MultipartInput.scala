package org.bitlap.zim.tapir

import sttp.model.Part
import sttp.tapir.TapirFile

/**
 * 文件上传
 *
 * @author 梦境迷离
 * @since 2022/2/3
 * @version 1.0
 */
case class MultipartInput(file: Part[TapirFile]) extends Serializable {

  def getFileName: String = file.fileName.getOrElse(file.name)

}
