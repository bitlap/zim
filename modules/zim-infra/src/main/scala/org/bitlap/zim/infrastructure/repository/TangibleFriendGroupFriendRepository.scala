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

import org.bitlap.zim.domain.model.AddFriend
import org.bitlap.zim.api.repository.FriendGroupFriendRepository
import scalikejdbc._
import zio._
import zio.stream.ZStream

/** 好友分组操作实现
 *
 *  @author
 *    梦境迷离
 *  @since 2022/1/2
 *  @version 1.0
 */
private final class TangibleFriendGroupFriendRepository(databaseName: String)
    extends TangibleBaseRepository(AddFriend)
    with FriendGroupFriendRepository[RStream] {

  override implicit val dbName: String                                                     = databaseName
  override implicit val sp: QuerySQLSyntaxProvider[SQLSyntaxSupport[AddFriend], AddFriend] = AddFriend.syntax("af")

  override def removeFriend(friendId: Int, uId: Int): RStream[Int] =
    _removeFriend(friendId, uId).toUpdateOperation

  override def changeGroup(groupId: Int, originRecordId: Int): RStream[Int] =
    _changeGroup(groupId, originRecordId).toUpdateOperation

  override def findUserGroup(uId: Int, mId: Int): RStream[Int] =
    _findUserGroup(uId, mId).toStreamOperation

  override def addFriend(from: AddFriend, to: AddFriend): RStream[Int] =
    _addFriend(from, to).toUpdateOperation
}

object TangibleFriendGroupFriendRepository {

  def apply(databaseName: String): FriendGroupFriendRepository[RStream] =
    new TangibleFriendGroupFriendRepository(databaseName)

  def findById(id: Int): stream.ZStream[FriendGroupFriendRepository[RStream], Throwable, AddFriend] =
    stream.ZStream.environmentWithStream(_.get.findById(id))

  def removeFriend(friendId: Int, uId: Int): ZStream[FriendGroupFriendRepository[RStream], Throwable, Int] =
    ZStream.environmentWithStream(_.get.removeFriend(friendId, uId))

  def changeGroup(groupId: Int, originRecordId: Int): ZStream[FriendGroupFriendRepository[RStream], Throwable, Int] =
    ZStream.environmentWithStream(_.get.changeGroup(groupId, originRecordId))

  def findUserGroup(uId: Int, mId: Int): ZStream[FriendGroupFriendRepository[RStream], Throwable, Int] =
    ZStream.environmentWithStream(_.get.findUserGroup(uId, mId))

  def addFriend(from: AddFriend, to: AddFriend): ZStream[FriendGroupFriendRepository[RStream], Throwable, Int] =
    ZStream.environmentWithStream(_.get.addFriend(from, to))

  val live: URLayer[String, FriendGroupFriendRepository[RStream]] = ZLayer(
    ZIO.service[String].map(TangibleFriendGroupFriendRepository.apply)
  )

  def make(databaseName: String): ULayer[FriendGroupFriendRepository[RStream]] =
    ZLayer.succeed(databaseName) >>> live

}
