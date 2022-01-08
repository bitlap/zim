package org.bitlap.zim.application

import org.bitlap.zim.domain.model.{ AddFriends, AddMessage, FriendGroup, GroupList, GroupMember, Receive, User }
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
import zio.logging.log
import zio.stream.ZStream
import zio.{ stream, Has, ZIO }

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
  friendGroupFriendRepository: FriendGroupFriendRepository[AddFriends],
  groupMemberRepository: GroupMemberRepository[GroupMember],
  addMessageRepository: AddMessageRepository[AddMessage],
  mailService: MailService
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
            // TODO wsService.deleteGroup(master, group.groupname, gid, uid)
            groupMemberRepository.leaveOutGroup(GroupMember(gid, uid))
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
    val add = AddFriends(mid, mgid, tid, tgid)
    friendGroupFriendRepository
      .addFriend(add)
      .flatMap(c => if (c == 1) updateAddMessage(messageBoxId, 1) else ZStream.succeed(false))
      .onError { cleanup =>
        // TODO
        log.error(cleanup.map(_.getMessage).prettyPrint)
        ZIO.succeed(false)
      }
  }

  override def createFriendGroup(groupname: String, uid: Int): stream.Stream[Throwable, Int] =
    friendGroupRepository.createFriendGroup(FriendGroup(0, uid, groupname))

  override def createGroup(groupList: GroupList): stream.Stream[Throwable, Int] =
    groupRepository.createGroupList(groupList).map(_.toInt)

  override def countUnHandMessage(uid: Int, agree: Int): stream.Stream[Throwable, Int] =
    addMessageRepository.countUnHandMessage(uid, agree)

  override def findAddInfo(uid: Int): stream.Stream[Throwable, AddInfo] = ???

  override def updateAddMessage(messageBoxId: Int, agree: Int): stream.Stream[Throwable, Boolean] = ???

  override def refuseAddFriend(messageBoxId: Int, user: User, to: Int): stream.Stream[Throwable, Boolean] = ???

  override def readFriendMessage(mine: Int, to: Int): stream.Stream[Throwable, Boolean] = ???

  override def readGroupMessage(gId: Int, to: Int): stream.Stream[Throwable, Boolean] = ???

  override def saveAddMessage(addMessage: AddMessage): stream.Stream[Throwable, Int] = ???

  override def countGroup(groupName: String): stream.Stream[Throwable, Int] = ???

  override def findGroup(groupName: String): stream.Stream[Throwable, GroupList] = ???

  override def countUsers(username: String, sex: Integer): stream.Stream[Throwable, Int] = ???

  override def findUsers(username: String, sex: Integer): stream.Stream[Throwable, User] = ???

  override def countHistoryMessage(uid: Int, mid: Int, `type`: String): stream.Stream[Throwable, Int] = ???

  override def findHistoryMessage(user: User, mid: Int, `type`: String): stream.Stream[Throwable, ChatHistory] = ???

  override def findOffLineMessage(uid: Int, status: Int): stream.Stream[Throwable, Receive] = ???

  override def saveMessage(receive: Receive): stream.Stream[Throwable, Int] = ???

  override def updateSing(user: User): stream.Stream[Throwable, Boolean] = ???

  override def activeUser(activeCode: String): stream.Stream[Throwable, Int] = ???

  override def existEmail(email: String): stream.Stream[Throwable, Boolean] = ???

  override def matchUser(user: User): stream.Stream[Throwable, User] = ???

  override def findUserByGroupId(gid: Int): stream.Stream[Throwable, User] = ???

  override def findFriendGroupsById(uid: Int): stream.Stream[Throwable, FriendList] = ???

  override def findUserById(id: Int): stream.Stream[Throwable, User] = ???

  override def findGroupsById(id: Int): stream.Stream[Throwable, GroupList] = ???

  override def saveUser(user: User): stream.Stream[Throwable, Boolean] = ???

}

object UserService {

  type ZUserApplication = Has[UserApplication]

  def apply(
    userRepository: UserRepository[User],
    groupRepository: GroupRepository[GroupList],
    receiveRepository: ReceiveRepository[Receive],
    friendGroupRepository: FriendGroupRepository[FriendGroup],
    friendGroupFriendRepository: FriendGroupFriendRepository[AddFriends],
    groupMemberRepository: GroupMemberRepository[GroupMember],
    addMessageRepository: AddMessageRepository[AddMessage],
    mailService: MailService
  ): UserApplication =
    new UserService(
      userRepository,
      groupRepository,
      receiveRepository,
      friendGroupRepository,
      friendGroupFriendRepository,
      groupMemberRepository,
      addMessageRepository,
      mailService
    )
}
