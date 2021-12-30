package org.bitlap.zim.repository

import zio.stream


/**
 * 消息的操作定义
 *
 * @author LittleTear
 * @since 2021/12/30
 * @version 1.0
 */
trait ReceiveRepository[T] extends BaseRepository[T] {

  def saveMessage(receive: T): stream.Stream[Throwable, Long]

  def findOffLineMessage(uid: Int, status: Int): stream.Stream[Throwable, T]

  def findHistoryMessage(uid: Option[Int], mid: Option[Int], typ: Option[String]): stream.Stream[Throwable, T]

  def countHistoryMessage(uid: Option[Int], mid: Option[Int], typ: Option[String]): stream.Stream[Throwable, Int]

  def readMessage(mine: Int, to: Int, typ: String): stream.Stream[Throwable, Int]


}
