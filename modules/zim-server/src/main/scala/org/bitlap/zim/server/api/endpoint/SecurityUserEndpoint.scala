package org.bitlap.zim.server.api.endpoint

import org.bitlap.zim.domain.input.UserSecurity
import org.bitlap.zim.domain.ZimError.Unauthorized
import org.bitlap.zim.tapir.{ ApiErrorMapping, UserEndpoint }
import sttp.tapir._
import org.bitlap.zim.domain.input.UserSecurity.UserSecurityInfo
import sttp.tapir.server.PartialServerEndpoint

import scala.concurrent.Future

/**
 * 用户接口的端点
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait SecurityUserEndpoint extends ApiErrorMapping with ZAuthorizer with UserEndpoint {

  override val secureEndpoint
    : PartialServerEndpoint[UserSecurity, UserSecurityInfo, Unit, Unauthorized, Unit, Any, Future] = endpoint
    .securityIn(cookie[String](Authorization).mapTo[UserSecurity])
    .errorOut(customJsonBody[Unauthorized])
    .serverSecurityLogic(authenticate)
}

object SecurityUserEndpoint extends SecurityUserEndpoint
