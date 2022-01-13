package org.bitlap.zim.application

import org.bitlap.zim.configuration.SystemConstant
import org.bitlap.zim.configuration.properties.ZimConfigurationProperties
import org.bitlap.zim.domain.model.{ AddFriend, AddMessage, FriendGroup, GroupList, GroupMember, Receive, User }
import org.bitlap.zim.domain.{ AddInfo, ChatHistory, FriendList }
import org.bitlap.zim.repository.{
  AddMessageRepository,
  FriendGroupFriendRepository,
  FriendGroupRepository,
  GroupMemberRepository,
  GroupRepository,
  ReceiveRepository,
  UserRepository
}
import org.bitlap.zim.util.{ SecurityUtil, UuidUtil }
import zio.crypto.hash.{ Hash, MessageDigest }
import zio.stream.ZStream
import zio.{ stream, Has, ZIO }
import java.time.ZonedDateTime
import scala.collection.mutable.ListBuffer
import org.bitlap.zim.application.ws.wsService

/**
 * 用户服务
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
private final class UserService(
  userRepository: UserRepository[User],
  groupRepository: GroupRepository[GroupList],
  receiveRepository: ReceiveRepository[Receive],
  friendGroupRepository: FriendGroupRepository[FriendGroup],
  friendGroupFriendRepository: FriendGroupFriendRepository[AddFriend],
  groupMemberRepository: GroupMemberRepository[GroupMember],
  addMessageRepository: AddMessageRepository[AddMessage],
  mailService: MailService,
  zimConfigurationProperties: ZimConfigurationProperties
) extends UserApplication {

  override def findById(id: Long): stream.Stream[Throwable, User] =
    userRepository.findById(id)

  override def leaveOutGroup(gid: Int, uid: Int): stream.Stream[Throwable, Boolean] =
    for {
      group <- groupRepository.findGroupById(gid)
      master <- findUserById(group.createId)
      ret <-
        if (group == null) ZStream.succeed(false)
        else {
          if (group.createId.equals(uid)) groupRepository.deleteGroup(gid).map(_ == 1)
          else groupMemberRepository.leaveOutGroup(GroupMember(gid, uid)).map(_ == 1)
        }
      _ <-
        if (ret && group.createId.equals(uid)) {
          groupMemberRepository.findGroupMembers(gid).flatMap { uid =>
            // group owner leave
            groupMemberRepository.leaveOutGroup(GroupMember(gid, uid)) *>
              ZStream.fromEffect(wsService.deleteGroup(master, group.groupname, gid, uid))
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
          updateAddMessage(messageBoxId, 1)
        } else ZStream.succeed(false)
      ret <-
        if (upRet && group.createId != uid) {
          groupMemberRepository.addGroupMember(GroupMember(gid, uid)).map(_ == 1)
        } else ZStream.succeed(true)
    } yield ret

  override def addGroupMember(gid: Int, uid: Int): stream.Stream[Throwable, Boolean] =
    groupMemberRepository.addGroupMember(new GroupMember(gid, uid)).map(_ == 1)

  override def removeFriend(friendId: Int, uId: Int): stream.Stream[Throwable, Boolean] =
    friendGroupFriendRepository.removeFriend(friendId, uId).map(_ == 1)

  override def updateAvatar(userId: Int, avatar: String): stream.Stream[Throwable, Boolean] =
    userRepository.updateAvatar(avatar, userId).map(_ == 1)

  override def updateUserInfo(user: User): stream.Stream[Throwable, Boolean] =
    userRepository.updateUserInfo(user.id, user).map(_ == 1)

  override def updateUserStatus(user: User): stream.Stream[Throwable, Boolean] =
    userRepository.updateUserStatus(user.status, user.id).map(_ == 1)

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
      .flatMap(c => if (c == 1) updateAddMessage(messageBoxId, 1) else ZStream.succeed(false))
      .onError(_ => ZIO.succeed(false))
  }

  override def createFriendGroup(groupname: String, uid: Int): stream.Stream[Throwable, Int] =
    friendGroupRepository.createFriendGroup(FriendGroup(0, uid, groupname))

  override def createGroup(groupList: GroupList): stream.Stream[Throwable, Int] =
    groupRepository.createGroupList(groupList).map(_.toInt)

  override def countUnHandMessage(uid: Int, agree: Int): stream.Stream[Throwable, Int] =
    addMessageRepository.countUnHandMessage(uid, agree)

  override def findAddInfo(uid: Int): stream.Stream[Throwable, AddInfo] =
    for {
      addMessage <- addMessageRepository.findAddInfo(uid)
      group <- groupRepository.findGroupById(addMessage.groupId)
      user <- findUserById(addMessage.fromUid)
      addInfo = AddInfo(
        addMessage.id,
        addMessage.toUid,
        null,
        addMessage.fromUid,
        addMessage.groupId,
        addMessage.`type`,
        addMessage.remark,
        null,
        addMessage.agree,
        addMessage.time,
        user
      )
      addInfoCopy =
        if (addMessage.`type` == 0) {
          addInfo.copy(content = "申请添加你为好友")
        } else {
          if (group != null) addInfo.copy(content = s"申请加入 '${group.groupname}' 群聊中!") else addInfo
        }
    } yield addInfoCopy

  override def updateAddMessage(messageBoxId: Int, agree: Int): stream.Stream[Throwable, Boolean] =
    addMessageRepository.updateAddMessage(AddMessage(agree = agree, id = messageBoxId)).map(_ == 1)

  override def refuseAddFriend(messageBoxId: Int, user: User, to: Int): stream.Stream[Throwable, Boolean] =
    ZStream.fromEffect(wsService.refuseAddFriend(messageBoxId, user, to))

  override def readFriendMessage(mine: Int, to: Int): stream.Stream[Throwable, Boolean] =
    receiveRepository.readMessage(mine, to, SystemConstant.FRIEND_TYPE).map(_ == 1)

  override def readGroupMessage(gId: Int, to: Int): stream.Stream[Throwable, Boolean] =
    receiveRepository.readMessage(gId, to, SystemConstant.GROUP_TYPE).map(_ == 1)

  override def saveAddMessage(addMessage: AddMessage): stream.Stream[Throwable, Int] =
    addMessageRepository.saveAddMessage(addMessage)

  override def countGroup(groupName: Option[String]): stream.Stream[Throwable, Int] =
    groupRepository.countGroup(groupName)

  override def findGroup(groupName: Option[String]): stream.Stream[Throwable, GroupList] =
    groupRepository.findGroup(groupName)

  override def countUsers(username: Option[String], sex: Option[Int]): stream.Stream[Throwable, Int] =
    userRepository.countUser(username, sex)

  override def findUsers(username: Option[String], sex: Option[Int]): stream.Stream[Throwable, User] =
    userRepository.findUsers(username, sex)

  override def countHistoryMessage(uid: Int, mid: Int, `type`: String): stream.Stream[Throwable, Int] =
    `type` match {
      case SystemConstant.FRIEND_TYPE => receiveRepository.countHistoryMessage(Some(uid), Some(mid), Some(`type`))
      case SystemConstant.GROUP_TYPE  => receiveRepository.countHistoryMessage(None, Some(mid), Some(`type`))
    }

  override def findHistoryMessage(user: User, mid: Int, `type`: String): stream.Stream[Throwable, ChatHistory] = {
    def userHistory() =
      //单人聊天记录
      for {
        toUser <- findUserById(mid)
        history <- receiveRepository.findHistoryMessage(Some(user.id), Some(mid), Some(`type`))
        newHistory =
          if (history.id == mid) {
            ChatHistory(
              history.id,
              toUser.username,
              toUser.avatar,
              history.content,
              history.timestamp
            )
          } else {
            ChatHistory(history.id, user.username, user.avatar, history.content, history.timestamp)
          }
      } yield newHistory

    def groupHistory() =
      //群聊天记录
      for {
        u <- findUserById(mid)
        history <- receiveRepository.findHistoryMessage(None, Some(mid), Some(`type`))
        newHistory =
          if (history.fromid.equals(user.id)) {
            ChatHistory(user.id, user.username, user.avatar, history.content, history.timestamp)
          } else {
            ChatHistory(history.id, u.username, u.avatar, history.content, history.timestamp)
          }
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
      u <- userRepository.matchUser(user.email)
      isMath <- ZStream.fromEffect(
        SecurityUtil
          .matched(user.password, MessageDigest(u.password))
          .provideLayer(Hash.live)
      )
      ret <- if (u == null || !isMath) ZStream.empty else ZStream.succeed(u)
    } yield ret
  }

  override def findUserByGroupId(gid: Int): stream.Stream[Throwable, User] =
    userRepository.findUserByGroupId(gid)

  override def findFriendGroupsById(uid: Int): stream.Stream[Throwable, FriendList] = {
    val groupListStream = friendGroupRepository.findFriendGroupsById(uid).map { friendGroup =>
      FriendList(id = friendGroup.id, groupname = friendGroup.groupname, Nil)
    }
    val gids = groupListStream.map(_.id).map { id =>
      val userStream = userRepository.findUsersByFriendGroupIds(id)
      val list = ListBuffer[User]()
      userStream.foreach(u => ZIO.succeed(list.append(u)))
      list.toList
    }
    for {
      groupList <- groupListStream
      users <- gids
    } yield groupList.copy(list = users)
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
      pwd <- SecurityUtil.encrypt(user.password).provideLayer(Hash.live)
      userCopy = user.copy(
        active = activeCode,
        createDate = ZonedDateTime.now(),
        password = pwd.value
      )
      _ <- userRepository.saveUser(userCopy).runHead
      _ <- createFriendGroup(SystemConstant.DEFAULT_GROUP_NAME, userCopy.id).runHead
      _ <- mailService.sendHtmlMail(
        userCopy.email,
        SystemConstant.SUBJECT,
        s"${userCopy.username} 请确定这是你本人注册的账号, http://${zimConfigurationProperties.interface}:${zimConfigurationProperties.port}/user/active/" + activeCode
      )
    } yield true
    ZStream.fromEffect(zioRet)
  }

}

object UserService {

  type ZUserApplication = Has[UserApplication]

  def apply(
    userRepository: UserRepository[User],
    groupRepository: GroupRepository[GroupList],
    receiveRepository: ReceiveRepository[Receive],
    friendGroupRepository: FriendGroupRepository[FriendGroup],
    friendGroupFriendRepository: FriendGroupFriendRepository[AddFriend],
    groupMemberRepository: GroupMemberRepository[GroupMember],
    addMessageRepository: AddMessageRepository[AddMessage],
    mailService: MailService,
    zimConfigurationProperties: ZimConfigurationProperties
  ): UserApplication =
    new UserService(
      userRepository,
      groupRepository,
      receiveRepository,
      friendGroupRepository,
      friendGroupFriendRepository,
      groupMemberRepository,
      addMessageRepository,
      mailService,
      zimConfigurationProperties
    )
}
