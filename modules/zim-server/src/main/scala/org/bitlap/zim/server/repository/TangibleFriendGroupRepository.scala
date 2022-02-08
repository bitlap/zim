package org.bitlap.zim.server.repository

import org.bitlap.zim.domain.model.FriendGroup
import org.bitlap.zim.domain.repository.FriendGroupRepository
import scalikejdbc._
import zio.{ stream, Has, ULayer, ZLayer }

/**
 * 好友分组操作实现
 *
 * @author LittleTear
 * @since 2021/12/31
 * @version 1.0
 */

private final class TangibleFriendGroupRepository(databaseName: String)
    extends TangibleBaseRepository(FriendGroup)
    with FriendGroupRepository {

  override implicit val dbName: String = databaseName
  override implicit val sp: QuerySQLSyntaxProvider[SQLSyntaxSupport[FriendGroup], FriendGroup] =
    FriendGroup.syntax("fg")

  override def createFriendGroup(friend: FriendGroup): stream.Stream[Throwable, Int] =
    _createFriendGroup(friend).toUpdateOperation

  override def findFriendGroupsById(uid: Int): stream.Stream[Throwable, FriendGroup] =
    _findFriendGroupsById(uid).toStreamOperation

  override def findById(id: Long): stream.Stream[Throwable, FriendGroup] =
    queryFindFriendGroupById(id).toSQLOperation

}

object TangibleFriendGroupRepository {

  def apply(databaseName: String): FriendGroupRepository =
    new TangibleFriendGroupRepository(databaseName)

  type ZFriendGroupRepository = Has[FriendGroupRepository]

  def findById(id: Int): stream.ZStream[ZFriendGroupRepository, Throwable, FriendGroup] =
    stream.ZStream.accessStream(_.get.findById(id))

  def createFriendGroup(friend: FriendGroup): stream.ZStream[ZFriendGroupRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.createFriendGroup(friend))

  def findFriendGroupsById(uid: Int): stream.ZStream[ZFriendGroupRepository, Throwable, FriendGroup] =
    stream.ZStream.accessStream(_.get.findFriendGroupsById(uid))

  val live: ZLayer[Has[String], Nothing, ZFriendGroupRepository] =
    ZLayer.fromService[String, FriendGroupRepository](TangibleFriendGroupRepository(_))

  def make(databaseName: String): ULayer[ZFriendGroupRepository] =
    ZLayer.succeed(databaseName) >>> live

}
