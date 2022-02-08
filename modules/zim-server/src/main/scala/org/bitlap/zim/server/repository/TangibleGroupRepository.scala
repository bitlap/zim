package org.bitlap.zim.server.repository

import org.bitlap.zim.domain.model.GroupList
import org.bitlap.zim.domain.repository.GroupRepository
import scalikejdbc._
import zio._

import scala.language.implicitConversions

/**
 * 群组的操作实现
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
private final class TangibleGroupRepository(databaseName: String)
    extends TangibleBaseRepository(GroupList)
    with GroupRepository {

  override implicit val dbName: String = databaseName
  override implicit val sp: QuerySQLSyntaxProvider[SQLSyntaxSupport[GroupList], GroupList] = GroupList.syntax("gl")

  override def deleteGroup(id: Int): stream.Stream[Throwable, Int] =
    _deleteGroup(id).toUpdateOperation

  override def countGroup(groupName: Option[String]): stream.Stream[Throwable, Int] =
    _countGroup(groupName).toStreamOperation

  override def createGroupList(group: GroupList): stream.Stream[Throwable, Long] =
    _createGroupList(group).toUpdateReturnKey

  override def findGroups(groupName: Option[String]): stream.Stream[Throwable, GroupList] =
    _findGroups(groupName).toStreamOperation

  override def findGroupById(gid: Int): stream.Stream[Throwable, GroupList] =
    _findGroupById(gid).toStreamOperation

  override def findGroupsById(uid: Int): stream.Stream[Throwable, GroupList] =
    _findGroupsById(uid).toStreamOperation
}

object TangibleGroupRepository {

  def apply(databaseName: String): GroupRepository =
    new TangibleGroupRepository(databaseName)

  type ZGroupRepository = Has[GroupRepository]

  def findById(id: Int): stream.ZStream[ZGroupRepository, Throwable, GroupList] =
    stream.ZStream.accessStream(_.get.findById(id))

  def deleteGroup(id: Int): stream.ZStream[ZGroupRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.deleteGroup(id))

  def countGroup(groupName: Option[String]): stream.ZStream[ZGroupRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.countGroup(groupName))

  def createGroupList(group: GroupList): stream.ZStream[ZGroupRepository, Throwable, Long] =
    stream.ZStream.accessStream(_.get.createGroupList(group))

  def findGroup(groupName: Option[String]): stream.ZStream[ZGroupRepository, Throwable, GroupList] =
    stream.ZStream.accessStream(_.get.findGroups(groupName))

  def findGroupById(gid: Int): stream.ZStream[ZGroupRepository, Throwable, GroupList] =
    stream.ZStream.accessStream(_.get.findGroupById(gid))

  def findGroupsById(uid: Int): stream.ZStream[ZGroupRepository, Throwable, GroupList] =
    stream.ZStream.accessStream(_.get.findGroupsById(uid))

  val live: ZLayer[Has[String], Nothing, ZGroupRepository] =
    ZLayer.fromService[String, GroupRepository](TangibleGroupRepository(_))

  def make(databaseName: String): ULayer[ZGroupRepository] =
    ZLayer.succeed(databaseName) >>> live

}
