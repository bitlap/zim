package org.bitlap.zim.server.repository

import org.bitlap.zim.domain.model.Receive
import org.bitlap.zim.domain.repository.ReceiveRepository
import zio._
import zio.stream.ZStream

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
    _findHistoryMessage(uid, mid, typ).toStreamOperation

  override def countHistoryMessage(
    uid: Option[Int],
    mid: Option[Int],
    typ: Option[String]
  ): stream.Stream[Throwable, Int] =
    _countHistoryMessage(uid, mid, typ).toStreamOperation

  override def readMessage(mine: Int, to: Int, typ: String): stream.Stream[Throwable, Int] =
    _readMessage(Receive.table, mine, to, typ).toUpdateOperation

  override def findById(id: Long): stream.Stream[Throwable, Receive] =
    queryFindReceiveById(Receive.table, id).toSQLOperation
}

object TangibleReceiveRepository {

  def apply(databaseName: String): ReceiveRepository[Receive] =
    new TangibleReceiveRepository(databaseName)

  type ZReceiveRepository = Has[ReceiveRepository[Receive]]

  def saveMessage(receive: Receive): ZStream[ZReceiveRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.saveMessage(receive))

  def findOffLineMessage(uid: Int, status: Int): ZStream[ZReceiveRepository, Throwable, Receive] =
    stream.ZStream.accessStream(_.get.findOffLineMessage(uid, status))

  def findHistoryMessage(
    uid: Option[Int],
    mid: Option[Int],
    typ: Option[String]
  ): ZStream[ZReceiveRepository, Throwable, Receive] =
    stream.ZStream.accessStream(_.get.findHistoryMessage(uid, mid, typ))

  def countHistoryMessage(
    uid: Option[Int],
    mid: Option[Int],
    typ: Option[String]
  ): ZStream[ZReceiveRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.countHistoryMessage(uid, mid, typ))

  def readMessage(mine: Int, to: Int, typ: String): ZStream[ZReceiveRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.readMessage(mine, to, typ))

  def findById(id: Int): stream.ZStream[ZReceiveRepository, Throwable, Receive] =
    stream.ZStream.accessStream(_.get.findById(id))

  val live: ZLayer[Has[String], Nothing, ZReceiveRepository] =
    ZLayer.fromService[String, ReceiveRepository[Receive]](TangibleReceiveRepository(_))

  def make(databaseName: String): ULayer[ZReceiveRepository] =
    ZLayer.succeed(databaseName) >>> live

}
