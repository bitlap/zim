package org.bitlap.zim.repository

import org.bitlap.zim.domain.model.{ AddFriends, FriendGroup }
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
    extends FriendGroupFriendRepository[AddFriends] {

  private implicit lazy val dbName: String = databaseName

  override def removeFriend(friendId: Int, uId: Int): stream.Stream[Throwable, Int] =
    _removeFriend(AddFriends.table, FriendGroup.table, friendId, uId).toUpdateOperation

  override def changeGroup(groupId: Int, originRecordId: Int): stream.Stream[Throwable, Int] =
    _changeGroup(AddFriends.table, groupId, originRecordId).toUpdateOperation

  override def findUserGroup(uId: Int, mId: Int): stream.Stream[Throwable, Int] =
    _findUserGroup(AddFriends.table, FriendGroup.table, uId, mId).toStreamOperation

  override def addFriend(addFriends: AddFriends): stream.Stream[Throwable, Int] =
    _addFriend(AddFriends.table, addFriends).toUpdateOperation

  override def findById(id: Long): stream.Stream[Throwable, AddFriends] =
    queryFindFriendGroupFriendById(AddFriends.table, id).toSQLOperation
}

object TangibleFriendGroupFriendRepository {

  def apply(databaseName: String): FriendGroupFriendRepository[AddFriends] =
    new TangibleFriendGroupFriendRepository(databaseName)

  type ZFriendGroupFriendRepository = Has[FriendGroupFriendRepository[AddFriends]]

  def findById(id: Int): stream.ZStream[ZFriendGroupFriendRepository, Throwable, AddFriends] =
    stream.ZStream.accessStream(_.get.findById(id))

  def removeFriend(friendId: Int, uId: Int): ZStream[ZFriendGroupFriendRepository, Throwable, Int] =
    ZStream.accessStream(_.get.removeFriend(friendId, uId))

  def changeGroup(groupId: Int, originRecordId: Int): ZStream[ZFriendGroupFriendRepository, Throwable, Int] =
    ZStream.accessStream(_.get.changeGroup(groupId, originRecordId))

  def findUserGroup(uId: Int, mId: Int): ZStream[ZFriendGroupFriendRepository, Throwable, Int] =
    ZStream.accessStream(_.get.findUserGroup(uId, mId))

  def addFriend(addFriends: AddFriends): ZStream[ZFriendGroupFriendRepository, Throwable, Int] =
    ZStream.accessStream(_.get.addFriend(addFriends))

  val live: ZLayer[Has[String], Nothing, ZFriendGroupFriendRepository] =
    ZLayer.fromService[String, FriendGroupFriendRepository[AddFriends]](TangibleFriendGroupFriendRepository(_))

  def make(databaseName: String): ULayer[ZFriendGroupFriendRepository] =
    ZLayer.succeed(databaseName) >>> live

}
