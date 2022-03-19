/*
 * Copyright 2022 bitlap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bitlap.zim.server.repository
import org.bitlap.zim.domain.model.AddMessage
import org.bitlap.zim.domain.repository.AddMessageRepository
import scalikejdbc._
import zio._
import zio.stream.ZStream

import scala.language.{ implicitConversions, postfixOps }

private final class TangibleAddMessageRepository(databaseName: String)
    extends TangibleBaseRepository(AddMessage)
    with AddMessageRepository {

  override implicit val dbName: String = databaseName
  override implicit val sp: QuerySQLSyntaxProvider[SQLSyntaxSupport[AddMessage], AddMessage] = AddMessage.syntax("am")

  override def countUnHandMessage(uid: Int, agree: Option[Int]): stream.Stream[Throwable, Int] =
    this.count("to_uid" === uid, "agree" === agree)

  override def findAddInfo(uid: Int): stream.Stream[Throwable, AddMessage] =
    _findAddInfo(uid).toStreamOperation

  override def updateAgree(id: Int, agree: Int): stream.Stream[Throwable, Int] =
    _updateAgree(id, agree).toUpdateOperation

  override def saveAddMessage(addMessage: AddMessage): stream.Stream[Throwable, Int] =
    _saveAddMessage(addMessage).toUpdateOperation
}

object TangibleAddMessageRepository {

  def apply(databaseName: String): AddMessageRepository =
    new TangibleAddMessageRepository(databaseName)

  type ZAddMessageRepository = Has[AddMessageRepository]

  def findById(id: Int): stream.ZStream[ZAddMessageRepository, Throwable, AddMessage] =
    stream.ZStream.accessStream(_.get.findById(id))

  def countUnHandMessage(uid: Int, agree: Option[Int]): ZStream[ZAddMessageRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.countUnHandMessage(uid, agree))

  def findAddInfo(uid: Int): ZStream[ZAddMessageRepository, Throwable, AddMessage] =
    stream.ZStream.accessStream(_.get.findAddInfo(uid))

  def updateAgree(id: Int, agree: Int): ZStream[ZAddMessageRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.updateAgree(id, agree))

  def saveAddMessage(addMessage: AddMessage): ZStream[ZAddMessageRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.saveAddMessage(addMessage))

  val live: ZLayer[Has[String], Nothing, ZAddMessageRepository] =
    ZLayer.fromService[String, AddMessageRepository](TangibleAddMessageRepository(_))

  def make(databaseName: String): ULayer[ZAddMessageRepository] =
    ZLayer.succeed(databaseName) >>> live
}
