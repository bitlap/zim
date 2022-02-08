package org.bitlap.zim.server.application.impl

import org.bitlap.zim.domain
import org.bitlap.zim.domain._
import org.bitlap.zim.domain.model._
import org.bitlap.zim.domain.repository._
import org.bitlap.zim.server.application.UserApplication
import org.bitlap.zim.server.application.ws.wsService
import org.bitlap.zim.server.configuration.InfrastructureConfiguration
import org.bitlap.zim.server.repository.TangibleAddMessageRepository.ZAddMessageRepository
import org.bitlap.zim.server.repository.TangibleFriendGroupFriendRepository.ZFriendGroupFriendRepository
import org.bitlap.zim.server.repository.TangibleFriendGroupRepository.ZFriendGroupRepository
import org.bitlap.zim.server.repository.TangibleGroupMemberRepository.ZGroupMemberRepository
import org.bitlap.zim.server.repository.TangibleGroupRepository.ZGroupRepository
import org.bitlap.zim.server.repository.TangibleReceiveRepository.ZReceiveRepository
import org.bitlap.zim.server.repository.TangibleUserRepository.ZUserRepository
import org.bitlap.zim.server.util.{ LogUtil, SecurityUtil, UuidUtil }
import zio.stream.ZStream
import zio.{ stream, Has, URLayer, ZLayer }

import java.time.ZonedDateTime

/**
 * 用户服务
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
private final class UserService(
  userRepository: UserRepository,
  groupRepository: GroupRepository,
  receiveRepository: ReceiveRepository[Receive],
  friendGroupRepository: FriendGroupRepository,
  friendGroupFriendRepository: FriendGroupFriendRepository,
  groupMemberRepository: GroupMemberRepository,
  addMessageRepository: AddMessageRepository
) extends UserApplication {

  override def findById(id: Long): stream.Stream[Throwable, User] =
    for {
      user <- userRepository.findById(id)
      _ <- LogUtil.infoS(s"findById $id")
    } yield user

  override def leaveOutGroup(gid: Int, uid: Int): stream.Stream[Throwable, Boolean] =
    for {
      group <- groupRepository.findGroupById(gid)
      ret <-
        if (group == null) ZStream.succeed(false)
        else {
          if (group.createId.equals(uid)) groupRepository.deleteGroup(gid).map(_ == 1)
          else groupMemberRepository.leaveOutGroup(GroupMember(gid, uid)).map(_ == 1)
        }
      _ <- LogUtil.infoS(s"leaveOutGroup gid=>$gid, uid=>$uid, group=>$group")
      master <- findUserById(group.createId)
      _ <-
        if (ret && group.createId.equals(uid)) {
          groupMemberRepository.findGroupMembers(gid).flatMap { uid =>
            // group owner leave
            groupMemberRepository.leaveOutGroup(GroupMember(gid, uid)) *>
              ZStream.fromEffect(wsService.deleteGroup(master, group.groupName, gid, uid))
          }
        } else ZStream.succeed(1)
    } yield ret

  override def findGroupById(gid: Int): stream.Stream[Throwable, GroupList] = groupRepository.findGroupById(gid)

  override def addGroupMember(gid: Int, uid: Int, messageBoxId: Int): stream.Stream[Throwable, Boolean] =
    for {
      group <- groupRepository.findGroupById(gid)
      //自己加自己的群，默认同意
      upRet <-
        if (group != null) {
          updateAgree(messageBoxId, 1)
        } else ZStream.succeed(false)
      _ <- LogUtil.infoS(
        s"addGroupMember gid=>$gid, uid=>$uid, group=>$group, messageBoxId=>$messageBoxId, upRet=>$upRet"
      )
      ret <-
        if (upRet && group.createId != uid) {
          groupMemberRepository.addGroupMember(GroupMember(gid, uid)).map(_ == 1)
        } else ZStream.succeed(true)
    } yield ret

  override def addGroupMember(gid: Int, uid: Int): stream.Stream[Throwable, Boolean] =
    groupMemberRepository.addGroupMember(new GroupMember(gid, uid)).map(_ == 1)

  override def removeFriend(friendId: Int, uId: Int): stream.Stream[Throwable, Boolean] =
    friendGroupFriendRepository.removeFriend(friendId, uId).map(_ > 0)

  override def updateAvatar(userId: Int, avatar: String): stream.Stream[Throwable, Boolean] =
    userRepository.updateAvatar(avatar, userId).map(_ == 1)

  override def updateUserInfo(user: User): stream.Stream[Throwable, Boolean] =
    userRepository.updateUserInfo(user.id, user).map(_ == 1)

  override def updateUserStatus(status: String, uid: Int): stream.Stream[Throwable, Boolean] =
    userRepository.updateUserStatus(status, uid).map(_ == 1)

  override def changeGroup(groupId: Int, uId: Int, mId: Int): stream.Stream[Throwable, Boolean] =
    friendGroupFriendRepository.findUserGroup(uId, mId).flatMap { originRecordId =>
      friendGroupFriendRepository.changeGroup(groupId, originRecordId).map(_ == 1)
    }

  override def addFriend(
    mid: Int,
    mgid: Int,
    tid: Int,
    tgid: Int,
    messageBoxId: Int
  ): stream.Stream[Throwable, Boolean] = {
    val from = AddFriend(mid, mgid)
    val to = AddFriend(tid, tgid)
    friendGroupFriendRepository
      .addFriend(from, to)
      .flatMap(c => if (c > 0) updateAgree(messageBoxId, 1) else ZStream.succeed(false))
  }

  override def createFriendGroup(groupname: String, uid: Int): stream.Stream[Throwable, Int] =
    friendGroupRepository.createFriendGroup(FriendGroup(0, uid, groupname))

  override def createGroup(groupList: GroupList): stream.Stream[Throwable, Int] =
    groupRepository.createGroupList(groupList).map(_.toInt)

  override def countUnHandMessage(uid: Int, agree: Option[Int]): stream.Stream[Throwable, Int] =
    addMessageRepository.countUnHandMessage(uid, agree)

  override def findAddInfo(uid: Int): stream.Stream[Throwable, AddInfo] =
    for {
      addMessage <- addMessageRepository.findAddInfo(uid)
      user <- findUserById(addMessage.fromUid)
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

  override def updateAgree(messageBoxId: Int, agree: Int): stream.Stream[Throwable, Boolean] =
    addMessageRepository.updateAgree(agree, messageBoxId).map(_ == 1)

  override def refuseAddFriend(messageBoxId: Int, username: String, to: Int): stream.Stream[Throwable, Boolean] =
    ZStream.fromEffect(wsService.refuseAddFriend(messageBoxId, username, to))

  override def readFriendMessage(mine: Int, to: Int): stream.Stream[Throwable, Boolean] =
    receiveRepository.readMessage(mine, to, SystemConstant.FRIEND_TYPE).map(_ == 1)

  override def readGroupMessage(gId: Int, to: Int): stream.Stream[Throwable, Boolean] =
    receiveRepository.readMessage(gId, to, SystemConstant.GROUP_TYPE).map(_ == 1)

  override def saveAddMessage(addMessage: AddMessage): stream.Stream[Throwable, Int] =
    addMessageRepository.saveAddMessage(addMessage)

  override def countGroup(groupName: Option[String]): stream.Stream[Throwable, Int] =
    groupRepository.countGroup(groupName)

  override def findGroups(groupName: Option[String]): stream.Stream[Throwable, GroupList] =
    groupRepository.findGroups(groupName)

  override def countUser(username: Option[String], sex: Option[Int]): stream.Stream[Throwable, Int] =
    userRepository.countUser(username, sex)

  override def findUsers(username: Option[String], sex: Option[Int]): stream.Stream[Throwable, User] =
    userRepository.findUsers(username, sex)

  override def countHistoryMessage(uid: Int, mid: Int, `type`: String): stream.Stream[Throwable, Int] =
    `type` match {
      case SystemConstant.FRIEND_TYPE => receiveRepository.countHistoryMessage(Some(uid), Some(mid), Some(`type`))
      case SystemConstant.GROUP_TYPE  => receiveRepository.countHistoryMessage(None, Some(mid), Some(`type`))
      case _                          => ZStream.empty
    }

  override def findHistoryMessage(
    user: User,
    mid: Int,
    `type`: String
  ): stream.Stream[Throwable, domain.ChatHistory] = {
    def userHistory(): stream.Stream[Throwable, ChatHistory] =
      //单人聊天记录
      for {
        toUser <- findUserById(mid)
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
        _ <- LogUtil.infoS(
          s"findHistoryMessage.userHistory user=>$user, mid=>${mid}, type=>${`type`}, toUser=>$toUser, newHistory=>$newHistory"
        )

      } yield newHistory

    def groupHistory(): stream.Stream[Throwable, ChatHistory] =
      //群聊天记录
      for {
        history <- receiveRepository.findHistoryMessage(None, Some(mid), Some(`type`))
        u <- findUserById(history.fromid)
        newHistory =
          if (history.fromid.equals(user.id)) {
            domain.ChatHistory(user.id, user.username, user.avatar, history.content, history.timestamp)
          } else {
            domain.ChatHistory(history.mid, u.username, u.avatar, history.content, history.timestamp)
          }
        _ <- LogUtil.infoS(
          s"findHistoryMessage.groupHistory user=>$user, mid=>$mid, type=>${`type`}, toUser=>$u, newHistory=>$newHistory"
        )
      } yield newHistory
    `type` match {
      case SystemConstant.FRIEND_TYPE => userHistory()
      case SystemConstant.GROUP_TYPE  => groupHistory()
      case _                          => ZStream.empty
    }
  }

  override def findOffLineMessage(uid: Int, status: Int): stream.Stream[Throwable, Receive] =
    receiveRepository.findOffLineMessage(uid, status)

  override def saveMessage(receive: Receive): stream.Stream[Throwable, Int] =
    receiveRepository.saveMessage(receive)

  override def updateSing(user: User): stream.Stream[Throwable, Boolean] =
    if (user == null || user.sign == null) ZStream.succeed(false)
    else userRepository.updateSign(user.sign, user.id).map(_ == 1)

  override def activeUser(activeCode: String): stream.Stream[Throwable, Int] =
    if (activeCode == null || "".equals(activeCode)) ZStream.succeed(0)
    else userRepository.activeUser(activeCode)

  override def existEmail(email: String): stream.Stream[Throwable, Boolean] =
    if (email == null || "".equals(email)) ZStream.succeed(false)
    else userRepository.matchUser(email).map(_ != null)

  override def matchUser(user: User): stream.Stream[Throwable, User] = {
    if (user == null || user.email == null) {
      return ZStream.empty
    }
    for {
      u <- userRepository.matchUser(user.email) //TODO 前端加密传输
      isMath <- ZStream.fromEffect(
        SecurityUtil
          .matched(user.password, u.password)
      )
      ret <- if (u == null || !isMath) ZStream.empty else ZStream.succeed(u)
      _ <- LogUtil.infoS(s"matchUser user=>$user, u=>$u, isMath=>$isMath, ret=>$ret")
    } yield ret
  }

  override def findUserByGroupId(gid: Int): stream.Stream[Throwable, User] =
    userRepository.findUserByGroupId(gid)

  override def findFriendGroupsById(uid: Int): stream.Stream[Throwable, FriendList] = {
    val groupListStream = friendGroupRepository.findFriendGroupsById(uid).map { friendGroup =>
      FriendList(id = friendGroup.id, groupName = friendGroup.groupName, Nil)
    }
    for {
      groupList <- groupListStream
      users <- ZStream.fromEffect(userRepository.findUsersByFriendGroupIds(groupList.id).runCollect)
      _ <- LogUtil.infoS(s"findFriendGroupsById uid=>$uid, groupList=>$groupList, users=>$users")
    } yield groupList.copy(list = users.toList)
  }

  override def findUserById(id: Int): stream.Stream[Throwable, User] =
    userRepository.findById(id)

  override def findGroupsById(id: Int): stream.Stream[Throwable, GroupList] =
    groupRepository.findGroupsById(id)

  override def saveUser(user: User): stream.Stream[Throwable, Boolean] = {
    if (user == null || user.username == null || user.password == null || user.email == null) {
      return ZStream.succeed(false)
    }
    // TODO createFriendGroup不用返回Stream会好看点
    val zioRet = for {
      activeCode <- UuidUtil.getUuid64
      pwd <- SecurityUtil.encrypt(user.password)
      userCopy = user.copy(
        sign = "",
        active = activeCode,
        createDate = ZonedDateTime.now(),
        password = pwd.value
      )
      _ <- userRepository.saveUser(userCopy).runHead
      _ <- createFriendGroup(SystemConstant.DEFAULT_GROUP_NAME, userCopy.id).runHead
      // 通过infra层访问配置
      zimConf <- InfrastructureConfiguration.zimConfigurationProperties
      mailConf <- InfrastructureConfiguration.mailConfigurationProperties
      _ <- MailService
        .sendHtmlMail(
          userCopy.email,
          SystemConstant.SUBJECT,
          s"${userCopy.username} 请确定这是你本人注册的账号, http://${zimConf.interface}:${zimConf.port}/user/active/" + activeCode
        )
        .provideLayer(MailService.make(mailConf))
      _ <- LogUtil.info(
        s"saveUser user=>$user, activeCode=>$activeCode, userCopy=>$userCopy, zimConf=>$zimConf, mailConf=>$mailConf"
      )
    } yield true
    ZStream.fromEffect(zioRet)
  }

}

object UserService {

  type ZUserApplication = Has[UserApplication]

  def apply(
    userRepository: UserRepository,
    groupRepository: GroupRepository,
    receiveRepository: ReceiveRepository[Receive],
    friendGroupRepository: FriendGroupRepository,
    friendGroupFriendRepository: FriendGroupFriendRepository,
    groupMemberRepository: GroupMemberRepository,
    addMessageRepository: AddMessageRepository
  ): UserApplication =
    new UserService(
      userRepository,
      groupRepository,
      receiveRepository,
      friendGroupRepository,
      friendGroupFriendRepository,
      groupMemberRepository,
      addMessageRepository
    )

  // 测试用
  // TODO 构造注入的代价，以后少用
  val live: URLayer[
    ZUserRepository with ZGroupRepository with ZReceiveRepository with ZFriendGroupRepository with ZFriendGroupFriendRepository with ZFriendGroupFriendRepository with ZGroupMemberRepository with ZAddMessageRepository,
    ZUserApplication
  ] =
    ZLayer.fromServices[UserRepository, GroupRepository, ReceiveRepository[
      Receive
    ], FriendGroupRepository, FriendGroupFriendRepository, GroupMemberRepository, AddMessageRepository, UserApplication] {
      (a, b, c, d, e, f, g) =>
        UserService(a, b, c, d, e, f, g)
    }

}
