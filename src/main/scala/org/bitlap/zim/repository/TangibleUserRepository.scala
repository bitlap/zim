package org.bitlap.zim.repository

import org.bitlap.zim.domain.model.{ AddFriend, GroupMember, User }
import zio._

import scala.language.implicitConversions

/**
 * 用户的操作实现
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
private final class TangibleUserRepository(databaseName: String) extends UserRepository[User] {

  private implicit lazy val dbName: String = databaseName

  // 有些没用的可能需要删掉，抽象出真正几个repository通用的到base repository
  override def findById(id: Long): stream.Stream[Throwable, User] =
    queryFindUserById(User.table, id).toSQLOperation

  override def countUser(username: Option[String], sex: Option[Int]): stream.Stream[Throwable, Int] =
    _countUser(username, sex).toStreamOperation

  override def findUser(username: Option[String], sex: Option[Int]): stream.Stream[Throwable, User] =
    _findUser(username, sex).toStreamOperation

  override def updateAvatar(avatar: String, uid: Int): stream.Stream[Throwable, Int] =
    _updateAvatar(User.table, avatar, uid).toUpdateOperation

  override def updateSign(sign: String, uid: Int): stream.Stream[Throwable, Int] =
    _updateSign(User.table, sign, uid).toUpdateOperation

  override def updateUserInfo(id: Int, user: User): stream.Stream[Throwable, Int] =
    _updateUserInfo(User.table, id, user).toUpdateOperation

  override def updateUserStatus(status: String, uid: Int): stream.Stream[Throwable, Int] =
    _updateUserStatus(User.table, status, uid).toUpdateOperation

  override def activeUser(activeCode: String): stream.Stream[Throwable, Int] =
    _activeUser(User.table, activeCode).toUpdateOperation

  override def findUserByGroupId(gid: Int): stream.Stream[Throwable, User] =
    _findUserByGroupId(User.table, GroupMember.table, gid).toStreamOperation

  override def findUsersByFriendGroupIds(fgid: Int): stream.Stream[Throwable, User] =
    _findUsersByFriendGroupIds(User.table, AddFriend.table, fgid).toStreamOperation

  override def saveUser(user: User): stream.Stream[Throwable, Long] =
    _saveUser(User.table, user).toUpdateReturnKey

  override def matchUser(email: String): stream.Stream[Throwable, User] =
    _matchUser(User.table, email).toStreamOperation
}

object TangibleUserRepository {

  def apply(databaseName: String): UserRepository[User] =
    new TangibleUserRepository(databaseName)

  type ZUserRepository = Has[UserRepository[User]]

  /**
   * 下面的测试很有用，对外提供
   * @return
   */
  def findById(id: Int): stream.ZStream[ZUserRepository, Throwable, User] =
    stream.ZStream.accessStream(_.get.findById(id))

  def saveUser(user: User): stream.ZStream[ZUserRepository, Throwable, Long] =
    stream.ZStream.accessStream(_.get.saveUser(user))

  def matchUser(email: String): stream.ZStream[ZUserRepository, Throwable, User] =
    stream.ZStream.accessStream(_.get.matchUser(email))

  def activeUser(activeCode: String): stream.ZStream[ZUserRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.activeUser(activeCode))

  def countUser(username: Option[String], sex: Option[Int]): stream.ZStream[ZUserRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.countUser(username, sex))

  def findUser(username: Option[String], sex: Option[Int]): stream.ZStream[ZUserRepository, Throwable, User] =
    stream.ZStream.accessStream(_.get.findUser(username, sex))

  def findUserByGroupId(gid: Int): stream.ZStream[ZUserRepository, Throwable, User] =
    stream.ZStream.accessStream(_.get.findUserByGroupId(gid))

  def findUsersByFriendGroupIds(fgid: Int): stream.ZStream[ZUserRepository, Throwable, User] =
    stream.ZStream.accessStream(_.get.findUsersByFriendGroupIds(fgid))

  def updateAvatar(avatar: String, uid: Int): stream.ZStream[ZUserRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.updateAvatar(avatar, uid))

  def updateSign(sign: String, uid: Int): stream.ZStream[ZUserRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.updateSign(sign, uid))

  def updateUserInfo(id: Int, user: User): stream.ZStream[ZUserRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.updateUserInfo(id, user))

  def updateUserStatus(status: String, uid: Int): stream.ZStream[ZUserRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.updateUserStatus(status, uid))

  val live: ZLayer[Has[String], Nothing, ZUserRepository] =
    ZLayer.fromService[String, UserRepository[User]](TangibleUserRepository(_))

  def make(databaseName: String): ULayer[ZUserRepository] =
    ZLayer.succeed(databaseName) >>> live

}
