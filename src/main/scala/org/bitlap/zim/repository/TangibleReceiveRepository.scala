package org.bitlap.zim.repository

import org.bitlap.zim.domain.model.Receive
import zio._

/**
 * 消息的操作实现
 *
 * @author LittleTear
 * @since 2021/12/30
 * @version 1.0
 */
private final class TangibleReceiveRepository(databaseName: String) extends ReceiveRepository[Receive] {

  private implicit lazy val dbName: String = databaseName

  override def saveMessage(receive: Receive): stream.Stream[Throwable, Int] =
    _saveMessage(Receive.table, receive).toUpdateOperation

  override def findOffLineMessage(uid: Int, status: Int): stream.Stream[Throwable, Receive] =
    _findOffLineMessage(Receive.table, uid, status).toStreamOperation

  override def findHistoryMessage(
    uid: Option[Int],
    mid: Option[Int],
    typ: Option[String]
  ): stream.Stream[Throwable, Receive] =
    _findHistoryMessage(Receive.table, uid, mid, typ).toStreamOperation

  override def countHistoryMessage(
    uid: Option[Int],
    mid: Option[Int],
    typ: Option[String]
  ): stream.Stream[Throwable, Int] =
    _countHistoryMessage(Receive.table, uid, mid, typ).toStreamOperation

  override def readMessage(mine: Int, to: Int, typ: String): stream.Stream[Throwable, Int] =
    _readMessage(Receive.table, mine, to, typ).toUpdateOperation

  override def findById(id: Long): stream.Stream[Throwable, Receive] = ???

  override def findAll(): stream.Stream[Throwable, Receive] = ???
}

object TangibleReceiveRepository {

  def apply(databaseName: String): ReceiveRepository[Receive] =
    new TangibleReceiveRepository(databaseName)

  type ZReceiveRepository = Has[ReceiveRepository[Receive]]

  val live: ZLayer[Has[String], Nothing, ZReceiveRepository] =
    ZLayer.fromService[String, ReceiveRepository[Receive]](TangibleReceiveRepository(_))

  def make(databaseName: String): ULayer[ZReceiveRepository] =
    ZLayer.succeed(databaseName) >>> live

}
