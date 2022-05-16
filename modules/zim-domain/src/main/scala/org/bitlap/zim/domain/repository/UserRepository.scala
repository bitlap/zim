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

import org.bitlap.zim.domain.model.User
import zio.stream

/** 用户的操作定义
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 1.0
 */
trait UserRepository extends BaseRepository[User] {

  def countUser(username: Option[String], sex: Option[Int]): stream.Stream[Throwable, Int]

  def findUsers(username: Option[String], sex: Option[Int]): stream.Stream[Throwable, User]

  def updateAvatar(avatar: String, uid: Int): stream.Stream[Throwable, Int]

  def updateSign(sign: String, uid: Int): stream.Stream[Throwable, Int]

  def updateUserInfo(id: Int, user: User): stream.Stream[Throwable, Int]

  def updateUserStatus(status: String, uid: Int): stream.Stream[Throwable, Int]

  def activeUser(activeCode: String): stream.Stream[Throwable, Int]

  def findUserByGroupId(gid: Int): stream.Stream[Throwable, User]

  def findUsersByFriendGroupIds(fgid: Int): stream.Stream[Throwable, User]

  def saveUser(user: User): stream.Stream[Throwable, Long]

  def matchUser(email: String): stream.Stream[Throwable, User]
}
