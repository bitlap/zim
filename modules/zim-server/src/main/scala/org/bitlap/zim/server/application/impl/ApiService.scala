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

package org.bitlap.zim.server.application.impl

import org.bitlap.zim.domain._
import org.bitlap.zim.domain.input.{ FriendGroupInput, GroupInput, RegisterUserInput, UpdateUserInput, UserSecurity }
import org.bitlap.zim.domain.model.{ GroupList, Receive, User }
import org.bitlap.zim.server.application.impl.UserService.ZUserApplication
import org.bitlap.zim.server.application.{ ApiApplication, UserApplication }
import org.bitlap.zim.server.util.{ FileUtil, LogUtil, SecurityUtil }
import org.bitlap.zim.tapir.MultipartInput
import zio.stream.ZStream
import zio.{ stream, Has, IO, TaskLayer, URLayer, ZLayer }

import java.time.ZonedDateTime

/**
 * @author 梦境迷离
 * @since 2022/1/8
 * @version 1.0
 */
private final class ApiService(userApplication: UserApplication) extends ApiApplication {

  override def findById(id: Long): stream.Stream[Throwable, User] = userApplication.findById(id)

  override def existEmail(email: String): stream.Stream[Throwable, Boolean] = userApplication.existEmail(email)

  override def findUserById(id: Int): stream.Stream[Throwable, User] = userApplication.findUserById(id)

  override def updateInfo(user: UpdateUserInput): stream.Stream[Throwable, Boolean] = {
    def check(): Boolean =
      user.password == null || user.password.isEmpty || user.oldpwd == null || user.oldpwd.isEmpty

    for {
      u <- userApplication.findUserById(user.id)
      sex = if (user.sex.equals("nan")) 1 else 0
      pwdCheck <- ZStream.fromEffect(SecurityUtil.matched(user.oldpwd.getOrElse(""), u.password))
      newPwd <- ZStream.fromEffect(SecurityUtil.encrypt(user.password.getOrElse("")))
      checkAndUpdate <-
        if (check()) {
          userApplication.updateUserInfo(u.copy(sex = sex, sign = user.sign, username = user.username))
        } else if (!pwdCheck) {
          ZStream.succeed(false)
        } else {
          userApplication
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

  override def login(user: UserSecurity.UserSecurityInfo): stream.Stream[Throwable, User] =
    userApplication.matchUser(User(user.id, user.email, user.password))

  override def init(userId: Int): stream.Stream[Throwable, FriendAndGroupInfo] = {
    val ret = for {
      user <- userApplication.findUserById(userId).runHead
      _ <- LogUtil.info(s"init user=>$user")
      friends <- userApplication.findFriendGroupsById(userId).runCollect
      _ <- LogUtil.info(s"init friends=>$friends")
      groups <- userApplication.findGroupsById(userId).runCollect
      _ <- LogUtil.info(s"init groups=>$groups")
      resp = FriendAndGroupInfo(
        // 怎么区分主动刷新？这样如果主动刷新会将隐式重置为在线
        mine = user.fold[User](null)(u => u.copy(status = SystemConstant.status.ONLINE)),
        friend = friends.toList,
        group = groups.toList
      )
      _ <- LogUtil.info(s"init ret=>$resp")
    } yield resp
    ZStream.fromEffect(ret)
  }

  override def getOffLineMessage(mid: Int): stream.Stream[Throwable, Receive] = {
    val groupReceives = for {
      gId <- userApplication.findGroupsById(mid).map(_.id)
      groupMsg <- userApplication.findOffLineMessage(gId, 0)
      _ <- LogUtil.infoS(s"getOffLineMessage userId=>$mid gId=>$gId groupMsg=>$groupMsg")
    } yield groupMsg

    val receives = groupReceives.filter(_.fromid != mid) ++ userApplication.findOffLineMessage(mid, 0)
    receives.flatMap { receive =>
      userApplication
        .findUserById(receive.fromid)
        .map(user => receive.copy(username = user.username, avatar = user.avatar))
    }
  }

  override def register(user: RegisterUserInput): stream.Stream[Throwable, Boolean] =
    userApplication.saveUser(
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

  override def activeUser(activeCode: String): stream.Stream[Throwable, Int] =
    userApplication
      .activeUser(activeCode)
      .map(i => if (i > 0) 1 else 0)

  override def createUserGroup(friendGroup: FriendGroupInput): stream.Stream[Throwable, Int] =
    userApplication.createFriendGroup(friendGroup.groupname, friendGroup.uid)

  override def createGroup(groupInput: GroupInput): stream.Stream[Throwable, Int] = {
    val ret = userApplication.createGroup(
      GroupList(id = 0, createId = groupInput.createId, groupName = groupInput.groupname, avatar = groupInput.avatar)
    )
    ret.flatMap(f =>
      if (f > 0) {
        userApplication.addGroupMember(f, groupInput.createId).map(b => if (b) f else -1)
      } else ZStream.succeed(-1)
    )

  }

  override def getMembers(id: Int): stream.Stream[Throwable, FriendList] = {
    val usersZio = userApplication
      .findUserByGroupId(id)
      .runCollect
      .map(f => FriendList(id = 0, groupName = "", list = f.toList))
    ZStream.fromEffect(usersZio)
  }

  override def updateSign(sign: String, mid: Int): stream.Stream[Throwable, Boolean] =
    userApplication.findUserById(mid).flatMap(user => userApplication.updateSing(user.copy(sign = sign)))

  override def leaveOutGroup(groupId: Int, mid: Int): stream.Stream[Throwable, Int] = {
    val masterIdStream = userApplication.findGroupById(groupId).map(_.createId)
    for {
      masterId <- masterIdStream
      status <- userApplication.leaveOutGroup(groupId, mid)
      _ <- LogUtil.infoS(s"leaveOutGroup groupId=>$groupId, mid=>$mid, masterId=$masterId, status=>$status")
    } yield if (status) masterId else -1
  }

  override def removeFriend(friendId: Int, mid: Int): stream.Stream[Throwable, Boolean] =
    userApplication.removeFriend(friendId, mid)

  override def changeGroup(groupId: Int, userId: Int, mid: Int): stream.Stream[Throwable, Boolean] =
    userApplication.changeGroup(groupId, userId, mid)

  override def refuseFriend(messageBoxId: Int, to: Int, username: String): stream.Stream[Throwable, Boolean] =
    userApplication.refuseAddFriend(messageBoxId, username, to)

  override def agreeFriend(
    uid: Int,
    fromGroup: Int,
    group: Int,
    messageBoxId: Int,
    mid: Int
  ): stream.Stream[Throwable, Boolean] =
    userApplication.addFriend(mid, group, uid, fromGroup, messageBoxId)

  override def chatLogIndex(id: Int, `type`: String, mid: Int): stream.Stream[Throwable, Int] =
    userApplication
      .countHistoryMessage(mid, id, `type`)
      .map(pages => if (pages < SystemConstant.SYSTEM_PAGE) pages else pages / SystemConstant.SYSTEM_PAGE + 1)

  override def chatLog(id: Int, `type`: String, page: Int, mid: Int): IO[Throwable, List[ChatHistory]] = {
    val ret = for {
      list <- userApplication
        .findUserById(mid)
        .flatMap { u =>
          userApplication.findHistoryMessage(u, id, `type`)
        }
        .runCollect
      pageRet = list
        .slice(
          SystemConstant.SYSTEM_PAGE * (page - 1),
          math.min(SystemConstant.SYSTEM_PAGE * page, list.size)
        )
        .toList
    } yield pageRet
    ret
  }

  override def findAddInfo(uid: Int, page: Int): IO[Throwable, ResultPageSet[AddInfo]] =
    for {
      list <- userApplication.findAddInfo(uid).runCollect
      listRet = list.slice(
        SystemConstant.ADD_MESSAGE_PAGE * (page - 1),
        math.min(SystemConstant.ADD_MESSAGE_PAGE * page, list.size)
      )
      countIO <- userApplication.countUnHandMessage(uid, None).runHead
      count = countIO.getOrElse(0)
    } yield {
      val pages = calculatePages(count, SystemConstant.ADD_MESSAGE_PAGE)
      ResultPageSet(listRet.toList, pages)
    }

  override def findUsers(name: Option[String], sex: Option[Int], page: Int): IO[Throwable, ResultPageSet[User]] =
    for {
      list <- userApplication.findUsers(name, sex).runCollect
      listRet = list.slice(
        SystemConstant.USER_PAGE * (page - 1),
        math.min(SystemConstant.USER_PAGE * page, list.size)
      )
      countIO <- userApplication.countUser(name, sex).runHead
      count = countIO.getOrElse(0)
    } yield {
      val pages = calculatePages(count, SystemConstant.USER_PAGE)
      ResultPageSet(listRet.toList, pages)
    }

  override def findGroups(name: Option[String], page: Int): IO[Throwable, ResultPageSet[GroupList]] =
    for {
      list <- userApplication.findGroups(name).runCollect
      listRet = list.slice(
        SystemConstant.USER_PAGE * (page - 1),
        math.min(SystemConstant.USER_PAGE * page, list.size)
      )
      countIO <- userApplication.countGroup(name).runHead
      count = countIO.getOrElse(0)
    } yield {
      val pages = calculatePages(count, SystemConstant.USER_PAGE)
      ResultPageSet(listRet.toList, pages)
    }

  override def findMyGroups(createId: Int, page: Int): IO[Throwable, ResultPageSet[GroupList]] =
    for {
      list <- userApplication.findGroupsById(createId).runCollect
      listFilter = list.filter(x => x.createId.equals(createId))
      listRet = listFilter.slice(
        SystemConstant.USER_PAGE * (page - 1),
        math.min(SystemConstant.USER_PAGE * page, listFilter.size)
      )
      count = listRet.size
    } yield {
      val pages = calculatePages(count, SystemConstant.USER_PAGE)
      ResultPageSet(listRet.toList, pages)
    }

  private def calculatePages(count: Int, PAGE: Int): Int =
    if (count < PAGE) 1
    else {
      if (count % PAGE == 0) count / PAGE
      else count / PAGE + 1
    }

  override def uploadFile(multipartInput: MultipartInput): stream.Stream[ZimError, UploadResult] = {
    if (multipartInput.file.name.isEmpty) {
      return ZStream.empty
    }
    // TODO 虚拟路径，目前为"/"，下同
    val fileIO = FileUtil
      .upload(SystemConstant.FILE_PATH, "/", multipartInput.file)
      .map(src => UploadResult(src = src, name = multipartInput.getFileName))
    ZStream.fromEffect(fileIO)
  }

  override def updateAvatar(multipartInput: MultipartInput, mid: Int): stream.Stream[Throwable, UploadResult] = {
    if (multipartInput.file.name.isEmpty) {
      return ZStream.empty
    }
    val fileIO = FileUtil.upload(SystemConstant.AVATAR_PATH, multipartInput.file).flatMap { src =>
      userApplication.updateAvatar(mid, src).runHead.as(UploadResult(src = src, name = multipartInput.getFileName))
    }
    ZStream.fromEffect(fileIO)
  }

  override def uploadGroupAvatar(multipartInput: MultipartInput): stream.Stream[Throwable, UploadResult] = {
    if (multipartInput.file.name.isEmpty) {
      return ZStream.empty
    }
    val fileIO = FileUtil
      .upload(SystemConstant.GROUP_AVATAR_PATH, multipartInput.file)
      .map(src => UploadResult(src = src, name = multipartInput.getFileName))
    ZStream.fromEffect(fileIO)
  }

  override def uploadImage(multipartInput: MultipartInput): stream.Stream[Throwable, UploadResult] = {
    if (multipartInput.file.name.isEmpty) {
      return ZStream.empty
    }
    val fileIO = FileUtil
      .upload(SystemConstant.IMAGE_PATH, "/", multipartInput.file)
      .map(src => UploadResult(src = src, name = multipartInput.getFileName))
    ZStream.fromEffect(fileIO)
  }
}

object ApiService {

  type ZApiApplication = Has[ApiApplication]

  def apply(userApplication: UserApplication): ApiApplication = new ApiService(userApplication)

  val live: URLayer[ZUserApplication, ZApiApplication] =
    ZLayer.fromService[UserApplication, ApiApplication](ApiService(_))

  def make(
    userApplicationLayer: TaskLayer[ZUserApplication]
  ): TaskLayer[ZApiApplication] = userApplicationLayer >>> live

}
