package org.bitlap.zim.server.application
import org.bitlap.zim.domain.FriendAndGroupInfo
import org.bitlap.zim.domain.input.{ FriendGroupInput, GroupInput, RegisterUserInput, UpdateUserInput, UserSecurity }
import org.bitlap.zim.domain.model.{ Receive, User }
import zio.stream

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

  def getOffLineMessage(userId: Int): stream.Stream[Throwable, Receive]

  def register(user: RegisterUserInput): stream.Stream[Throwable, Boolean]

  def activeUser(activeCode: String): stream.Stream[Throwable, Int]

  def createUserGroup(friendGroup: FriendGroupInput): stream.Stream[Throwable, Int]

  def createGroup(groupInput: GroupInput): stream.Stream[Throwable, Int]

}
