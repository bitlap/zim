package org.bitlap.zim.repository

import zio.stream

/**
 * 用户的操作定义
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait UserRepository[T] extends BaseRepository[T] {

  def countUser(username: Option[String], sex: Option[Int]): stream.Stream[Throwable, Int]

  def findUsers(username: Option[String], sex: Option[Int]): stream.Stream[Throwable, T]

  def updateAvatar(avatar: String, uid: Int): stream.Stream[Throwable, Int]

  def updateSign(sign: String, uid: Int): stream.Stream[Throwable, Int]

  def updateUserInfo(id: Int, user: T): stream.Stream[Throwable, Int]

  def updateUserStatus(status: String, uid: Int): stream.Stream[Throwable, Int]

  def activeUser(activeCode: String): stream.Stream[Throwable, Int]

  def findUserByGroupId(gid: Int): stream.Stream[Throwable, T]

  def findUsersByFriendGroupIds(fgid: Int): stream.Stream[Throwable, T]

  def saveUser(user: T): stream.Stream[Throwable, Long]

  def matchUser(email: String): stream.Stream[Throwable, T]
}
