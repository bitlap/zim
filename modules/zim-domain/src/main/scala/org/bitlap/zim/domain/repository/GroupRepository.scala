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

package org.bitlap.zim.domain.repository

import org.bitlap.zim.domain.model.GroupList

/** 群组的操作定义
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/29
 *  @version 1.0
 */
trait GroupRepository[F[_]] extends BaseRepository[F, GroupList] {

  def deleteGroup(id: Int): F[Int]

  def countGroup(groupName: Option[String]): F[Int]

  def createGroupList(group: GroupList): F[Long]

  def findGroups(groupName: Option[String]): F[GroupList]

  def findGroupById(gid: Int): F[GroupList]

  def findGroupsById(uid: Int): F[GroupList]

}
