package org.bitlap.zim.server.repository

import org.bitlap.zim.domain.model
import org.bitlap.zim.domain.model.User
import org.bitlap.zim.domain.repository.UserRepository
import scalikejdbc._
import zio._

import scala.language.{ implicitConversions, postfixOps }

/**
 * 用户的操作实现
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
private final class TangibleUserRepository(databaseName: String)
    extends TangibleBaseRepository(User)
    with UserRepository {

  override implicit val sp: QuerySQLSyntaxProvider[SQLSyntaxSupport[User], User] = User.syntax("u")
  override implicit lazy val dbName: String = databaseName

  override def countUser(username: Option[String], sex: Option[Int]): stream.Stream[Throwable, Int] =
    this.count("username" like username, "sex" === sex)

  override def findUsers(username: Option[String], sex: Option[Int]): stream.Stream[Throwable, model.User] =
    this.find("username" like username, "sex" === sex)

  override def updateAvatar(avatar: String, uid: Int): stream.Stream[Throwable, Int] =
    _updateAvatar(avatar, uid).toUpdateOperation

  override def updateSign(sign: String, uid: Int): stream.Stream[Throwable, Int] =
    _updateSign(sign, uid).toUpdateOperation

  override def updateUserInfo(id: Int, user: model.User): stream.Stream[Throwable, Int] =
    _updateUserInfo(id, user).toUpdateOperation

  override def updateUserStatus(status: String, uid: Int): stream.Stream[Throwable, Int] =
    _updateUserStatus(status, uid).toUpdateOperation

  override def activeUser(activeCode: String): stream.Stream[Throwable, Int] =
    _activeUser(activeCode).toUpdateOperation

  override def findUserByGroupId(gid: Int): stream.Stream[Throwable, model.User] =
    _findUserByGroupId(gid).toStreamOperation

  override def findUsersByFriendGroupIds(fgid: Int): stream.Stream[Throwable, model.User] =
    _findUsersByFriendGroupIds(fgid).toStreamOperation

  override def saveUser(user: model.User): stream.Stream[Throwable, Long] =
    _saveUser(user).toUpdateReturnKey

  override def matchUser(email: String): stream.Stream[Throwable, model.User] =
    this.find("email" === email)
}

object TangibleUserRepository {

  def apply(databaseName: String): UserRepository =
    new TangibleUserRepository(databaseName)

  type ZUserRepository = Has[UserRepository]

  /**
   * 下面的测试很有用，对外提供
   *
   * @return
   */
  def findById(id: Int): stream.ZStream[ZUserRepository, Throwable, model.User] =
    stream.ZStream.accessStream(_.get.findById(id))

  def saveUser(user: model.User): stream.ZStream[ZUserRepository, Throwable, Long] =
    stream.ZStream.accessStream(_.get.saveUser(user))

  def matchUser(email: String): stream.ZStream[ZUserRepository, Throwable, model.User] =
    stream.ZStream.accessStream(_.get.matchUser(email))

  def activeUser(activeCode: String): stream.ZStream[ZUserRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.activeUser(activeCode))

  def countUser(username: Option[String], sex: Option[Int]): stream.ZStream[ZUserRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.countUser(username, sex))

  def findUsers(username: Option[String], sex: Option[Int]): stream.ZStream[ZUserRepository, Throwable, model.User] =
    stream.ZStream.accessStream(_.get.findUsers(username, sex))

  def findUserByGroupId(gid: Int): stream.ZStream[ZUserRepository, Throwable, model.User] =
    stream.ZStream.accessStream(_.get.findUserByGroupId(gid))

  def findUsersByFriendGroupIds(fgid: Int): stream.ZStream[ZUserRepository, Throwable, model.User] =
    stream.ZStream.accessStream(_.get.findUsersByFriendGroupIds(fgid))

  def updateAvatar(avatar: String, uid: Int): stream.ZStream[ZUserRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.updateAvatar(avatar, uid))

  def updateSign(sign: String, uid: Int): stream.ZStream[ZUserRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.updateSign(sign, uid))

  def updateUserInfo(id: Int, user: model.User): stream.ZStream[ZUserRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.updateUserInfo(id, user))

  def updateUserStatus(status: String, uid: Int): stream.ZStream[ZUserRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.updateUserStatus(status, uid))

  val live: ZLayer[Has[String], Nothing, ZUserRepository] =
    ZLayer.fromService[String, UserRepository](TangibleUserRepository(_))

  def make(databaseName: String): ULayer[ZUserRepository] =
    ZLayer.succeed(databaseName) >>> live

}
