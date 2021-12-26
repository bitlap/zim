package org.bitlap.zim.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import org.bitlap.zim.api.endpoint.{ ApiErrorMapping, ApiJsonCodec, UserEndpoint }
import org.bitlap.zim.application.UserApplication
import org.bitlap.zim.application.UserService.ZUserApplication
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
  lazy val route: Route = Route.seal(userGetRoute ~ userGetAllRoute)

  lazy val userGetRoute: Route = AkkaHttpServerInterpreter.toRoute(UserEndpoint.userGetOneEndpoint) { id =>
    val userStream = userApplication.findById(id)
    buildMonoResponse[User](userStream)
  }

  lazy val userGetAllRoute: Route = AkkaHttpServerInterpreter.toRoute(UserEndpoint.userGetAllEndpoint) { _ =>
    val userStream = userApplication.findAll()
    buildFlowResponse[User](userStream)
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
