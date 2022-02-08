package org.bitlap.zim.server.util

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * 时间工具
 *
 * @since 2021年12月31日
 * @author 梦境迷离
 */
object DateUtil {

  final lazy val pattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  /**
   * 获取格式化后的当前时间yyyy-MM-dd
   * @param now
   */
  def getDateString(now: ZonedDateTime = ZonedDateTime.now()): String =
    now.format(pattern)

}
