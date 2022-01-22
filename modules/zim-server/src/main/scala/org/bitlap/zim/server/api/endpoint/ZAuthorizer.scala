package org.bitlap.zim.server.api.endpoint

import org.bitlap.zim.domain.input.UserSecurity
import org.bitlap.zim.domain.model.User
import org.bitlap.zim.server.api.exception.ZimError.Unauthorized
import sttp.tapir.server.PartialServerEndpoint
import sttp.tapir.{ auth, customJsonBody, endpoint }

import java.util.Base64
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

/**
 *  auth
 * @author 梦境迷离
 * @since 2022/1/22
 * @version 1.0
 */
trait ZAuthorizer extends ApiJsonCodec {

  def authenticate(token: UserSecurity): Future[Either[Unauthorized, User]] =
    Future {
      val secret: String = Try(new String(Base64.getDecoder.decode(token.value))).getOrElse(null)
      if (secret == null || secret.isEmpty) {
        Left(Unauthorized())
      } else if (secret.contains(":")) {
        val usernamePassword = secret.split(":")
        Right(User(usernamePassword(0), usernamePassword(1)))
      } else {
        Left(Unauthorized())
      }
    }

  val secureEndpoint: PartialServerEndpoint[UserSecurity, User, Unit, Unauthorized, Unit, Any, Future] = endpoint
    .securityIn(auth.basic[String]().mapTo[UserSecurity])
    .errorOut(customJsonBody[Unauthorized])
    .serverSecurityLogic(authenticate)

}
