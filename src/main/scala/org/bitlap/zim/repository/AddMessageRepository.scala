package org.bitlap.zim.repository

import zio.stream

trait AddMessageRepository[T] extends BaseRepository[T] {
  def countUnHandMessage(uid: Option[Int], agree: Option[Int]): stream.Stream[Throwable, Int]

  def findAddInfo(uid: Int): stream.Stream[Throwable, T]

  def updateAddMessage(addMessage: T): stream.Stream[Throwable, Int]

  def saveAddMessage(addMessage: T): stream.Stream[Throwable, Int]
}
