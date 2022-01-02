package org.bitlap.zim.application

import org.bitlap.zim.domain.model.{ AddFriends, AddMessage, FriendGroup, GroupList, Receive, User }
import org.bitlap.zim.domain.{ AddInfo, ChatHistory, FriendList }
import org.bitlap.zim.repository.{
  FriendGroupFriendRepository,
  FriendGroupRepository,
  GroupRepository,
  ReceiveRepository,
  UserRepository
}
import zio.{ stream, Has }

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
  mailService: MailService
) extends UserApplication {

  override def findById(id: Long): stream.Stream[Throwable, User] =
    userRepository.findById(id)

  override def findAll(): stream.Stream[Throwable, User] =
    userRepository.findAll()

  override def leaveOutGroup(gid: Int, uid: Int): stream.Stream[Throwable, Boolean] = ???

  override def findGroupById(gid: Int): stream.Stream[Throwable, GroupList] = ???

  override def addGroupMember(gid: Int, uid: Int, messageBoxId: Int): stream.Stream[Throwable, Boolean] = ???

  override def addGroupMember(gid: Int, uid: Int): stream.Stream[Throwable, Boolean] = ???

  override def removeFriend(friendId: Int, uId: Int): stream.Stream[Throwable, Boolean] = ???

  override def updateAvatar(userId: Int, avatar: String): stream.Stream[Throwable, Boolean] = ???

  override def updateUserInfo(user: User): stream.Stream[Throwable, Boolean] = ???

  override def updateUserStatus(user: User): stream.Stream[Throwable, Boolean] = ???

  override def changeGroup(groupId: Int, uId: Int, mId: Int): stream.Stream[Throwable, Boolean] = ???

  override def addFriend(
    mid: Int,
    mgid: Int,
    tid: Int,
    tgid: Int,
    messageBoxId: Int
  ): stream.Stream[Throwable, Boolean] = ???

  override def createFriendGroup(groupname: String, uid: Int): stream.Stream[Throwable, Int] = ???

  override def createGroup(groupList: GroupList): stream.Stream[Throwable, Int] = ???

  override def countUnHandMessage(uid: Int, agree: Integer): stream.Stream[Throwable, Int] = ???

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
    mailService: MailService
  ): UserApplication =
    new UserService(
      userRepository,
      groupRepository,
      receiveRepository,
      friendGroupRepository,
      friendGroupFriendRepository,
      mailService
    )
}
