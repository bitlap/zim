package org.bitlap.zim.server.application
import org.bitlap.zim.domain._
import org.bitlap.zim.domain.input.{ FriendGroupInput, GroupInput, RegisterUserInput, UpdateUserInput, UserSecurity }
import org.bitlap.zim.domain.model.{ Receive, User }
import zio.{ stream, Chunk, IO }

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
  def chatLog(id: Int, `type`: String, page: Int, mid: Int): IO[Throwable, List[ChatHistory]]

  /**
   * 分页接口 内存分页
   *
   * @param uid
   * @param page
   * @return
   */
  def findAddInfo(uid: Int, page: Int): IO[Throwable, ResultPageSet[AddInfo]]
}
