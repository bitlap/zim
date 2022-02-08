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

  override def findById(id: Long): stream.Stream[Throwable, AddFriend] =
    queryFindFriendGroupFriendById(id).toSQLOperation

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
