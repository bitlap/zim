package org.bitlap.zim.server.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import org.bitlap.zim.domain.model.User
import org.bitlap.zim.server.api.endpoint.{ ApiErrorMapping, ApiJsonCodec, UserEndpoint }
import org.bitlap.zim.server.application.ApiApplication
import org.bitlap.zim.server.application.impl.ApiService.ZApiApplication
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import zio._

/**
 * 用户API
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
final class ZimUserApi(apiApplication: ApiApplication)(implicit materializer: Materializer)
    extends ApiJsonCodec
    with ApiErrorMapping {

  // 定义所有接口的路由
  val route: Route = Route.seal(staticResources ~ userGetRoute ~ existEmailRoute ~ findUserByIdRoute)

  lazy val userGetRoute: Route = AkkaHttpServerInterpreter.toRoute(UserEndpoint.userGetOneEndpoint) { id =>
    val userStream = apiApplication.findById(id)
    buildMonoResponse[User](userStream)
  }

  lazy val findUserByIdRoute: Route = AkkaHttpServerInterpreter.toRoute(UserEndpoint.findUserEndpoint) { input =>
    val resultStream = apiApplication.findUserById(input)
    buildMonoResponse(resultStream)
  }

  lazy val existEmailRoute: Route = AkkaHttpServerInterpreter.toRoute(UserEndpoint.existEmailEndpoint) { input =>
    val resultStream = apiApplication.existEmail(input.email)
    buildBooleanMonoResponse(resultStream)
  }

  lazy val staticResources: Route =
    concat(
      pathSingleSlash {
        get {
          getFromResource("index.html")
        }
      },
      get {
        pathPrefix("static" / Remaining) { resource =>
          getFromResource("static/" + resource)
        }
      }
    )
}

object ZimUserApi {

  def apply(app: ApiApplication)(implicit materializer: Materializer): ZimUserApi = new ZimUserApi(app)

  type ZZimUserApi = Has[ZimUserApi]

  val route: ZIO[ZZimUserApi, Nothing, Route] =
    ZIO.access[ZZimUserApi](_.get.route)

  val live: ZLayer[ZApiApplication with Has[Materializer], Nothing, ZZimUserApi] =
    ZLayer.fromServices[ApiApplication, Materializer, ZimUserApi]((app, mat) => ZimUserApi(app)(mat))

}
