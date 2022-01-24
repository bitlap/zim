package org.bitlap.zim.server.api.endpoint

import io.circe.jawn
import io.circe.syntax.EncoderOps
import org.bitlap.zim.cache.zioRedisService
import org.bitlap.zim.domain.input.UserSecurity
import org.bitlap.zim.domain.input.UserSecurity.UserSecurityInfo
import org.bitlap.zim.server.configuration.properties.MysqlConfigurationProperties
import org.bitlap.zim.server.repository.TangibleUserRepository
import org.bitlap.zim.server.util.{ LogUtil, SecurityUtil }
import zio.{ IO, ZIO }

import java.util.Base64
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try
import org.bitlap.zim.domain.ZimError.Unauthorized

/**
 *  auth
 * @author 梦境迷离
 * @since 2022/1/22
 * @version 1.0
 */
trait ZAuthorizer {

  lazy val zioRuntime: zio.Runtime[zio.ZEnv] = zio.Runtime.default

  /**
   * 鉴权，目前写死，后续使用Redis缓存用户信息，没有再查库
   * @param token
   * @return
   */
  def authenticate(token: UserSecurity): Future[Either[Unauthorized, UserSecurityInfo]] =
    Future {
      val tk = if (token.cookie.trim.contains(" ")) token.cookie.trim.split(" ")(1) else token.cookie
      val secret: String = Try(new String(Base64.getDecoder.decode(tk))).getOrElse(null)
      if (secret == null || secret.isEmpty) {
        Left(Unauthorized())
      } else if (secret.contains(":")) {
        val usernamePassword = secret.split(":")
        val email = usernamePassword(0)
        val passwd = usernamePassword(1)
        val ret = verifyUserAuth(email, passwd)
        val (check, user) = zioRuntime.unsafeRun(ret)
        if (!check) {
          Left(Unauthorized())
        } else {
          Right(user.orNull)
        }
      } else {
        Left(Unauthorized())
      }
    }

  private def verifyUserAuth(email: String, passwd: String): IO[Throwable, (Boolean, Option[UserSecurityInfo])] =
    for {
      userSecurityInfo <- zioRedisService
        .get[String](email)
        .map(_.map(s => jawn.decode[UserSecurityInfo](s).getOrElse(null)))
      user <-
        if (userSecurityInfo.isEmpty || userSecurityInfo.get == null)
          TangibleUserRepository
            .matchUser(email)
            .map(user => UserSecurityInfo(user.id, user.email, user.password))
            .runHead
            .provideLayer(TangibleUserRepository.make(MysqlConfigurationProperties().databaseName))
        else ZIO.succeed(userSecurityInfo)
      check <- SecurityUtil.matched(passwd, user.map(_.password).getOrElse(""))
      _ <- LogUtil.info(
        s"verifyUserAuth email=>$email passwd=>$passwd userSecurityInfo=>$userSecurityInfo user=>$user check=>$check"
      )
      _ <-
        if (check && user.isDefined) zioRedisService.set[String](email, user.get.asJson.noSpaces)
        else ZIO.succeed(false)
    } yield check -> user

}
