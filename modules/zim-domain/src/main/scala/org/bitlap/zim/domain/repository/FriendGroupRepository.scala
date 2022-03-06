/*
 * Copyright 2021 bitlap
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
import org.bitlap.zim.domain.model.FriendGroup
import zio.stream

/**
 * 好友分组操作定义
 *
 * @author LittleTear
 * @since 2021/12/31
 * @version 1.0
 */
trait FriendGroupRepository extends BaseRepository[FriendGroup] {

  def createFriendGroup(friend: FriendGroup): stream.Stream[Throwable, Int]

  def findFriendGroupsById(uid: Int): stream.Stream[Throwable, FriendGroup]
}
