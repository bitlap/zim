package org.bitlap.zim.server.api

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, HttpResponse }
import akka.http.scaladsl.server.Directive.addDirectiveApply
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import org.bitlap.zim.domain.SystemConstant
import org.bitlap.zim.domain.input.UserSecurity
import org.bitlap.zim.domain.model.User
import org.bitlap.zim.server.api.endpoint.UserEndpoint.{ authenticate, userResource, Authorization }
import org.bitlap.zim.server.api.endpoint.{ ApiErrorMapping, ApiJsonCodec, UserEndpoint }
import org.bitlap.zim.server.application.ApiApplication
import org.bitlap.zim.server.application.impl.ApiService.ZApiApplication
import org.bitlap.zim.server.util.FileUtil
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import zio._

import scala.concurrent.ExecutionContext.Implicits.global

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
      ~ indexRoute
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
        msg = SystemConstant.REGISTER_FAIL
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
        path("favicon.ico") {
          getFromResource("static/image/favicon.ico")
        }
      },
      get {
        pathPrefix("static" / Remaining) { resource =>
          getFromResource("static/" + resource)
        }
      }
    )

  lazy val indexRoute: Route = get {
    pathPrefix(userResource.show / "index") {
      headerValueByName(Authorization) { user =>
        val checkFuture = authenticate(UserSecurity(user)).map(_.getOrElse(null))
        onComplete(checkFuture) {
          case util.Success(u) if u != null =>
            // 这是不使用任何渲染模板
            val resp =
              HttpEntity(
                ContentTypes.`text/html(UTF-8)`,
                FileUtil.getFileAndInjectData("static/html/index.html", "${uid}", s"${u.id}")
              )
            val httpResp = HttpResponse(OK, entity = resp) /*.addAttribute(AttributeKey("uid"), u.id)*/
            complete(httpResp)
          case _ => getFromResource("static/html/403.html")

        }
      }
    }
  }
}

object ZimUserApi {

  def apply(app: ApiApplication)(implicit materializer: Materializer): ZimUserApi = new ZimUserApi(app)

  type ZZimUserApi = Has[ZimUserApi]

  val route: ZIO[ZZimUserApi, Nothing, Route] =
    ZIO.access[ZZimUserApi](_.get.route)

  val live: ZLayer[ZApiApplication with Has[Materializer], Nothing, ZZimUserApi] =
    ZLayer.fromServices[ApiApplication, Materializer, ZimUserApi]((app, mat) => ZimUserApi(app)(mat))

}
