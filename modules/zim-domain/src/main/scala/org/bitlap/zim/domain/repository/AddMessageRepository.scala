package org.bitlap.zim.domain.repository
import zio.stream

/**
 * @author 梦境迷离
 * @since 2022/1/15
 * @version 1.0
 */
trait AddMessageRepository[T] extends BaseRepository[T] {

  def countUnHandMessage(uid: Int, agree: Int): stream.Stream[Throwable, Int]

  def findAddInfo(uid: Int): stream.Stream[Throwable, T]

  def updateAddMessage(addMessage: T): stream.Stream[Throwable, Int]

  def saveAddMessage(addMessage: T): stream.Stream[Throwable, Int]
}
