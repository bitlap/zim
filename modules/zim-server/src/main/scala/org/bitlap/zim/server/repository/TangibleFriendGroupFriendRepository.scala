package org.bitlap.zim.server.repository

import org.bitlap.zim.domain.model.AddFriend
import org.bitlap.zim.domain.repository.FriendGroupFriendRepository
import zio.stream.ZStream
import zio.{ stream, Has, ULayer, ZLayer }
import org.bitlap.zim.domain.model

/**
 * 好友分组操作实现
 *
 * @author 梦境迷离
 * @since 2022/1/2
 * @version 1.0
 */

private final class TangibleFriendGroupFriendRepository(databaseName: String)
    extends FriendGroupFriendRepository[AddFriend] {

  private implicit lazy val dbName: String = databaseName

  override def removeFriend(friendId: Int, uId: Int): stream.Stream[Throwable, Int] =
    _removeFriend(AddFriend.table, model.FriendGroup.table, friendId, uId).toUpdateOperation

  override def changeGroup(groupId: Int, originRecordId: Int): stream.Stream[Throwable, Int] =
    _changeGroup(AddFriend.table, groupId, originRecordId).toUpdateOperation

  override def findUserGroup(uId: Int, mId: Int): stream.Stream[Throwable, Int] =
    _findUserGroup(AddFriend.table, model.FriendGroup.table, uId, mId).toStreamOperation

  override def addFriend(from: AddFriend, to: AddFriend): stream.Stream[Throwable, Int] =
    _addFriend(AddFriend.table, from, to).toUpdateOperation

  override def findById(id: Long): stream.Stream[Throwable, AddFriend] =
    queryFindFriendGroupFriendById(AddFriend.table, id).toSQLOperation
}

object TangibleFriendGroupFriendRepository {

  def apply(databaseName: String): FriendGroupFriendRepository[AddFriend] =
    new TangibleFriendGroupFriendRepository(databaseName)

  type ZFriendGroupFriendRepository = Has[FriendGroupFriendRepository[AddFriend]]

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
    ZLayer.fromService[String, FriendGroupFriendRepository[AddFriend]](TangibleFriendGroupFriendRepository(_))

  def make(databaseName: String): ULayer[ZFriendGroupFriendRepository] =
    ZLayer.succeed(databaseName) >>> live

}
