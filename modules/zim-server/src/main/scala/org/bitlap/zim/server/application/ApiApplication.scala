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

package org.bitlap.zim.server.application

import org.bitlap.zim.domain._
import org.bitlap.zim.domain.input.{ FriendGroupInput, GroupInput, RegisterUserInput, UpdateUserInput, UserSecurity }
import org.bitlap.zim.domain.model._
import org.bitlap.zim.tapir.MultipartInput
import zio.{ stream, IO }

/**
 *  直接提供给endpoint使用
 *  对userService做一定的包装
 *
 * @author 梦境迷离
 * @since 2022/1/8
 * @version 1.0
 */
trait ApiApplication extends BaseApplication[User] {

  def existEmail(email: String): stream.Stream[Throwable, Boolean]

  def findUserById(id: Int): stream.Stream[Throwable, User]

  def updateInfo(user: UpdateUserInput): stream.Stream[Throwable, Boolean]

  def login(user: UserSecurity.UserSecurityInfo): stream.Stream[Throwable, User]

  def init(userId: Int): stream.Stream[Throwable, FriendAndGroupInfo]

  def getOffLineMessage(mid: Int): stream.Stream[Throwable, Receive]

  def register(user: RegisterUserInput): stream.Stream[Throwable, Boolean]

  def activeUser(activeCode: String): stream.Stream[Throwable, Int]

  def createUserGroup(friendGroup: FriendGroupInput): stream.Stream[Throwable, Int]

  def createGroup(groupInput: GroupInput): stream.Stream[Throwable, Int]

  def getMembers(id: Int): stream.Stream[Throwable, FriendList]

  def updateSign(sign: String, mid: Int): stream.Stream[Throwable, Boolean]

  def leaveOutGroup(groupId: Int, mid: Int): stream.Stream[Throwable, Int]

  def removeFriend(friendId: Int, mid: Int): stream.Stream[Throwable, Boolean]

  def changeGroup(groupId: Int, userId: Int, mid: Int): stream.Stream[Throwable, Boolean]

  def refuseFriend(messageBoxId: Int, to: Int, username: String): stream.Stream[Throwable, Boolean]

  def agreeFriend(uid: Int, fromGroup: Int, group: Int, messageBoxId: Int, mid: Int): stream.Stream[Throwable, Boolean]

  def chatLogIndex(id: Int, `type`: String, mid: Int): stream.Stream[Throwable, Int]

  /**
   * 分页接口 内存分页
   *
   * TODO 没有使用数据的offset
   * @param id
   * @param `type`
   * @param page
   * @param mid
   * @return
   */
  def chatLog(id: Int, `type`: String, page: Int, mid: Int): IO[Throwable, ResultPageSet[ChatHistory]]

  def findAddInfo(uid: Int, page: Int): IO[Throwable, ResultPageSet[AddInfo]]

  def findUsers(name: Option[String], sex: Option[Int], page: Int): IO[Throwable, ResultPageSet[User]]

  def findGroups(name: Option[String], page: Int): IO[Throwable, ResultPageSet[GroupList]]

  def findMyGroups(createId: Int, page: Int): IO[Throwable, ResultPageSet[GroupList]]

  /**
   * 聊天文件上传
   *
   * @param multipartInput
   * @return
   */
  def uploadFile(multipartInput: MultipartInput): stream.Stream[Throwable, UploadResult]

  /**
   * 聊天图片上传
   *
   * @param multipartInput
   * @return
   */
  def uploadImage(multipartInput: MultipartInput): stream.Stream[Throwable, UploadResult]

  /**
   * 用户资料的头像更新
   *
   * NOTE: 上传成功就已经更新了。
   *
   * @param multipartInput
   * @param mid
   * @return
   */
  def updateAvatar(multipartInput: MultipartInput, mid: Int): stream.Stream[Throwable, UploadResult]

  /**
   * 群组资料的头像上传
   *
   * @param multipartInput
   * @return
   */
  def uploadGroupAvatar(multipartInput: MultipartInput): stream.Stream[Throwable, UploadResult]

}
