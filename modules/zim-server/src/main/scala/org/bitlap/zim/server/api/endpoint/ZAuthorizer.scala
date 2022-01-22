package org.bitlap.zim.server.api.endpoint

import org.bitlap.zim.domain.input.UserSecurity
import org.bitlap.zim.domain.model.User
import org.bitlap.zim.server.api.exception.ZimError.Unauthorized
import org.bitlap.zim.server.repository.TangibleUserRepository
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

  lazy val Authorization: String = "Authorization"

  lazy val zioRuntime: zio.Runtime[zio.ZEnv] = zio.Runtime.default

  /**
   * 鉴权，目前写死，后续使用Redis缓存用户信息，没有再查库
   * @param token
   * @return
   */
  def authenticate(token: UserSecurity): Future[Either[Unauthorized, User]] =
    Future {
      val tk = if (token.value.trim.contains(" ")) token.value.trim.split(" ")(1) else token.value
      val secret: String = Try(new String(Base64.getDecoder.decode(tk))).getOrElse(null)
      if (secret == null || secret.isEmpty) {
        Left(Unauthorized())
      } else if (secret.contains(":")) {
        val usernamePassword = secret.split(":")
        val email = usernamePassword(0)
        val user = zioRuntime.unsafeRun(
          TangibleUserRepository.matchUser(email).runHead.provideLayer(TangibleUserRepository.make("zim"))
        )
        if (user.isEmpty) Left(Unauthorized())
        else
          Right(User(user.map(_.id).getOrElse(0), usernamePassword(0), usernamePassword(1)))
      } else {
        Left(Unauthorized())
      }
    }

  val secureEndpoint: PartialServerEndpoint[UserSecurity, User, Unit, Unauthorized, Unit, Any, Future] = endpoint
    .securityIn(auth.basic[String]().mapTo[UserSecurity])
    .errorOut(customJsonBody[Unauthorized])
    .serverSecurityLogic(authenticate)

}
