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

import org.bitlap.zim.domain.model._

/** 用户的操作定义
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 1.0
 */
trait UserRepository[F[_]] extends BaseRepository[F, User] {

  def countUser(username: Option[String], sex: Option[Int]): F[Int]

  def findUsers(username: Option[String], sex: Option[Int]): F[User]

  def updateAvatar(avatar: String, uid: Int): F[Int]

  def updateSign(sign: String, uid: Int): F[Int]

  def updateUserInfo(id: Int, user: User): F[Int]

  def updateUserStatus(status: String, uid: Int): F[Int]

  def activeUser(activeCode: String): F[Int]

  def findUserByGroupId(gid: Int): F[User]

  def findUsersByFriendGroupIds(fgid: Int): F[User]

  def saveUser(user: User): F[Long]

  def matchUser(email: String): F[User]
}
