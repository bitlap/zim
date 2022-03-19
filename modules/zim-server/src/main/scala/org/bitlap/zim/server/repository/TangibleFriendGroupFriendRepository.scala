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

import org.bitlap.zim.domain.model.AddFriend
import org.bitlap.zim.domain.repository.FriendGroupFriendRepository
import scalikejdbc._
import zio.stream.ZStream
import zio.{ stream, Has, ULayer, ZLayer }

/**
 * 好友分组操作实现
 *
 * @author 梦境迷离
 * @since 2022/1/2
 * @version 1.0
 */
private final class TangibleFriendGroupFriendRepository(databaseName: String)
    extends TangibleBaseRepository(AddFriend)
    with FriendGroupFriendRepository {

  override implicit val dbName: String = databaseName
  override implicit val sp: QuerySQLSyntaxProvider[SQLSyntaxSupport[AddFriend], AddFriend] = AddFriend.syntax("af")

  override def removeFriend(friendId: Int, uId: Int): stream.Stream[Throwable, Int] =
    _removeFriend(friendId, uId).toUpdateOperation

  override def changeGroup(groupId: Int, originRecordId: Int): stream.Stream[Throwable, Int] =
    _changeGroup(groupId, originRecordId).toUpdateOperation

  override def findUserGroup(uId: Int, mId: Int): stream.Stream[Throwable, Int] =
    _findUserGroup(uId, mId).toStreamOperation

  override def addFriend(from: AddFriend, to: AddFriend): stream.Stream[Throwable, Int] =
    _addFriend(from, to).toUpdateOperation
}

object TangibleFriendGroupFriendRepository {

  def apply(databaseName: String): FriendGroupFriendRepository =
    new TangibleFriendGroupFriendRepository(databaseName)

  type ZFriendGroupFriendRepository = Has[FriendGroupFriendRepository]

  def findById(id: Int): stream.ZStream[ZFriendGroupFriendRepository, Throwable, AddFriend] =
    stream.ZStream.accessStream(_.get.findById(id))

  def removeFriend(friendId: Int, uId: Int): ZStream[ZFriendGroupFriendRepository, Throwable, Int] =
    ZStream.accessStream(_.get.removeFriend(friendId, uId))

  def changeGroup(groupId: Int, originRecordId: Int): ZStream[ZFriendGroupFriendRepository, Throwable, Int] =
    ZStream.accessStream(_.get.changeGroup(groupId, originRecordId))

  def findUserGroup(uId: Int, mId: Int): ZStream[ZFriendGroupFriendRepository, Throwable, Int] =
    ZStream.accessStream(_.get.findUserGroup(uId, mId))

  def addFriend(from: AddFriend, to: AddFriend): ZStream[ZFriendGroupFriendRepository, Throwable, Int] =
    ZStream.accessStream(_.get.addFriend(from, to))

  val live: ZLayer[Has[String], Nothing, ZFriendGroupFriendRepository] =
    ZLayer.fromService[String, FriendGroupFriendRepository](TangibleFriendGroupFriendRepository(_))

  def make(databaseName: String): ULayer[ZFriendGroupFriendRepository] =
    ZLayer.succeed(databaseName) >>> live

}
