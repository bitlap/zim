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

package org.bitlap.zim.api.service

import org.bitlap.zim.api._
import org.bitlap.zim.domain._
import org.bitlap.zim.domain.input._
import org.bitlap.zim.domain.model._

/** 直接提供给endpoint使用 对userService做一定的包装
 *
 *  @author
 *    梦境迷离
 *  @since 2022/1/8
 *  @version 1.0
 */
trait ApiService[F[_], S[_]] extends PaginationApiService[S] {

  def existEmail(email: String): F[Boolean]

  def findUserById(id: Int): F[User]

  def updateInfo(user: UpdateUserInput): F[Boolean]

  def login(user: UserSecurity.UserSecurityInfo): F[User]

  def init(userId: Int): F[FriendAndGroupInfo]

  def getOffLineMessage(mid: Int): F[Receive]

  def register(user: RegisterUserInput): F[Boolean]

  def activeUser(activeCode: String): F[Int]

  def createUserGroup(friendGroup: FriendGroupInput): F[Int]

  def createGroup(groupInput: GroupInput): F[Int]

  def getMembers(id: Int): F[FriendList]

  def updateSign(sign: String, mid: Int): F[Boolean]

  def leaveOutGroup(groupId: Int, mid: Int): F[Int]

  def removeFriend(friendId: Int, mid: Int): F[Boolean]

  def changeGroup(groupId: Int, userId: Int, mid: Int): F[Boolean]

  def refuseFriend(messageBoxId: Int, to: Int, username: String): F[Boolean]

  def agreeFriend(uid: Int, fromGroup: Int, group: Int, messageBoxId: Int, mid: Int): F[Boolean]

  def chatLogIndex(id: Int, `type`: String, mid: Int): F[Int]

  /** 聊天文件上传
   *
   *  @param multipartInput
   *  @return
   */
  def uploadFile(multipartInput: MultipartInput): F[UploadResult]

  /** 聊天图片上传
   *
   *  @param multipartInput
   *  @return
   */
  def uploadImage(multipartInput: MultipartInput): F[UploadResult]

  /** 用户资料的头像更新
   *
   *  NOTE: 上传成功就已经更新了。
   *
   *  @param multipartInput
   *  @param mid
   *  @return
   */
  def updateAvatar(multipartInput: MultipartInput, mid: Int): F[UploadResult]

  /** 群组资料的头像上传
   *
   *  @param multipartInput
   *  @return
   */
  def uploadGroupAvatar(multipartInput: MultipartInput): F[UploadResult]

}
