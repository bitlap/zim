/*
 * Copyright 2023 bitlap
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

import org.bitlap.zim.api.repository.GroupMemberRepository
import org.bitlap.zim.domain.model.GroupMember
import scalikejdbc._
import zio._
import zio.stream.ZStream

private final class TangibleGroupMemberRepository(databaseName: String)
    extends TangibleBaseRepository(GroupMember)
    with GroupMemberRepository[RStream] {

  override implicit val dbName: String = databaseName
  override implicit val sp: QuerySQLSyntaxProvider[SQLSyntaxSupport[GroupMember], GroupMember] =
    GroupMember.syntax("gm")

  override def leaveOutGroup(groupMember: GroupMember): RStream[Int] =
    _leaveOutGroup(groupMember).toUpdateOperation

  override def findGroupMembers(gid: Int): RStream[Int] =
    _findGroupMembers(gid).toStreamOperation

  override def addGroupMember(groupMember: GroupMember): RStream[Int] =
    _addGroupMember(groupMember).toUpdateOperation
}

object TangibleGroupMemberRepository {

  def apply(databaseName: String): GroupMemberRepository[RStream] =
    new TangibleGroupMemberRepository(databaseName)

  def findById(id: Int): stream.ZStream[GroupMemberRepository[RStream], Throwable, GroupMember] =
    stream.ZStream.environmentWithStream(_.get.findById(id))

  def leaveOutGroup(groupMember: GroupMember): ZStream[GroupMemberRepository[RStream], Throwable, Int] =
    stream.ZStream.environmentWithStream(_.get.leaveOutGroup(groupMember))

  def findGroupMembers(gid: Int): ZStream[GroupMemberRepository[RStream], Throwable, Int] =
    stream.ZStream.environmentWithStream(_.get.findGroupMembers(gid))

  def addGroupMember(groupMember: GroupMember): ZStream[GroupMemberRepository[RStream], Throwable, Int] =
    stream.ZStream.environmentWithStream(_.get.addGroupMember(groupMember))

  def make(databaseName: String): ULayer[GroupMemberRepository[RStream]] =
    ZLayer.succeed(databaseName) >>> ZLayer(
      ZIO.service[String].map(TangibleGroupMemberRepository.apply)
    )
}
