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

import java.time._

import org.bitlap.zim._
import org.bitlap.zim.api.repository._
import org.bitlap.zim.api.service._
import org.bitlap.zim.domain.ZimError._
import org.bitlap.zim.domain._
import org.bitlap.zim.domain.model._
import org.bitlap.zim.infrastructure.properties.{MailConfigurationProperties, ZimConfigurationProperties}
import org.bitlap.zim.infrastructure.repository.RStream
import org.bitlap.zim.infrastructure.util._
import org.bitlap.zim.server.service.ws.WsService
import zio._
import zio.stream._

/** 用户服务
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 1.0
 */
final class UserServiceImpl(
  userRepository: UserRepository[RStream],
  groupRepository: GroupRepository[RStream],
  receiveRepository: ReceiveRepository[RStream],
  friendGroupRepository: FriendGroupRepository[RStream],
  friendGroupFriendRepository: FriendGroupFriendRepository[RStream],
  groupMemberRepository: GroupMemberRepository[RStream],
  addMessageRepository: AddMessageRepository[RStream]
) extends UserService[RStream] {

  override def leaveOutGroup(gid: Int, uid: Int): RStream[Boolean] =
    for {
      group <- groupRepository.findGroupById(gid)
      ret <-
        if (group == null) ZStream.fail(BusinessException())
        else {
          if (group.createId.equals(uid)) groupRepository.deleteGroup(gid).map(_ > 0)
          else groupMemberRepository.leaveOutGroup(GroupMember(gid, uid)).map(_ > 0)
        }
      _      <- LogUtil.infoS(s"leaveOutGroup gid=>$gid, uid=>$uid, group=>$group ret=>$ret")
      master <- findUserById(group.createId)
      _ <-
        if (ret && group.createId.equals(uid)) {
          groupMemberRepository.findGroupMembers(gid).flatMap { uid =>
            // group owner leave
            groupMemberRepository.leaveOutGroup(GroupMember(gid, uid)) *>
              ZStream.fromZIO(WsService.deleteGroup(master, group.groupName, gid, uid))
          }
        } else ZStream.succeed(1)
    } yield ret

  override def findGroupById(gid: Int): RStream[GroupList] = groupRepository.findGroupById(gid)

  override def addGroupMember(gid: Int, uid: Int, messageBoxId: Int): RStream[Boolean] =
    for {
      group <- groupRepository.findGroupById(gid)
      // 自己加自己的群，默认同意
      upRet <- updateAgree(messageBoxId, 1).when(group != null)
      _ <- LogUtil.infoS(
        s"addGroupMember gid=>$gid, uid=>$uid, group=>$group, messageBoxId=>$messageBoxId, upRet=>$upRet"
      )
      ret <- groupMemberRepository
        .addGroupMember(GroupMember(gid, uid))
        .map(_ == 1)
        .when(upRet && group.createId != uid)
    } yield ret

  override def addGroupMember(gid: Int, uid: Int): RStream[Boolean] =
    groupMemberRepository.addGroupMember(GroupMember(gid, uid)).map(_ == 1)

  override def removeFriend(friendId: Int, uId: Int): RStream[Boolean] =
    friendGroupFriendRepository.removeFriend(friendId, uId).map(_ > 0)

  override def updateAvatar(userId: Int, avatar: String): RStream[Boolean] =
    userRepository.updateAvatar(avatar, userId).map(_ == 1)

  override def updateUserInfo(user: User): RStream[Boolean] =
    userRepository.updateUserInfo(user.id, user).map(_ == 1).tap { r =>
      LogUtil.info("remove redis user") *> RedisCache.del(user.email).as(r)
    }

  override def updateUserStatus(status: String, uid: Int): RStream[Boolean] =
    userRepository.updateUserStatus(status, uid).map(_ == 1)

  override def changeGroup(groupId: Int, uId: Int, mId: Int): RStream[Boolean] =
    friendGroupFriendRepository.findUserGroup(uId, mId).flatMap { originRecordId =>
      friendGroupFriendRepository.changeGroup(groupId, originRecordId).map(_ == 1)
    }

  override def addFriend(
    mid: Int,
    mgid: Int,
    tid: Int,
    tgid: Int,
    messageBoxId: Int
  ): RStream[Boolean] =
    if (mid == tid) {
      ZStream.succeed(false)
    } else {
      val from = AddFriend(mid, mgid)
      val to   = AddFriend(tid, tgid)
      friendGroupFriendRepository
        .addFriend(from, to)
        .flatMap(c => if (c > 0) updateAgree(messageBoxId, 1) else ZStream.fail(BusinessException()))
    }

  override def createFriendGroup(groupname: String, uid: Int): RStream[Int] =
    friendGroupRepository.createFriendGroup(FriendGroup(0, uid, groupname))

  override def createGroup(groupList: GroupList): RStream[Int] =
    groupRepository.createGroupList(groupList).map(_.toInt)

  override def countUnHandMessage(uid: Int, agree: Option[Int]): RStream[Int] =
    addMessageRepository.countUnHandMessage(uid, agree)

  override def findAddInfo(uid: Int): RStream[AddInfo] =
    for {
      addMessage <- addMessageRepository.findAddInfo(uid)
      user       <- findUserById(addMessage.fromUid)
      addInfo = AddInfo(
        addMessage.id,
        addMessage.toUid,
        null,
        addMessage.fromUid,
        addMessage.groupId,
        addMessage.`type`,
        addMessage.remark,
        "",
        addMessage.agree,
        addMessage.time,
        user
      )
      _ <- LogUtil.infoS(s"findAddInfo uid=>$uid, addInfo=>$addInfo, addMessage=>$addMessage, user=>$user")
      addInfoCopy <-
        if (addMessage.`type` == 0) {
          ZStream.succeed(addInfo.copy(content = "申请添加你为好友"))
        } else {
          groupRepository.findGroupById(addMessage.groupId).map { group =>
            addInfo.copy(content = s"申请加入 '${group.groupName}' 群聊中!")
          }
        }
    } yield addInfoCopy

  override def updateAgree(messageBoxId: Int, agree: Int): RStream[Boolean] =
    addMessageRepository.updateAgree(messageBoxId, agree).map(_ == 1)

  override def refuseAddFriend(messageBoxId: Int, username: String, to: Int): RStream[Boolean] =
    ZStream.fromZIO(WsService.refuseAddFriend(messageBoxId, username, to))

  override def readFriendMessage(mine: Int, to: Int): RStream[Boolean] =
    receiveRepository.readMessage(mine, to, SystemConstant.FRIEND_TYPE).map(_ == 1)

  override def readGroupMessage(gId: Int, to: Int): RStream[Boolean] =
    receiveRepository.readMessage(gId, to, SystemConstant.GROUP_TYPE).map(_ == 1)

  override def saveAddMessage(addMessage: AddMessage): RStream[Int] =
    addMessageRepository.saveAddMessage(addMessage)

  override def countGroup(groupName: Option[String]): RStream[Int] =
    groupRepository.countGroup(groupName)

  override def findGroups(groupName: Option[String]): RStream[GroupList] =
    groupRepository.findGroups(groupName)

  override def countUser(username: Option[String], sex: Option[Int]): RStream[Int] =
    userRepository.countUser(username, sex)

  override def findUsers(username: Option[String], sex: Option[Int]): RStream[User] =
    userRepository.findUsers(username, sex)

  override def countHistoryMessage(uid: Int, mid: Int, `type`: String): RStream[Int] =
    `type` match {
      case SystemConstant.FRIEND_TYPE => receiveRepository.countHistoryMessage(Some(uid), Some(mid), Some(`type`))
      case SystemConstant.GROUP_TYPE  => receiveRepository.countHistoryMessage(None, Some(mid), Some(`type`))
      case _                          => ZStream.fail(BusinessException())
    }

  override def findHistoryMessage(
    user: User,
    mid: Int,
    `type`: String
  ): RStream[domain.ChatHistory] = {
    def userHistory(): RStream[ChatHistory] =
      // 单人聊天记录
      for {
        toUser  <- findUserById(mid)
        history <- receiveRepository.findHistoryMessage(Some(user.id), Some(mid), Some(`type`))
        newHistory =
          if (history.mid == mid) {
            domain.ChatHistory(
              history.mid,
              toUser.username,
              toUser.avatar,
              history.content,
              history.timestamp
            )
          } else {
            domain.ChatHistory(history.mid, user.username, user.avatar, history.content, history.timestamp)
          }
        _ <-
          LogUtil.infoS(
            s"findHistoryMessage.userHistory user=>$user, mid=>${mid}, type=>${`type`}, toUser=>$toUser, newHistory=>$newHistory"
          )

      } yield newHistory

    def groupHistory(): RStream[ChatHistory] =
      // 群聊天记录
      for {
        history <- receiveRepository.findHistoryMessage(None, Some(mid), Some(`type`))
        u       <- findUserById(history.fromid)
        newHistory =
          if (history.fromid.equals(user.id)) {
            domain.ChatHistory(user.id, user.username, user.avatar, history.content, history.timestamp)
          } else {
            domain.ChatHistory(history.mid, u.username, u.avatar, history.content, history.timestamp)
          }
        _ <-
          LogUtil.infoS(
            s"findHistoryMessage.groupHistory user=>$user, mid=>$mid, type=>${`type`}, toUser=>$u, newHistory=>$newHistory"
          )
      } yield newHistory
    `type` match {
      case SystemConstant.FRIEND_TYPE => userHistory()
      case SystemConstant.GROUP_TYPE  => groupHistory()
      case _                          => ZStream.fail(BusinessException())
    }
  }

  override def findOffLineMessage(uid: Int, status: Int): RStream[Receive] =
    receiveRepository.findOffLineMessage(uid, status)

  override def saveMessage(receive: Receive): RStream[Int] =
    receiveRepository.saveMessage(receive)

  override def updateSign(user: User): RStream[Boolean] =
    userRepository.updateSign(user.sign, user.id).map(_ == 1)

  override def activeUser(activeCode: String): RStream[Int] =
    userRepository.activeUser(activeCode)

  override def existEmail(email: String): RStream[Boolean] =
    userRepository.matchUser(email).map(_ != null)

  override def matchUser(user: User): RStream[User] =
    for {
      u <- userRepository.matchUser(user.email) // FIXME 前端加密传输
      isMath <- ZStream.fromZIO(
        SecurityUtil
          .matched(user.password, Option(u).map(_.password).getOrElse(""))
      )
      ret <-
        if (!isMath) {
          ZStream.fail(BusinessException(msg = SystemConstant.LOGIN_ERROR))
        } else if (u.status.equals("nonactivated")) {
          ZStream.fail(BusinessException(msg = SystemConstant.NON_ACTIVE))
        } else {
          ZStream.succeed(u)
        }
      _ <- LogUtil.infoS(s"matchUser user=>$user, u=>$u, isMath=>$isMath")
    } yield ret

  override def findUserByGroupId(gid: Int): RStream[User] =
    userRepository.findUserByGroupId(gid)

  override def findFriendGroupsById(uid: Int): RStream[FriendList] =
    for {
      groupList <- friendGroupRepository.findFriendGroupsById(uid).map { friendGroup =>
        FriendList(id = friendGroup.id, groupName = friendGroup.groupName, Nil)
      }
      users <- ZStream.fromZIO(userRepository.findUsersByFriendGroupIds(groupList.id).runCollect)
      _     <- LogUtil.infoS(s"findFriendGroupsById uid=>$uid, groupList=>$groupList, users=>$users")
    } yield groupList.copy(list = users.toList)

  override def findUserById(id: Int): RStream[User] =
    userRepository.findById(id)

  override def findGroupsById(id: Int): RStream[GroupList] =
    groupRepository.findGroupsById(id)

  override def saveUser(user: User): RStream[Boolean] =
    // TODO createFriendGroup不用返回Stream会好看点
    ZStream.fromZIO {
      for {
        activeCode <- UuidUtil.getUuid64
        pwd        <- SecurityUtil.encrypt(user.password)
        userCopy = user.copy(
          sign = "",
          active = activeCode,
          createDate = ZonedDateTime.now(),
          password = pwd.value
        )
        id <- userRepository.saveUser(userCopy).runHead
        _  <- createFriendGroup(SystemConstant.DEFAULT_GROUP_NAME, id.map(_.toInt).getOrElse(0)).runHead
        // 通过infra层访问配置
        port    <- ZIO.serviceWith[ZimConfigurationProperties](_.port)
        webHost <- ZIO.serviceWith[ZimConfigurationProperties](_.webHost)
        host = if (port == 80) webHost else s"${webHost}:${port}"
        _ <- MailServiceImpl
          .sendHtmlMail(
            userCopy.email,
            SystemConstant.SUBJECT,
            s"${userCopy.username} 请确定这是你本人注册的账号, http://$host/user/active/" + activeCode
          )
          .provide(MailServiceImpl.live, MailConfigurationProperties.live)
        _ <-
          LogUtil.info(
            s"saveUser uid=$id, user=>$user, activeCode=>$activeCode, userCopy=>$userCopy"
          )
      } yield true
    }.provideLayer(ZimConfigurationProperties.live)

}

object UserServiceImpl {

  // 测试用
  // TODO 构造注入的代价，以后少用
  lazy val live: URLayer[
    AddMessageRepository[RStream]
      with GroupMemberRepository[RStream]
      with FriendGroupFriendRepository[RStream]
      with FriendGroupRepository[RStream]
      with ReceiveRepository[RStream]
      with GroupRepository[RStream]
      with UserRepository[RStream],
    UserService[RStream]
  ] =
    ZLayer {
      for {
        user              <- ZIO.service[UserRepository[RStream]]
        group             <- ZIO.service[GroupRepository[RStream]]
        receive           <- ZIO.service[ReceiveRepository[RStream]]
        friendGroup       <- ZIO.service[FriendGroupRepository[RStream]]
        friendGroupFriend <- ZIO.service[FriendGroupFriendRepository[RStream]]
        groupMember       <- ZIO.service[GroupMemberRepository[RStream]]
        addMessage        <- ZIO.service[AddMessageRepository[RStream]]
      } yield new UserServiceImpl(
        userRepository = user,
        groupRepository = group,
        receiveRepository = receive,
        friendGroupRepository = friendGroup,
        friendGroupFriendRepository = friendGroupFriend,
        groupMemberRepository = groupMember,
        addMessageRepository = addMessage
      )
    }

}
