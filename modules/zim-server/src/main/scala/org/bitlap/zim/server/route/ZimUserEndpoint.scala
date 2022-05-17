/*
 * Copyright 2022 bitlap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bitlap.zim.server.route

import io.circe.jawn
import io.circe.syntax.EncoderOps
import org.bitlap.zim.auth.CookieAuthority
import org.bitlap.zim.cache.ZioRedisService
import org.bitlap.zim.domain.input.UserSecurity
import org.bitlap.zim.domain.ZimError.Unauthorized
import org.bitlap.zim.domain.input.UserSecurity.UserSecurityInfo
import org.bitlap.zim.infrastructure.properties.MysqlConfigurationProperties
import org.bitlap.zim.infrastructure.repository.TangibleUserRepository
import org.bitlap.zim.infrastructure.util.{ LogUtil, SecurityUtil }
import org.bitlap.zim.tapir.{ ApiErrorMapping, UserEndpoint }
import sttp.model.HeaderNames.Authorization
import sttp.tapir._
import sttp.tapir.server.PartialServerEndpoint
import zio.{ IO, ZIO }

import scala.concurrent.Future

/** 用户接口的端点
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 1.0
 */
trait ZimUserEndpoint extends ApiErrorMapping with CookieAuthority with UserEndpoint {

  def authorityCacheFunction(email: String, passwd: String): IO[Throwable, (Boolean, Option[UserSecurityInfo])] =
    for {
      userSecurityInfo <- ZioRedisService
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
        if (check && user.isDefined) ZioRedisService.set[String](email, user.get.asJson.noSpaces)
        else ZIO.succeed(false)
    } yield check -> user

  override val secureEndpoint
    : PartialServerEndpoint[UserSecurity, UserSecurityInfo, Unit, Unauthorized, Unit, Any, Future] = endpoint
    .securityIn(cookie[String](Authorization).mapTo[UserSecurity])
    .errorOut(customJsonBody[Unauthorized])
    .serverSecurityLogic(token => authenticate(token)(authorityCacheFunction))
}

object ZimUserEndpoint extends ZimUserEndpoint
