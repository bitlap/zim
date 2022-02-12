package org.bitlap.zim.server.util

import java.time.{ ZoneId, ZonedDateTime }
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * @author 梦境迷离
 * @since 2022/2/12
 * @version 1.0
 */
object DateHelper {

  val fromPattern: DateTimeFormatter = DateTimeFormatter
    .ofPattern("yyyy-MM-dd HH:mm:ss")
    .withLocale(Locale.CHINA)
    .withZone(ZoneId.of("UTC+8"))

  def getConstantTime: ZonedDateTime =
    ZonedDateTime.parse("2022-02-11 00:00:00", fromPattern)

}
