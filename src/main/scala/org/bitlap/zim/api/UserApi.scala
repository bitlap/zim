package org.bitlap.zim.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import org.bitlap.zim.api.endpoint.{ ApiErrorMapping, ApiJsonCodec, UserEndpoint }
import org.bitlap.zim.application.UserApplication
import org.bitlap.zim.application.UserService.ZUserApplication
import org.bitlap.zim.configuration.SystemConstant
import org.bitlap.zim.domain.ZimError
import org.bitlap.zim.domain.ZimError.BusinessException
import org.bitlap.zim.domain.model.User
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import zio._

/**
 * 用户API
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
final class UserApi(userApplication: UserApplication)(implicit materializer: Materializer)
    extends ApiJsonCodec
    with ApiErrorMapping {

  // 定义所有接口的路由
  lazy val route: Route = Route.seal(userGetAllRoute ~ userGetRoute)

  lazy val userGetRoute: Route = AkkaHttpServerInterpreter.toRoute(UserEndpoint.userGetOneEndpoint) { id =>
    val userStream = userApplication.findById(id)
    val resp = userStream.mapError({
      case e: BusinessException => BusinessException(SystemConstant.ERROR, e.msg)
      case e: Exception         => e
    })
    buildMonoResponse[User](resp)
  }

  lazy val userGetAllRoute: Route = AkkaHttpServerInterpreter.toRoute(UserEndpoint.userGetAllEndpoint) { _ =>
    val userStream = userApplication.findAll()
    val resp = userStream.mapError[ZimError]({
      case e: BusinessException => BusinessException(SystemConstant.ERROR, e.msg)
      case e: Exception         => BusinessException(SystemConstant.ERROR, e.getMessage)
    })
    buildFlowResponse[User](resp)
  }
}

object UserApi {

  def apply(app: UserApplication)(implicit materializer: Materializer): UserApi = new UserApi(app)

  type ZUserApi = Has[UserApi]

  val route: ZIO[ZUserApi, Nothing, Route] =
    ZIO.access[ZUserApi](_.get.route)

  val live: ZLayer[ZUserApplication with Has[Materializer], Nothing, ZUserApi] =
    ZLayer.fromServices[UserApplication, Materializer, UserApi]((app, mat) => UserApi(app)(mat))

}
