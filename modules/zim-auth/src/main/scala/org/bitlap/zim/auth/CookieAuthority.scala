package org.bitlap.zim.auth

import org.bitlap.zim.domain.input.UserSecurity
import org.bitlap.zim.domain.input.UserSecurity.UserSecurityInfo
import org.bitlap.zim.domain.ZimError.Unauthorized
import zio.IO

import java.util.Base64
import scala.concurrent.Future
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Cookie based authentication
 *
 * @author 梦境迷离
 * @version 1.0,2022/1/25
 */
trait CookieAuthority {

  lazy val zioRuntime: zio.Runtime[zio.ZEnv] = zio.Runtime.default

  type AuthorityFunction = (String, String) => IO[Throwable, (Boolean, Option[UserSecurityInfo])]

  /**
   * 鉴权，目前写死，后续使用Redis缓存用户信息，没有再查库
   * @param token
   * @return
   */
  def authenticate(token: UserSecurity)(auth: AuthorityFunction): Future[Either[Unauthorized, UserSecurityInfo]] =
    Future {
      val tk = if (token.cookie.trim.contains(" ")) token.cookie.trim.split(" ")(1) else token.cookie
      val secret: String = Try(new String(Base64.getDecoder.decode(tk))).getOrElse(null)
      if (secret == null || secret.isEmpty) {
        Left(Unauthorized())
      } else if (secret.contains(":")) {
        val usernamePassword = secret.split(":")
        val email = usernamePassword(0)
        val passwd = usernamePassword(1)
        val ret = auth(email, passwd)
        val (check, user) = zioRuntime.unsafeRun(ret)
        if (!check) {
          Left(Unauthorized())
        } else {
          if (user.orNull == null) Left(Unauthorized()) else Right(user.orNull)
        }
      } else {
        Left(Unauthorized())
      }
    }

}
