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

package org.bitlap.zim.infrastructure.repository

import org.bitlap.zim.domain.model.GroupList
import org.bitlap.zim.api.repository.GroupRepository
import scalikejdbc._
import zio._

/** 群组的操作实现
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 1.0
 */
private final class TangibleGroupRepository(databaseName: String)
    extends TangibleBaseRepository(GroupList)
    with GroupRepository[RStream] {

  override implicit val dbName: String                                                     = databaseName
  override implicit val sp: QuerySQLSyntaxProvider[SQLSyntaxSupport[GroupList], GroupList] = GroupList.syntax("g")

  override def deleteGroup(id: Int): RStream[Int] =
    _deleteGroup(id).toUpdateOperation

  override def countGroup(groupName: Option[String]): RStream[Int] =
    _countGroup(groupName).toStreamOperation

  override def createGroupList(group: GroupList): RStream[Long] =
    _createGroupList(group).toUpdateReturnKey

  override def findGroups(groupName: Option[String]): RStream[GroupList] =
    _findGroups(groupName).toStreamOperation

  override def findGroupById(gid: Int): RStream[GroupList] =
    _findGroupById(gid).toStreamOperation

  override def findGroupsById(uid: Int): RStream[GroupList] =
    _findGroupsById(uid).toStreamOperation
}

object TangibleGroupRepository {

  def apply(databaseName: String): GroupRepository[RStream] =
    new TangibleGroupRepository(databaseName)

  def findById(id: Int): stream.ZStream[GroupRepository[RStream], Throwable, GroupList] =
    stream.ZStream.environmentWithStream(_.get.findById(id))

  def deleteGroup(id: Int): stream.ZStream[GroupRepository[RStream], Throwable, Int] =
    stream.ZStream.environmentWithStream(_.get.deleteGroup(id))

  def countGroup(groupName: Option[String]): stream.ZStream[GroupRepository[RStream], Throwable, Int] =
    stream.ZStream.environmentWithStream(_.get.countGroup(groupName))

  def createGroupList(group: GroupList): stream.ZStream[GroupRepository[RStream], Throwable, Long] =
    stream.ZStream.environmentWithStream(_.get.createGroupList(group))

  def findGroup(groupName: Option[String]): stream.ZStream[GroupRepository[RStream], Throwable, GroupList] =
    stream.ZStream.environmentWithStream(_.get.findGroups(groupName))

  def findGroupById(gid: Int): stream.ZStream[GroupRepository[RStream], Throwable, GroupList] =
    stream.ZStream.environmentWithStream(_.get.findGroupById(gid))

  def findGroupsById(uid: Int): stream.ZStream[GroupRepository[RStream], Throwable, GroupList] =
    stream.ZStream.environmentWithStream(_.get.findGroupsById(uid))

  val live: URLayer[String, GroupRepository[RStream]] = ZLayer(ZIO.service[String].map(TangibleGroupRepository.apply))

  def make(databaseName: String): ULayer[GroupRepository[RStream]] =
    ZLayer.succeed(databaseName) >>> live

}
