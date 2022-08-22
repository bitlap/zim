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

package org.bitlap.zim.api.repository

import org.bitlap.zim.domain.model.AddFriend

/** 好友分组操作定义
 *
 *  @author
 *    梦境迷离
 *  @since 2022/1/2
 *  @version 1.0
 */
trait FriendGroupFriendRepository[F[_]] extends BaseRepository[F, AddFriend] {

  def removeFriend(friendId: Int, uId: Int): F[Int]

  def changeGroup(groupId: Int, originRecordId: Int): F[Int]

  def findUserGroup(uId: Int, mId: Int): F[Int]

  def addFriend(from: AddFriend, to: AddFriend): F[Int]
}
