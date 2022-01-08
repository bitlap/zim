package org.bitlap.zim.repository

import org.bitlap.zim.domain.model.AddMessage
import zio._
import zio.stream.ZStream

private final class TangibleAddMessageRepository(databaseName: String) extends AddMessageRepository[AddMessage] {

  private implicit lazy val dbName: String = databaseName

  override def countUnHandMessage(uid: Int, agree: Int): stream.Stream[Throwable, Int] =
    _countUnHandMessage(uid, agree).toStreamOperation

  override def findAddInfo(uid: Int): stream.Stream[Throwable, AddMessage] =
    _findAddInfo(uid).toStreamOperation

  override def updateAddMessage(addMessage: AddMessage): stream.Stream[Throwable, Int] =
    _updateAddMessage(AddMessage.table, addMessage).toUpdateOperation

  override def saveAddMessage(addMessage: AddMessage): stream.Stream[Throwable, Int] =
    _saveAddMessage(AddMessage.table, addMessage).toUpdateOperation

  override def findById(id: Long): stream.Stream[Throwable, AddMessage] = ???

  override def findAll(): stream.Stream[Throwable, AddMessage] = ???
}

object TangibleAddMessageRepository {

  def apply(databaseName: String): AddMessageRepository[AddMessage] =
    new TangibleAddMessageRepository(databaseName)

  type ZAddMessageRepository = Has[AddMessageRepository[AddMessage]]

  def countUnHandMessage(uid: Int, agree: Int): ZStream[ZAddMessageRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.countUnHandMessage(uid, agree))

  def findAddInfo(uid: Int): ZStream[ZAddMessageRepository, Throwable, AddMessage] =
    stream.ZStream.accessStream(_.get.findAddInfo(uid))

  def updateAddMessage(addMessage: AddMessage): ZStream[ZAddMessageRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.updateAddMessage(addMessage))

  def saveAddMessage(addMessage: AddMessage): ZStream[ZAddMessageRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.saveAddMessage(addMessage))

  val live: ZLayer[Has[String], Nothing, ZAddMessageRepository] =
    ZLayer.fromService[String, AddMessageRepository[AddMessage]](TangibleAddMessageRepository(_))

  def make(databaseName: String): ULayer[ZAddMessageRepository] =
    ZLayer.succeed(databaseName) >>> live
}
