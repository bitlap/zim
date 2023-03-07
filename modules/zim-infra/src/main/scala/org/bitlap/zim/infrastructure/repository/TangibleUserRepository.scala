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

import org.bitlap.zim.api.repository.UserRepository
import org.bitlap.zim.domain.model
import org.bitlap.zim.domain.model.User
import scalikejdbc._
import zio._

/** 用户的操作实现
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 1.0
 */
private final class TangibleUserRepository(databaseName: String)
    extends TangibleBaseRepository(User)
    with UserRepository[RStream] {

  override implicit val sp: QuerySQLSyntaxProvider[SQLSyntaxSupport[User], User] = User.syntax("u")
  override implicit lazy val dbName: String                                      = databaseName

  override def countUser(username: Option[String], sex: Option[Int]): RStream[Int] =
    this.count("username" like username, "sex" === sex)

  override def findUsers(username: Option[String], sex: Option[Int]): RStream[model.User] =
    this.find("username" like username, "sex" === sex)

  override def updateAvatar(avatar: String, uid: Int): RStream[Int] =
    _updateAvatar(avatar, uid).toUpdateOperation

  override def updateSign(sign: String, uid: Int): RStream[Int] =
    _updateSign(sign, uid).toUpdateOperation

  override def updateUserInfo(id: Int, user: model.User): RStream[Int] =
    _updateUserInfo(id, user).toUpdateOperation

  override def updateUserStatus(status: String, uid: Int): RStream[Int] =
    _updateUserStatus(status, uid).toUpdateOperation

  override def activeUser(activeCode: String): RStream[Int] =
    _activeUser(activeCode).toUpdateOperation

  override def findUserByGroupId(gid: Int): RStream[model.User] =
    _findUserByGroupId(gid).toStreamOperation

  override def findUsersByFriendGroupIds(fgid: Int): RStream[model.User] =
    _findUsersByFriendGroupIds(fgid).toStreamOperation

  override def saveUser(user: model.User): RStream[Long] =
    _saveUser(user).toUpdateReturnKey

  override def matchUser(email: String): RStream[model.User] =
    this.find("email" === email)
}

object TangibleUserRepository {

  def apply(databaseName: String): UserRepository[RStream] =
    new TangibleUserRepository(databaseName)

  /** 下面的测试很有用，对外提供
   *
   *  @return
   */
  def findById(id: Int): stream.ZStream[UserRepository[RStream], Throwable, model.User] =
    stream.ZStream.environmentWithStream(_.get.findById(id))

  def saveUser(user: model.User): stream.ZStream[UserRepository[RStream], Throwable, Long] =
    stream.ZStream.environmentWithStream(_.get.saveUser(user))

  def matchUser(email: String): stream.ZStream[UserRepository[RStream], Throwable, model.User] =
    stream.ZStream.environmentWithStream(_.get.matchUser(email))

  def activeUser(activeCode: String): stream.ZStream[UserRepository[RStream], Throwable, Int] =
    stream.ZStream.environmentWithStream(_.get.activeUser(activeCode))

  def countUser(username: Option[String], sex: Option[Int]): stream.ZStream[UserRepository[RStream], Throwable, Int] =
    stream.ZStream.environmentWithStream(_.get.countUser(username, sex))

  def findUsers(
    username: Option[String],
    sex: Option[Int]
  ): stream.ZStream[UserRepository[RStream], Throwable, model.User] =
    stream.ZStream.environmentWithStream(_.get.findUsers(username, sex))

  def findUserByGroupId(gid: Int): stream.ZStream[UserRepository[RStream], Throwable, model.User] =
    stream.ZStream.environmentWithStream(_.get.findUserByGroupId(gid))

  def findUsersByFriendGroupIds(fgid: Int): stream.ZStream[UserRepository[RStream], Throwable, model.User] =
    stream.ZStream.environmentWithStream(_.get.findUsersByFriendGroupIds(fgid))

  def updateAvatar(avatar: String, uid: Int): stream.ZStream[UserRepository[RStream], Throwable, Int] =
    stream.ZStream.environmentWithStream(_.get.updateAvatar(avatar, uid))

  def updateSign(sign: String, uid: Int): stream.ZStream[UserRepository[RStream], Throwable, Int] =
    stream.ZStream.environmentWithStream(_.get.updateSign(sign, uid))

  def updateUserInfo(id: Int, user: model.User): stream.ZStream[UserRepository[RStream], Throwable, Int] =
    stream.ZStream.environmentWithStream(_.get.updateUserInfo(id, user))

  def updateUserStatus(status: String, uid: Int): stream.ZStream[UserRepository[RStream], Throwable, Int] =
    stream.ZStream.environmentWithStream(_.get.updateUserStatus(status, uid))

  def make(databaseName: String): ULayer[UserRepository[RStream]] =
    ZLayer.succeed(databaseName) >>> ZLayer(ZIO.service[String].map(TangibleUserRepository.apply))

}
