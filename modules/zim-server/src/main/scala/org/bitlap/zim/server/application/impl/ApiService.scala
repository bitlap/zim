package org.bitlap.zim.server.application.impl

import org.bitlap.zim.domain.input.UserInput
import org.bitlap.zim.domain.model.User
import org.bitlap.zim.server.application.{ ApiApplication, UserApplication }
import org.bitlap.zim.server.util.SecurityUtil
import zio.crypto.hash.MessageDigest
import zio.stream.ZStream
import zio.{ stream, Has }

/**
 * @author 梦境迷离
 * @since 2022/1/8
 * @version 1.0
 */
private final class ApiService(userApplication: UserApplication) extends ApiApplication {

  override def findById(id: Long): stream.Stream[Throwable, User] = userApplication.findById(id)

  override def existEmail(email: String): stream.Stream[Throwable, Boolean] = userApplication.existEmail(email)

  override def findUserById(id: Int): stream.Stream[Throwable, User] = userApplication.findUserById(id)

  override def updateInfo(user: UserInput): stream.Stream[Throwable, Boolean] = {
    def check(): Boolean =
      user.password == null || user.password.trim.isEmpty || user.oldpwd == null || user.oldpwd.trim.isEmpty
    for {
      u <- userApplication.findUserById(user.id)
      pwdCheck <- ZStream.fromEffect(SecurityUtil.matched(user.oldpwd, MessageDigest(u.password)))
      newPwd <- ZStream.fromEffect(SecurityUtil.encrypt(user.password))
      sex = if (user.sex.equals("nan")) 1 else 0
      checkAndUpdate <-
        if (check()) {
          userApplication.updateUserInfo(u.copy(sex = sex, sign = user.sign, username = user.username))
        } else if (pwdCheck) {
          ZStream.succeed(false)
        } else {
          userApplication
            .updateUserInfo(
              u.copy(
                password = newPwd.value,
                sex = sex,
                sign = user.sign,
                username = user.username
              )
            )

        }
    } yield checkAndUpdate
  }
}

object ApiService {

  type ZApiApplication = Has[ApiApplication]

  def apply(userApplication: UserApplication): ApiApplication = new ApiService(userApplication)

}
