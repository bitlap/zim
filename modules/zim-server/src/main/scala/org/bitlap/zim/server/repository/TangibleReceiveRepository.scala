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
    _saveMessage(receive).toUpdateOperation

  override def findOffLineMessage(uid: Int, status: Int): stream.Stream[Throwable, Receive] =
    _findOffLineMessage(uid, status).toStreamOperation

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
    _readMessage(mine, to, typ).toUpdateOperation

  override def findById(id: Long): stream.Stream[Throwable, Receive] =
    queryFindReceiveById(id).toSQLOperation
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

  val live: URLayer[Has[String], ZReceiveRepository] =
    ZLayer.fromService[String, ReceiveRepository[Receive]](TangibleReceiveRepository(_))

  def make(databaseName: String): ULayer[ZReceiveRepository] =
    ZLayer.succeed(databaseName) >>> live

}
