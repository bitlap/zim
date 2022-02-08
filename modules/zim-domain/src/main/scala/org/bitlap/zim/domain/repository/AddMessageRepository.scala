package org.bitlap.zim.domain.repository
import org.bitlap.zim.domain.model.AddMessage
import zio.stream

/**
 * @author 梦境迷离
 * @since 2022/1/15
 * @version 1.0
 */
trait AddMessageRepository extends BaseRepository[AddMessage] {

  def countUnHandMessage(uid: Int, agree: Option[Int]): stream.Stream[Throwable, Int]

  def findAddInfo(uid: Int): stream.Stream[Throwable, AddMessage]

  def updateAgree(id: Int, agree: Int): stream.Stream[Throwable, Int]

  def saveAddMessage(addMessage: AddMessage): stream.Stream[Throwable, Int]
}
