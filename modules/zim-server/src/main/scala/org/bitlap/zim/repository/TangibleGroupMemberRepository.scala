package org.bitlap.zim.repository

import org.bitlap.zim.domain.model.GroupMember
import org.bitlap.zim.domain.repository.GroupMemberRepository
import zio._
import zio.stream.ZStream

private final class TangibleGroupMemberRepository(databaseName: String) extends GroupMemberRepository[GroupMember] {

  private implicit lazy val dbName: String = databaseName

  override def leaveOutGroup(groupMember: GroupMember): stream.Stream[Throwable, Int] =
    _leaveOutGroup(GroupMember.table, groupMember).toUpdateOperation

  override def findGroupMembers(gid: Int): stream.Stream[Throwable, Int] =
    _findGroupMembers(GroupMember.table, gid).toStreamOperation

  override def addGroupMember(groupMember: GroupMember): stream.Stream[Throwable, Int] =
    _addGroupMember(GroupMember.table, groupMember).toUpdateOperation

  override def findById(id: Long): stream.Stream[Throwable, GroupMember] =
    queryFindGroupMemberById(GroupMember.table, id).toSQLOperation
}

object TangibleGroupMemberRepository {

  def apply(databaseName: String): GroupMemberRepository[GroupMember] =
    new TangibleGroupMemberRepository(databaseName)

  type ZGroupMemberRepository = Has[GroupMemberRepository[GroupMember]]

  def findById(id: Int): stream.ZStream[ZGroupMemberRepository, Throwable, GroupMember] =
    stream.ZStream.accessStream(_.get.findById(id))

  def leaveOutGroup(groupMember: GroupMember): ZStream[ZGroupMemberRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.leaveOutGroup(groupMember))

  def findGroupMembers(gid: Int): ZStream[ZGroupMemberRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.findGroupMembers(gid))

  def addGroupMember(groupMember: GroupMember): ZStream[ZGroupMemberRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.addGroupMember(groupMember))

  val live: ZLayer[Has[String], Nothing, ZGroupMemberRepository] =
    ZLayer.fromService[String, GroupMemberRepository[GroupMember]](TangibleGroupMemberRepository(_))

  def make(databaseName: String): ULayer[ZGroupMemberRepository] =
    ZLayer.succeed(databaseName) >>> live
}
