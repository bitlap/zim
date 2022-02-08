package org.bitlap.zim.server.util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time.format.DateTimeFormatter
import java.time.{ ZoneId, ZonedDateTime }
import java.util.Locale

/**
 * @author 梦境迷离
 * @since 2022/2/8
 * @version 1.0
 */
class DateUtilSpec extends AnyFlatSpec with Matchers {

  "getDateString" should "ok" in {
    val fromPattern: DateTimeFormatter = DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm:ss")
      .withLocale(Locale.CHINA)
      .withZone(ZoneId.of("UTC+8"))
    val dateString = DateUtil.getDateString(ZonedDateTime.parse("2020-02-21 00:00:00", fromPattern))
    dateString shouldBe "2020-02-21"
  }
}
