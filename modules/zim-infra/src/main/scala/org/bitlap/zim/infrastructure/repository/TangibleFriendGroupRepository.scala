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

package org.bitlap.zim.infrastructure.repository

import org.bitlap.zim.domain.model.FriendGroup
import org.bitlap.zim.api.repository.FriendGroupRepository
import scalikejdbc._
import zio._

/** 好友分组操作实现
 *
 *  @author
 *    LittleTear
 *  @since 2021/12/31
 *  @version 1.0
 */
private final class TangibleFriendGroupRepository(databaseName: String)
    extends TangibleBaseRepository(FriendGroup)
    with FriendGroupRepository[RStream] {

  override implicit val dbName: String = databaseName
  override implicit val sp: QuerySQLSyntaxProvider[SQLSyntaxSupport[FriendGroup], FriendGroup] =
    FriendGroup.syntax("fg")

  override def createFriendGroup(friend: FriendGroup): RStream[Int] =
    _createFriendGroup(friend).toUpdateOperation

  override def findFriendGroupsById(uid: Int): RStream[FriendGroup] =
    _findFriendGroupsById(uid).toStreamOperation
}

object TangibleFriendGroupRepository {

  def apply(databaseName: String): FriendGroupRepository[RStream] =
    new TangibleFriendGroupRepository(databaseName)

  def findById(id: Int): stream.ZStream[FriendGroupRepository[RStream], Throwable, FriendGroup] =
    stream.ZStream.environmentWithStream(_.get.findById(id))

  def createFriendGroup(friend: FriendGroup): stream.ZStream[FriendGroupRepository[RStream], Throwable, Int] =
    stream.ZStream.environmentWithStream(_.get.createFriendGroup(friend))

  def findFriendGroupsById(uid: Int): stream.ZStream[FriendGroupRepository[RStream], Throwable, FriendGroup] =
    stream.ZStream.environmentWithStream(_.get.findFriendGroupsById(uid))

  val live: URLayer[String, FriendGroupRepository[RStream]] = ZLayer(
    ZIO.service[String].map(TangibleFriendGroupRepository.apply)
  )

  def make(databaseName: String): ULayer[FriendGroupRepository[RStream]] = ZLayer.succeed(databaseName) >>> live

}
