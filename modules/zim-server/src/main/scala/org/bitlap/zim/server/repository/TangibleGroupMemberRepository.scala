package org.bitlap.zim.server.repository

import org.bitlap.zim.domain.model.GroupMember
import org.bitlap.zim.domain.repository.GroupMemberRepository
import scalikejdbc._
import zio._
import zio.stream.ZStream
private final class TangibleGroupMemberRepository(databaseName: String)
    extends TangibleBaseRepository(GroupMember)
    with GroupMemberRepository {

  override implicit val dbName: String = databaseName
  override implicit val sp: QuerySQLSyntaxProvider[SQLSyntaxSupport[GroupMember], GroupMember] =
    GroupMember.syntax("gm")

  override def leaveOutGroup(groupMember: GroupMember): stream.Stream[Throwable, Int] =
    _leaveOutGroup(groupMember).toUpdateOperation

  override def findGroupMembers(gid: Int): stream.Stream[Throwable, Int] =
    _findGroupMembers(gid).toStreamOperation

  override def addGroupMember(groupMember: GroupMember): stream.Stream[Throwable, Int] =
    _addGroupMember(groupMember).toUpdateOperation
}

object TangibleGroupMemberRepository {

  def apply(databaseName: String): GroupMemberRepository =
    new TangibleGroupMemberRepository(databaseName)

  type ZGroupMemberRepository = Has[GroupMemberRepository]

  def findById(id: Int): stream.ZStream[ZGroupMemberRepository, Throwable, GroupMember] =
    stream.ZStream.accessStream(_.get.findById(id))

  def leaveOutGroup(groupMember: GroupMember): ZStream[ZGroupMemberRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.leaveOutGroup(groupMember))

  def findGroupMembers(gid: Int): ZStream[ZGroupMemberRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.findGroupMembers(gid))

  def addGroupMember(groupMember: GroupMember): ZStream[ZGroupMemberRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.addGroupMember(groupMember))

  val live: ZLayer[Has[String], Nothing, ZGroupMemberRepository] =
    ZLayer.fromService[String, GroupMemberRepository](TangibleGroupMemberRepository(_))

  def make(databaseName: String): ULayer[ZGroupMemberRepository] =
    ZLayer.succeed(databaseName) >>> live
}
