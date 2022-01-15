package org.bitlap.zim.util

import java.text.SimpleDateFormat
import java.util.Date

/**
 * 时间工具
 *
 * @since 2021年12月31日
 * @author 梦境迷离
 */
object DateUtil {

  private final val partternAll: String = "yyyy-MM-dd: HH:mm:ss"

  private final val partternPart: String = "yyyy-MM-dd"

  /**
   * 获取格式化后的当前时间yyyy-MM-dd
   */
  def getDateString: String = new SimpleDateFormat(partternPart).format(new Date)

  /**
   * 获取特定格式的当前时间
   */
  def getDateTimeString(parttern: String): String = new SimpleDateFormat(parttern).format(new Date)

  /**
   * 获取当前时间yyyy-MM-dd: HH:mm:ss
   */
  def getDateTimeString: String = new SimpleDateFormat(partternAll).format(new Date)

  /**
   * 获取特定格式的当前时间 yyyy-MM-dd
   */
  def getDate: Date = new SimpleDateFormat(partternPart).parse(getDateString)

  /**
   * 获取特定格式的当前时间 yyyy-MM-dd: HH:mm:ss
   */
  def getDateTime: Date = new SimpleDateFormat(partternAll).parse(getDateTimeString)

  /**
   * 获取当前时间Long类型
   */
  def getLongDateTime: Long = new Date().getTime

}
