/*
 * Copyright 2023 bitlap
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

package org.bitlap.zim.infrastructure.repository

import org.bitlap.zim.api.repository.AddMessageRepository
import org.bitlap.zim.domain.model.AddMessage

import scalikejdbc._
import zio._
import zio.stream.ZStream

private final class TangibleAddMessageRepository(databaseName: String)
    extends TangibleBaseRepository(AddMessage)
    with AddMessageRepository[RStream] {

  override implicit val dbName: String                                                       = databaseName
  override implicit val sp: QuerySQLSyntaxProvider[SQLSyntaxSupport[AddMessage], AddMessage] = AddMessage.syntax("am")

  override def countUnHandMessage(uid: Int, agree: Option[Int]): RStream[Int] =
    this.count("to_uid" === uid, "agree" === agree)

  override def findAddInfo(uid: Int): RStream[AddMessage] =
    _findAddInfo(uid).toStreamOperation

  override def updateAgree(id: Int, agree: Int): RStream[Int] =
    _updateAgree(id, agree).toUpdateOperation

  override def saveAddMessage(addMessage: AddMessage): RStream[Int] =
    _saveAddMessage(addMessage).toUpdateOperation
}

object TangibleAddMessageRepository {

  def apply(databaseName: String): AddMessageRepository[RStream] =
    new TangibleAddMessageRepository(databaseName)

  def findById(id: Int): stream.ZStream[AddMessageRepository[RStream], Throwable, AddMessage] =
    stream.ZStream.environmentWithStream(_.get.findById(id))

  def countUnHandMessage(uid: Int, agree: Option[Int]): ZStream[AddMessageRepository[RStream], Throwable, Int] =
    stream.ZStream.environmentWithStream(_.get.countUnHandMessage(uid, agree))

  def findAddInfo(uid: Int): ZStream[AddMessageRepository[RStream], Throwable, AddMessage] =
    stream.ZStream.environmentWithStream(_.get.findAddInfo(uid))

  def updateAgree(id: Int, agree: Int): ZStream[AddMessageRepository[RStream], Throwable, Int] =
    stream.ZStream.environmentWithStream(_.get.updateAgree(id, agree))

  def saveAddMessage(addMessage: AddMessage): ZStream[AddMessageRepository[RStream], Throwable, Int] =
    stream.ZStream.environmentWithStream(_.get.saveAddMessage(addMessage))

  def make(databaseName: String): ULayer[AddMessageRepository[RStream]] =
    ZLayer.succeed(databaseName) >>> ZLayer(
      ZIO.service[String].map(TangibleAddMessageRepository.apply)
    )
}
