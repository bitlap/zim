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

import org.bitlap.zim.domain.model.GroupMember

/** @author
 *    梦境迷离
 *  @since 2022/1/15
 *  @version 1.0
 */
trait GroupMemberRepository[F[_]] extends BaseRepository[F, GroupMember] {

  def leaveOutGroup(groupMember: GroupMember): F[Int]

  def findGroupMembers(gid: Int): F[Int]

  def addGroupMember(groupMember: GroupMember): F[Int]

}
