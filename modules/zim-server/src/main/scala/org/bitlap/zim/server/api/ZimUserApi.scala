package org.bitlap.zim.server.api

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, HttpResponse }
import akka.http.scaladsl.server.Directive.addDirectiveApply
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import org.bitlap.zim.domain.input.UserSecurity
import org.bitlap.zim.domain.model.User
import org.bitlap.zim.domain.{ FriendAndGroupInfo, SystemConstant }
import org.bitlap.zim.server.api.ZimUserEndpoint._
import org.bitlap.zim.server.application.ApiApplication
import org.bitlap.zim.server.application.impl.ApiService.ZApiApplication
import org.bitlap.zim.server.util.FileUtil
import org.bitlap.zim.tapir.{ ApiErrorMapping, ApiJsonCodec }
import sttp.model.HeaderNames.Authorization
import sttp.model.Uri
import sttp.model.headers.CookieValueWithMeta
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import zio._
import zio.stream.ZStream

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

  private val USER: String = "user"

  // 定义所有接口的路由
  val route: Route = Route.seal(
    staticResources
      ~ userGetRoute
      ~ existEmailRoute
      ~ findUserByIdRoute
      ~ updateInfoRoute
      ~ loginRoute
      ~ indexRoute
      ~ initRoute
      ~ getOffLineMessageRoute
      ~ registerRoute
      ~ activeRoute
      ~ createGroupRoute
      ~ createUserGroupRoute
      ~ getMembersRoute
      ~ updateSignRoute
      ~ leaveOutGroupRoute
      ~ removeFriendRoute
      ~ changeGroupRoute
      ~ refuseFriendRoute
      ~ agreeFriendRoute
      ~ chatLogIndexRoute
      ~ chatLogRoute
      ~ findAddInfoRoute
  )

  lazy val userGetRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.userGetOneEndpoint.serverLogic { id =>
      val userStream = apiApplication.findById(id)
      buildMonoResponse[User]()(userStream)
    })

  lazy val findAddInfoRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.findAddInfoEndpoint.serverLogic { _ => input =>
      val userIO = apiApplication.findAddInfo(input._1, input._2)
      buildPagesResponse(userIO)
    })

  lazy val chatLogRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.chatLogEndpoint.serverLogic { user => input =>
      val userIO = apiApplication.chatLog(input._1, input._2, input._3, user.id)
      buildFlowResponse(ZStream.fromIterable(unsafeRun(userIO)))
    })

  lazy val agreeFriendRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.agreeFriendEndpoint.serverLogic { user => input =>
      val userStream = apiApplication.agreeFriend(input._1, input._2, input._3, input._4, user.id)
      buildBooleanMonoResponse()(userStream)
    })

  lazy val refuseFriendRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.refuseFriendEndpoint.serverLogic { user => input =>
      val userStream = apiApplication.refuseFriend(input._1, input._2, user.username)
      buildBooleanMonoResponse()(userStream)
    })

  lazy val changeGroupRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.changeGroupEndpoint.serverLogic { user => input =>
      val userStream = apiApplication.changeGroup(input._1, input._2, user.id)
      buildBooleanMonoResponse()(userStream)
    })

  lazy val removeFriendRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.removeFriendEndpoint.serverLogic { user => friendId =>
      val userStream = apiApplication.removeFriend(friendId, user.id)
      buildBooleanMonoResponse()(userStream)
    })

  lazy val leaveOutGroupRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.leaveOutGroupEndpoint.serverLogic { _ => input =>
      val userStream = apiApplication.leaveOutGroup(input._1, input._2)
      buildIntMonoResponse()(userStream)
    })

  lazy val updateSignRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.updateSignEndpoint.serverLogic { user => sign =>
      val userStream = apiApplication.updateSign(sign, user.id)
      buildBooleanMonoResponse()(userStream)
    })

  lazy val getMembersRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.getMembersEndpoint.serverLogic { _ => id =>
      val userStream = apiApplication.getMembers(id)
      buildFlowResponse(userStream)
    })

  lazy val createGroupRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.createGroupEndpoint.serverLogic { _ => input =>
      val userStream = apiApplication.createGroup(input)
      buildIntMonoResponse()(userStream)
    })

  lazy val createUserGroupRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.createUserGroupEndpoint.serverLogic { _ => input =>
      val userStream = apiApplication.createUserGroup(input)
      buildIntMonoResponse()(userStream)
    })

  lazy val activeRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.activeUserEndpoint.serverLogic { activeCode =>
      val resultStream = apiApplication.activeUser(activeCode)
      val str = unsafeRun(resultStream.runHead)
      val ret = buildIntMonoResponse()(ZStream.succeed(0))
      ret.map {
        case Right(s)    => Right(Tuple2(Uri.unsafeParse(s"/#tologin?status=$str"), s))
        case Left(value) => Left(value.msg)
      }
    })

  lazy val registerRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.registerEndpoint.serverLogic { input =>
      val resultStream = apiApplication.register(input)
      buildBooleanMonoResponse(msg = SystemConstant.REGISTER_FAIL)(resultStream)
    })

  lazy val getOffLineMessageRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.getOffLineMessageEndpoint.serverLogic { user => _ =>
      val resultStream = apiApplication.getOffLineMessage(user.id)
      buildFlowResponse(resultStream)
    })

  // 超高阶函数
  // user是一阶入参 表示登录用户
  // uid是二阶入参 表示接口的参数
  lazy val findUserByIdRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.findUserEndpoint.serverLogic { _ => uid =>
      val resultStream = apiApplication.findUserById(uid)
      buildMonoResponse()(resultStream)
    })

  lazy val existEmailRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.existEmailEndpoint.serverLogic { input =>
      val resultStream = apiApplication.existEmail(input.email)
      buildBooleanMonoResponse()(resultStream)
    })

  lazy val updateInfoRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.updateInfoEndpoint.serverLogic { _ => input =>
      val resultStream = apiApplication.updateInfo(input)
      buildBooleanMonoResponse()(resultStream)
    })

  lazy val loginRoute: Route = AkkaHttpServerInterpreter().toRoute(
    ZimUserEndpoint.loginEndpoint.serverLogic { input =>
      val resultStream = apiApplication.login(input)
      val ret = buildMonoResponse[User] {
        case user: User if user == null                       => SystemConstant.LOGIN_ERROR
        case user: User if user.status.equals("nonactivated") => SystemConstant.NON_ACTIVE
        case _                                                => null
      }(resultStream)
      ret.map {
        case Right(s) =>
          Right(
            Tuple2(
              CookieValueWithMeta.unsafeApply(
                value = input.toCookieValue,
                maxAge = Some(30 * 60 * 7L),
                httpOnly = true,
                secure = true
              ),
              s
            )
          )
        case Left(value) => Left(value)

      }
    }
  )

  lazy val initRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.initEndpoint.serverLogic { _ => input =>
      val userStream = apiApplication.init(input)
      buildMonoResponse[FriendAndGroupInfo]()(userStream)
    })

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

  // TODO 暂时先这样搞
  lazy val indexRoute: Route = get {
    pathPrefix(USER / "index") {
      cookie(Authorization) { user =>
        val checkFuture = authenticate(UserSecurity(user.value))(authorityCacheFunction).map(_.getOrElse(null))
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

  // TODO 暂时先这样搞
  lazy val chatLogIndexRoute: Route = get {
    pathPrefix(USER / "chatLogIndex") {
      parameters("id".as[Int], "type") { (id, `type`) =>
        cookie(Authorization) { user =>
          val checkFuture = authenticate(UserSecurity(user.value))(authorityCacheFunction).map(_.getOrElse(null))
          onComplete(checkFuture) {
            case util.Success(u) if u != null =>
              val pages = unsafeRun(apiApplication.chatLogIndex(id, `type`, u.id).runHead)
              val resp =
                HttpEntity(
                  ContentTypes.`text/html(UTF-8)`,
                  FileUtil.getFileAndInjectData(
                    "static/html/chatlog.html",
                    "${id}" -> id.toString,
                    "${type}" -> `type`,
                    "${pages}" -> pages.toString
                  )
                )
              complete(HttpResponse(OK, entity = resp))
            case _ => getFromResource("static/html/403.html")
          }
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
