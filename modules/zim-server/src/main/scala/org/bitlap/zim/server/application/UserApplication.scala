package org.bitlap.zim.server.application

import org.bitlap.zim.domain
import org.bitlap.zim.domain.model.{ GroupList, Receive, User }
import org.bitlap.zim.domain.{ model, AddInfo, FriendList }
import zio.stream

/**
 * 用户应用定义
 * 这不是最终接口，参数和返回值可能后面需要修改
 * 注意：sim项目controller层的逻辑都需要下沉到ApiService，而不只是直接使用UserService的逻辑
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait UserApplication extends BaseApplication[User] {

  /**
   * 退出群
   *
   * @param gid 群组id
   * @param uid 用户
   * @return
   */
  def leaveOutGroup(gid: Int, uid: Int): stream.Stream[Throwable, Boolean]

  /**
   * 根据ID查找群
   *
   * @param gid
   * @return
   */
  def findGroupById(gid: Int): stream.Stream[Throwable, GroupList]

  /**
   * 添加群成员
   *
   * @param gid          群组id
   * @param uid          用户id
   * @param messageBoxId 消息盒子Id
   * @return
   */
  def addGroupMember(gid: Int, uid: Int, messageBoxId: Int): stream.Stream[Throwable, Boolean]

  /**
   * 用户创建群时，将自己加入群组，不需要提示
   *
   * @param gid 群组id
   * @param uid 用户id
   * @return
   */
  def addGroupMember(gid: Int, uid: Int): stream.Stream[Throwable, Boolean]

  /**
   * 删除好友
   *
   * @param friendId 好友id
   * @param uId      个人/用户id
   * @return
   */
  def removeFriend(friendId: Int, uId: Int): stream.Stream[Throwable, Boolean]

  /**
   * 更新用户头像
   *
   * @param userId 个人id
   * @param avatar 头像
   * @return
   */
  def updateAvatar(userId: Int, avatar: String): stream.Stream[Throwable, Boolean]

  /**
   * 更新用户信息
   *
   * @param user 个人信息
   * @return
   */
  def updateUserInfo(user: User): stream.Stream[Throwable, Boolean]

  /**
   * 更新用户状态
   *
   * @param user 个人信息
   * @return
   */
  def updateUserStatus(user: User): stream.Stream[Throwable, Boolean]

  /**
   * 移动好友分组
   *
   * @param groupId 新的分组id
   * @param uId     被移动的好友id
   * @param mId     我的id
   * @return
   */
  def changeGroup(groupId: Int, uId: Int, mId: Int): stream.Stream[Throwable, Boolean]

  /**
   * 添加好友操作
   *
   * @param mid          我的id
   * @param mgid         我设定的分组
   * @param tid          对方的id
   * @param tgid         对方设定的分组
   * @param messageBoxId 消息盒子的消息id
   * @return
   */
  def addFriend(mid: Int, mgid: Int, tid: Int, tgid: Int, messageBoxId: Int): stream.Stream[Throwable, Boolean]

  /**
   * 创建好友分组列表
   *
   * @param uid       个人id
   * @param groupname 群组id
   * @return
   */
  def createFriendGroup(groupname: String, uid: Int): stream.Stream[Throwable, Int]

  /**
   * 创建群组
   *
   * @param groupList 群
   * @return
   */
  def createGroup(groupList: GroupList): stream.Stream[Throwable, Int]

  /**
   * 统计未处理消息
   *
   * @param uid   个人id
   * @param agree 0未处理，1同意，2拒绝
   * @return
   */
  def countUnHandMessage(uid: Int, agree: Option[Int]): stream.Stream[Throwable, Int]

  /**
   * 查询添加好友、群组信息
   *
   * @param uid 个人id
   * @return
   */
  def findAddInfo(uid: Int): stream.Stream[Throwable, AddInfo]

  /**
   * 更新好友、群组信息请求
   *
   * @param messageBoxId 消息盒子id
   * @param agree        0未处理，1同意，2拒绝
   * @return Boolean
   */
  def updateAddMessage(messageBoxId: Int, agree: Int): stream.Stream[Throwable, Boolean]

  /**
   * 拒绝添加好友
   * @param messageBoxId
   * @param username
   * @param to
   * @return
   */
  def refuseAddFriend(messageBoxId: Int, username: String, to: Int): stream.Stream[Throwable, Boolean]

  /**
   * 好友消息已读
   *
   * @param mine
   * @param to
   * @return
   */
  def readFriendMessage(mine: Int, to: Int): stream.Stream[Throwable, Boolean]

  /**
   * 将本群中的所有消息对我标记为已读
   *
   * @param gId
   * @param to 群离线消息的接收人to就是群的ID
   * @return
   */
  def readGroupMessage(gId: Int, to: Int): stream.Stream[Throwable, Boolean]

  /**
   * 添加好友、群组信息请求
   *
   * @param addMessage 添加好友、群组信息对象
   * @return
   */
  def saveAddMessage(addMessage: model.AddMessage): stream.Stream[Throwable, Int]

  /**
   * 根据群名模糊统计
   *
   * @param groupName 群组名称
   * @return
   */
  def countGroup(groupName: Option[String]): stream.Stream[Throwable, Int]

  /**
   * 根据群名模糊查询群
   *
   * @param groupName 群组名称
   * @return
   */
  def findGroup(groupName: Option[String]): stream.Stream[Throwable, GroupList]

  /**
   * 根据用户名和性别统计用户
   *
   * @param username 用户名
   * @param sex      性别
   * @return
   */
  def countUsers(username: Option[String], sex: Option[Int]): stream.Stream[Throwable, Int]

  /**
   * 根据用户名和性别查询用户
   *
   * @param username 用户名
   * @param sex      性别
   * @return
   */
  def findUsers(username: Option[String], sex: Option[Int]): stream.Stream[Throwable, User]

  /**
   * 统计查询消息
   *
   * @param uid    消息所属用户id、用户个人id
   * @param mid    来自哪个用户
   * @param `type` 消息类型，可能来自friend或者group
   * @return
   */
  def countHistoryMessage(uid: Int, mid: Int, `type`: String): stream.Stream[Throwable, Int]

  /**
   * 查询历史消息
   * @param user 所属用户、用户个人
   * @param mid 来自哪个用户
   * @param `type` 消息类型，可能来自friend或者group
   * @return
   */
  def findHistoryMessage(user: User, mid: Int, `type`: String): stream.Stream[Throwable, domain.ChatHistory]

  /**
   * 查询离线消息
   * @param uid 消息所属用户id、用户个人id
   * @param status 历史消息还是离线消息 0代表离线 1表示已读
   * @return
   */
  def findOffLineMessage(uid: Int, status: Int): stream.Stream[Throwable, Receive]

  /**
   * 保存用户聊天记录
   * @param receive
   * @return
   */
  def saveMessage(receive: Receive): stream.Stream[Throwable, Int]

  /**
   * 用户更新签名
   * @param user
   * @return
   */
  def updateSing(user: User): stream.Stream[Throwable, Boolean]

  /**
   * 激活码激活用户
   * @param activeCode
   * @return
   */
  def activeUser(activeCode: String): stream.Stream[Throwable, Int]

  /**
   * 判断邮件是否存在
   * @param email
   * @return
   */
  def existEmail(email: String): stream.Stream[Throwable, Boolean]

  /**
   * 用户邮件和密码是否匹配
   * @param user
   * @return
   */
  def matchUser(user: User): stream.Stream[Throwable, User]

  /**
   * 根据群组ID查询群里用户的信息
   * @param gid
   * @return
   */
  def findUserByGroupId(gid: Int): stream.Stream[Throwable, User]

  /**
   * 根据ID查询用户的好友分组的列表信息
   *
   * FriendList表示一个好友列表，一个用户可以有多个FriendList
   * @param uid
   * @return
   */
  def findFriendGroupsById(uid: Int): stream.Stream[Throwable, FriendList]

  /**
   * 根据ID查询用户信息
   * @param id
   * @return
   */
  def findUserById(id: Int): stream.Stream[Throwable, User]

  /**
   * 根据用户ID查询用户的群组列表
   * @param id
   * @return
   */
  def findGroupsById(id: Int): stream.Stream[Throwable, GroupList]

  /**
   * 保存用户信息
   * @param user
   * @return
   */
  def saveUser(user: User): stream.Stream[Throwable, Boolean]
}
