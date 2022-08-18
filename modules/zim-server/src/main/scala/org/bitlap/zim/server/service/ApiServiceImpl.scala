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

package org.bitlap.zim.server.service

import org.bitlap.zim.api.MultipartInput
import org.bitlap.zim.api.service.{ ApiService, PaginationApiService, UserService }
import org.bitlap.zim.domain.ZimError.BusinessException
import org.bitlap.zim.domain._
import org.bitlap.zim.domain.input._
import org.bitlap.zim.domain.model.{ GroupList, Receive, User }
import org.bitlap.zim.infrastructure.repository.RStream
import org.bitlap.zim.infrastructure.util.{ LogUtil, SecurityUtil }
import org.bitlap.zim.server.FileUtil
import org.bitlap.zim.server.service.UserServiceImpl.ZUserApplication
import zio.stream.ZStream
import zio.{ stream, Has, IO, Task, TaskLayer, URLayer, ZLayer }

import java.time.ZonedDateTime

/** @author
 *    梦境迷离
 *  @since 2022/1/8
 *  @version 1.0
 */
class ApiServiceImpl(userService: UserService[RStream]) extends ApiService[RStream] with PaginationApiService[Task] {

  override def existEmail(email: String): RStream[Boolean] =
    if (email.isEmpty) ZStream.fail(BusinessException(msg = SystemConstant.PARAM_ERROR))
    else userService.existEmail(email)

  override def findUserById(id: Int): RStream[User] = userService.findUserById(id)

  override def updateInfo(user: UpdateUserInput): RStream[Boolean] = {
    def check(): Boolean = user.password.isEmpty || user.oldpwd.isEmpty

    for {
      u <- userService.findUserById(user.id)
      sex = if (user.sex.equals("nan")) 1 else 0
      pwdCheck <- ZStream.fromEffect(SecurityUtil.matched(user.oldpwd.getOrElse(""), u.password))
      newPwd   <- ZStream.fromEffect(SecurityUtil.encrypt(user.password.getOrElse("")))
      checkAndUpdate <-
        if (check()) {
          userService.updateUserInfo(u.copy(sex = sex, sign = user.sign, username = user.username))
        } else if (!pwdCheck) {
          ZStream.fail(BusinessException(msg = "旧密码不正确"))
        } else {
          userService
            .updateUserInfo(
              u.copy(
                password = newPwd.value,
                sex = sex,
                sign = user.sign,
                username = user.username
              )
            )

        }
    } yield checkAndUpdate
  }

  override def login(user: UserSecurity.UserSecurityInfo): RStream[User] =
    if (user.email.isEmpty) {
      ZStream.fail(BusinessException(msg = SystemConstant.PARAM_ERROR))
    } else userService.matchUser(User(user.id, user.email, user.password))

  override def init(userId: Int): RStream[FriendAndGroupInfo] =
    ZStream.fromEffect {
      for {
        user    <- userService.findUserById(userId).runHead
        _       <- LogUtil.info(s"init user=>$user")
        friends <- userService.findFriendGroupsById(userId).runCollect
        _       <- LogUtil.info(s"init friends=>$friends")
        groups  <- userService.findGroupsById(userId).runCollect
        _       <- LogUtil.info(s"init groups=>$groups")
        resp = FriendAndGroupInfo(
          // 怎么区分主动刷新？这样如果主动刷新会将隐式重置为在线
          mine = user.fold[User](null)(u => u.copy(status = SystemConstant.status.ONLINE)),
          friend = friends.toList,
          group = groups.toList
        )
        _ <- LogUtil.info(s"init ret=>$resp")
      } yield resp
    }

  override def getOffLineMessage(mid: Int): RStream[Receive] = {
    val groupReceives = for {
      gId      <- userService.findGroupsById(mid).map(_.id)
      groupMsg <- userService.findOffLineMessage(gId, 0)
      _        <- LogUtil.infoS(s"getOffLineMessage userId=>$mid gId=>$gId groupMsg=>$groupMsg")
    } yield groupMsg

    val receives = groupReceives.filter(_.fromid != mid) ++ userService.findOffLineMessage(mid, 0)
    receives.flatMap { receive =>
      userService
        .findUserById(receive.fromid)
        .map(user => receive.copy(username = user.username, avatar = user.avatar))
    }
  }

  override def register(user: RegisterUserInput): RStream[Boolean] =
    if (user.username.isEmpty || user.password.isEmpty) {
      ZStream.fail(BusinessException(msg = SystemConstant.PARAM_ERROR))
    } else if (!ApiServiceImpl.EMAIL_REGEX.matches(user.email)) {
      ZStream.fail(BusinessException(msg = "邮箱格式不正确"))
    } else {
      userService.saveUser(
        User(
          id = 0,
          username = user.username,
          password = user.password,
          sign = null,
          avatar = null,
          email = user.email,
          createDate = ZonedDateTime.now(),
          sex = 0,
          status = null,
          active = null
        )
      )
    }

  override def activeUser(activeCode: String): RStream[Int] =
    userService
      .activeUser(activeCode)
      .map(i => if (i > 0) 1 else 0)

  override def createUserGroup(friendGroup: FriendGroupInput): RStream[Int] =
    userService.createFriendGroup(friendGroup.groupname, friendGroup.uid)

  override def createGroup(groupInput: GroupInput): RStream[Int] =
    userService
      .createGroup(
        GroupList(id = 0, createId = groupInput.createId, groupName = groupInput.groupname, avatar = groupInput.avatar)
      )
      .flatMap(f =>
        if (f > 0) {
          for {
            s   <- userService.addGroupMember(f, groupInput.createId)
            ret <- if (s) ZStream.succeed(f) else ZStream.fail(BusinessException())
          } yield ret
        } else ZStream.fail(BusinessException())
      )

  override def getMembers(id: Int): RStream[FriendList] =
    ZStream.fromEffect {
      userService
        .findUserByGroupId(id)
        .runCollect
        .map(f => FriendList(id = 0, groupName = "", list = f.toList))
    }

  override def updateSign(sign: String, mid: Int): RStream[Boolean] =
    userService.findUserById(mid).flatMap(user => userService.updateSign(user.copy(sign = sign)))

  override def leaveOutGroup(groupId: Int, mid: Int): RStream[Int] =
    for {
      masterId <- userService.findGroupById(groupId).map(_.createId)
      status   <- userService.leaveOutGroup(groupId, mid)
      _        <- LogUtil.infoS(s"leaveOutGroup groupId=>$groupId, mid=>$mid, masterId=$masterId, status=>$status")
    } yield if (status) masterId else -1

  override def removeFriend(friendId: Int, mid: Int): RStream[Boolean] =
    userService.removeFriend(friendId, mid)

  override def changeGroup(groupId: Int, userId: Int, mid: Int): RStream[Boolean] =
    userService.changeGroup(groupId, userId, mid)

  override def refuseFriend(messageBoxId: Int, to: Int, username: String): RStream[Boolean] =
    userService.refuseAddFriend(messageBoxId, username, to)

  override def agreeFriend(
    uid: Int,
    fromGroup: Int,
    group: Int,
    messageBoxId: Int,
    mid: Int
  ): RStream[Boolean] =
    userService.addFriend(mid, group, uid, fromGroup, messageBoxId)

  override def chatLogIndex(id: Int, `type`: String, mid: Int): RStream[Int] =
    userService
      .countHistoryMessage(mid, id, `type`)
      .map(pages => if (pages < SystemConstant.SYSTEM_PAGE) pages else pages / SystemConstant.SYSTEM_PAGE + 1)

  override def chatLog(id: Int, `type`: String, page: Int, mid: Int): IO[Throwable, ResultPageSet[ChatHistory]] =
    for {
      list <- userService
        .findUserById(mid)
        .flatMap { u =>
          userService.findHistoryMessage(u, id, `type`)
        }
        .runCollect
      pageRet = list
        .slice(
          SystemConstant.SYSTEM_PAGE * (page - 1),
          math.min(SystemConstant.SYSTEM_PAGE * page, list.size)
        )
    } yield {
      val pages = calculatePages(list.size, SystemConstant.SYSTEM_PAGE)
      ResultPageSet(pageRet.toList, pages)
    }

  override def findAddInfo(uid: Int, page: Int): IO[Throwable, ResultPageSet[AddInfo]] =
    for {
      list <- userService.findAddInfo(uid).runCollect
      listRet = list.slice(
        SystemConstant.ADD_MESSAGE_PAGE * (page - 1),
        math.min(SystemConstant.ADD_MESSAGE_PAGE * page, list.size)
      )
    } yield {
      val pages = calculatePages(list.size, SystemConstant.ADD_MESSAGE_PAGE)
      ResultPageSet(listRet.toList, pages)
    }

  override def findUsers(name: Option[String], sex: Option[Int], page: Int): IO[Throwable, ResultPageSet[User]] =
    for {
      list <- userService.findUsers(name, sex).runCollect
      listRet = list.slice(
        SystemConstant.USER_PAGE * (page - 1),
        math.min(SystemConstant.USER_PAGE * page, list.size)
      )
    } yield {
      val pages = calculatePages(list.size, SystemConstant.USER_PAGE)
      ResultPageSet(listRet.toList, pages)
    }

  override def findGroups(name: Option[String], page: Int): IO[Throwable, ResultPageSet[GroupList]] =
    for {
      list <- userService.findGroups(name).runCollect
      listRet = list.slice(
        SystemConstant.USER_PAGE * (page - 1),
        math.min(SystemConstant.USER_PAGE * page, list.size)
      )
    } yield {
      val pages = calculatePages(list.size, SystemConstant.USER_PAGE)
      ResultPageSet(listRet.toList, pages)
    }

  override def findMyGroups(createId: Int, page: Int): IO[Throwable, ResultPageSet[GroupList]] =
    for {
      list <- userService.findGroupsById(createId).runCollect
      listFilter = list.filter(x => x.createId.equals(createId))
      listRet = listFilter.slice(
        SystemConstant.USER_PAGE * (page - 1),
        math.min(SystemConstant.USER_PAGE * page, listFilter.size)
      )
    } yield {
      val pages = calculatePages(listFilter.size, SystemConstant.USER_PAGE)
      ResultPageSet(listRet.toList, pages)
    }

  @inline private def calculatePages(count: Int, PAGE: Int): Int =
    if (count < PAGE) 1
    else {
      if (count % PAGE == 0) count / PAGE
      else count / PAGE + 1
    }

  override def uploadFile(multipartInput: MultipartInput): stream.Stream[ZimError, UploadResult] =
    if (multipartInput.file.name.isEmpty) {
      ZStream.fail(BusinessException(msg = SystemConstant.PARAM_ERROR))
    } else {
      // TODO 虚拟路径，目前为"/"，下同
      val fileIO = FileUtil
        .upload(SystemConstant.FILE_PATH, "/", multipartInput.file)
        .map(src => UploadResult(src = src, name = multipartInput.getFileName))
      ZStream.fromEffect(fileIO)
    }

  override def updateAvatar(multipartInput: MultipartInput, mid: Int): RStream[UploadResult] =
    if (multipartInput.file.name.isEmpty) {
      ZStream.fail(BusinessException(msg = SystemConstant.PARAM_ERROR))
    } else {
      val fileIO = FileUtil.upload(SystemConstant.AVATAR_PATH, multipartInput.file).flatMap { src =>
        userService.updateAvatar(mid, src).runHead.as(UploadResult(src = src, name = multipartInput.getFileName))
      }
      ZStream.fromEffect(fileIO)
    }

  override def uploadGroupAvatar(multipartInput: MultipartInput): RStream[UploadResult] =
    if (multipartInput.file.name.isEmpty) {
      ZStream.fail(BusinessException(msg = SystemConstant.PARAM_ERROR))
    } else {
      val fileIO = FileUtil
        .upload(SystemConstant.GROUP_AVATAR_PATH, multipartInput.file)
        .map(src => UploadResult(src = src, name = multipartInput.getFileName))
      ZStream.fromEffect(fileIO)
    }

  override def uploadImage(multipartInput: MultipartInput): RStream[UploadResult] =
    if (multipartInput.file.name.isEmpty) {
      ZStream.fail(BusinessException(msg = SystemConstant.PARAM_ERROR))
    } else {
      val fileIO = FileUtil
        .upload(SystemConstant.IMAGE_PATH, "/", multipartInput.file)
        .map(src => UploadResult(src = src, name = multipartInput.getFileName))
      ZStream.fromEffect(fileIO)
    }
}

object ApiServiceImpl {

  private[service] val EMAIL_REGEX = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$".r

  type ZApiApplication = Has[APIService]

  val live: URLayer[ZUserApplication, ZApiApplication] =
    ZLayer.fromService[UserService[RStream], APIService](p => new ApiServiceImpl(p) with PaginationApiService[Task])

  def make(
    userApplicationLayer: TaskLayer[ZUserApplication]
  ): TaskLayer[ZApiApplication] = userApplicationLayer >>> live

}
