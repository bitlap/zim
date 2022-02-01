package org.bitlap.zim.server.api

import io.circe.jawn
import io.circe.syntax.EncoderOps
import org.bitlap.zim.auth.CookieAuthority
import org.bitlap.zim.cache.zioRedisService
import org.bitlap.zim.domain.input.UserSecurity
import org.bitlap.zim.domain.ZimError.Unauthorized
import org.bitlap.zim.domain.input.UserSecurity.UserSecurityInfo
import org.bitlap.zim.server.configuration.properties.MysqlConfigurationProperties
import org.bitlap.zim.server.repository.TangibleUserRepository
import org.bitlap.zim.server.util.{ LogUtil, SecurityUtil }
import org.bitlap.zim.tapir.{ ApiErrorMapping, UserEndpoint }
import sttp.tapir._
import sttp.tapir.server.PartialServerEndpoint
import zio.{ IO, ZIO }

import scala.concurrent.Future
import sttp.model.HeaderNames.Authorization

/**
 * 用户接口的端点
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait ZimUserEndpoint extends ApiErrorMapping with CookieAuthority with UserEndpoint {

  def authorityCacheFunction(email: String, passwd: String): IO[Throwable, (Boolean, Option[UserSecurityInfo])] =
    for {
      userSecurityInfo <- zioRedisService
        .get[String](email)
        .map(_.map(s => jawn.decode[UserSecurityInfo](s).getOrElse(null)))
      user <-
        if (userSecurityInfo.isEmpty || userSecurityInfo.get == null)
          TangibleUserRepository
            .matchUser(email)
            .map(user => UserSecurityInfo(user.id, user.email, user.password, user.username))
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

  override val secureEndpoint
    : PartialServerEndpoint[UserSecurity, UserSecurityInfo, Unit, Unauthorized, Unit, Any, Future] = endpoint
    .securityIn(cookie[String](Authorization).mapTo[UserSecurity])
    .errorOut(customJsonBody[Unauthorized])
    .serverSecurityLogic(token => authenticate(token)(authorityCacheFunction))
}

object ZimUserEndpoint extends ZimUserEndpoint
