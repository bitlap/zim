package org.bitlap.zim.repository

import org.bitlap.zim.domain.model.{ GroupList, GroupMember }
import zio._

import scala.language.implicitConversions

import org.bitlap.zim.domain.model.Receive
import org.bitlap.zim.repository.TangibleReceiveRepository.ZReceiveRepository

/**
 * 群组的操作实现
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
private final class TangibleGroupRepository(databaseName: String) extends GroupRepository[GroupList] {

  private implicit lazy val dbName: String = databaseName

  override def deleteGroup(id: Int): stream.Stream[Throwable, Int] =
    _deleteGroup(GroupList.table, id).toUpdateOperation

  override def countGroup(groupName: Option[String]): stream.Stream[Throwable, Int] =
    _countGroup(groupName).toStreamOperation

  override def createGroupList(group: GroupList): stream.Stream[Throwable, Long] =
    _createGroupList(GroupList.table, group).toUpdateReturnKey

  override def findGroup(groupName: Option[String]): stream.Stream[Throwable, GroupList] =
    _findGroup(groupName).toStreamOperation

  override def findGroupById(gid: Int): stream.Stream[Throwable, GroupList] =
    _findGroupById(GroupList.table, gid).toStreamOperation

  override def findGroupsById(uid: Int): stream.Stream[Throwable, GroupList] =
    _findGroupsById(GroupList.table, GroupMember.table, uid).toStreamOperation

  override def findById(id: Long): stream.Stream[Throwable, GroupList] =
    queryFindGroupById(GroupList.table, id).toSQLOperation
}

object TangibleGroupRepository {

  def apply(databaseName: String): GroupRepository[GroupList] =
    new TangibleGroupRepository(databaseName)

  type ZGroupRepository = Has[GroupRepository[GroupList]]

  def findById(id: Int): stream.ZStream[ZGroupRepository, Throwable, GroupList] =
    stream.ZStream.accessStream(_.get.findById(id))

  def deleteGroup(id: Int): stream.ZStream[ZGroupRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.deleteGroup(id))

  def countGroup(groupName: Option[String]): stream.ZStream[ZGroupRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.countGroup(groupName))

  def createGroupList(group: GroupList): stream.ZStream[ZGroupRepository, Throwable, Long] =
    stream.ZStream.accessStream(_.get.createGroupList(group))

  def findGroup(groupName: Option[String]): stream.ZStream[ZGroupRepository, Throwable, GroupList] =
    stream.ZStream.accessStream(_.get.findGroup(groupName))

  def findGroupById(gid: Int): stream.ZStream[ZGroupRepository, Throwable, GroupList] =
    stream.ZStream.accessStream(_.get.findGroupById(gid))

  def findGroupsById(uid: Int): stream.ZStream[ZGroupRepository, Throwable, GroupList] =
    stream.ZStream.accessStream(_.get.findGroupsById(uid))

  val live: ZLayer[Has[String], Nothing, ZGroupRepository] =
    ZLayer.fromService[String, GroupRepository[GroupList]](TangibleGroupRepository(_))

  def make(databaseName: String): ULayer[ZGroupRepository] =
    ZLayer.succeed(databaseName) >>> live

}
