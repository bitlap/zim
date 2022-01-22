package org.bitlap.zim.server.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import org.bitlap.zim.domain.SystemConstant
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
 * @version 2.0
 */
final class ZimUserApi(apiApplication: ApiApplication)(implicit materializer: Materializer)
    extends ApiJsonCodec
    with ApiErrorMapping {

  // 定义所有接口的路由
  val route: Route = Route.seal(
    staticResources
      ~ userGetRoute
      ~ existEmailRoute
      ~ findUserByIdRoute
      ~ updateInfoRoute
      ~ loginRoute
  )

  lazy val userGetRoute: Route = AkkaHttpServerInterpreter().toRoute(UserEndpoint.userGetOneEndpoint.serverLogic { id =>
    val userStream = apiApplication.findById(id)
    buildMonoResponse[User](returnError => returnError == null)(userStream)
  })

  // 超高阶函数
  // user是一阶入参 表示登录用户
  // uid是二阶入参 表示接口的参数
  lazy val findUserByIdRoute: Route =
    AkkaHttpServerInterpreter().toRoute(UserEndpoint.findUserEndpoint.serverLogic { user => uid =>
      val resultStream = apiApplication.findUserById(uid)
      buildMonoResponse()(resultStream)
    })

  lazy val existEmailRoute: Route =
    AkkaHttpServerInterpreter().toRoute(UserEndpoint.existEmailEndpoint.serverLogic { input =>
      val resultStream = apiApplication.existEmail(input.email)
      buildBooleanMonoResponse()(resultStream)
    })

  lazy val updateInfoRoute: Route =
    AkkaHttpServerInterpreter().toRoute(UserEndpoint.updateInfoEndpoint.serverLogic { user => input =>
      val resultStream = apiApplication.updateInfo(input)
      buildBooleanMonoResponse()(resultStream)
    })

  lazy val loginRoute: Route = AkkaHttpServerInterpreter().toRoute(
    UserEndpoint.loginEndpoint.serverLogic { input =>
      val resultStream = apiApplication.login(input)
      buildMonoResponse[User](
        t => if (t == null || t.status.equals("nonactivated")) true else false,
        msg = SystemConstant.NONACTIVED
      )(resultStream)
    }
  )

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
