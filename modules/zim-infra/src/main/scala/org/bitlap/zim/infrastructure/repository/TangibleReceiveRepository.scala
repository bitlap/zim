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

import org.bitlap.zim.api.repository.ReceiveRepository
import org.bitlap.zim.domain.model.Receive

import zio._
import zio.stream.ZStream

/** 消息的操作实现
 *
 *  @author
 *    LittleTear
 *  @since 2021/12/30
 *  @version 1.0
 */
private final class TangibleReceiveRepository(databaseName: String) extends ReceiveRepository[RStream] {

  private implicit lazy val dbName: String = databaseName

  override def saveMessage(receive: Receive): RStream[Int] =
    _saveMessage(receive).toUpdateOperation

  override def findOffLineMessage(uid: Int, status: Int): RStream[Receive] =
    _findOffLineMessage(uid, status).toStreamOperation

  override def findHistoryMessage(
      uid: Option[Int],
      mid: Option[Int],
      typ: Option[String]
  ): RStream[Receive] =
    _findHistoryMessage(uid, mid, typ).toStreamOperation

  override def countHistoryMessage(
      uid: Option[Int],
      mid: Option[Int],
      typ: Option[String]
  ): RStream[Int] =
    _countHistoryMessage(uid, mid, typ).toStreamOperation

  override def readMessage(mine: Int, to: Int, typ: String): RStream[Int] =
    _readMessage(mine, to, typ).toUpdateOperation

  override def findById(id: Long): RStream[Receive] =
    queryFindReceiveById(id).toSQLOperation
}

object TangibleReceiveRepository {

  def apply(databaseName: String): ReceiveRepository[RStream] =
    new TangibleReceiveRepository(databaseName)

  def saveMessage(receive: Receive): ZStream[ReceiveRepository[RStream], Throwable, Int] =
    stream.ZStream.environmentWithStream(_.get.saveMessage(receive))

  def findOffLineMessage(uid: Int, status: Int): ZStream[ReceiveRepository[RStream], Throwable, Receive] =
    stream.ZStream.environmentWithStream(_.get.findOffLineMessage(uid, status))

  def findHistoryMessage(
      uid: Option[Int],
      mid: Option[Int],
      typ: Option[String]
  ): ZStream[ReceiveRepository[RStream], Throwable, Receive] =
    stream.ZStream.environmentWithStream(_.get.findHistoryMessage(uid, mid, typ))

  def countHistoryMessage(
      uid: Option[Int],
      mid: Option[Int],
      typ: Option[String]
  ): ZStream[ReceiveRepository[RStream], Throwable, Int] =
    stream.ZStream.environmentWithStream(_.get.countHistoryMessage(uid, mid, typ))

  def readMessage(mine: Int, to: Int, typ: String): ZStream[ReceiveRepository[RStream], Throwable, Int] =
    stream.ZStream.environmentWithStream(_.get.readMessage(mine, to, typ))

  def findById(id: Int): stream.ZStream[ReceiveRepository[RStream], Throwable, Receive] =
    stream.ZStream.environmentWithStream(_.get.findById(id))

  def make(databaseName: String): ULayer[ReceiveRepository[RStream]] =
    ZLayer.succeed(databaseName) >>> ZLayer(
      ZIO.service[String].map(TangibleReceiveRepository.apply)
    )

}
