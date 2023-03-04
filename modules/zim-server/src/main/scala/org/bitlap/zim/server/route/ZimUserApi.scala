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

import java.time.Instant

import scala.concurrent._
import scala.util.Try

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directive.addDirectiveApply
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream._
import org.bitlap.zim.api._
import org.bitlap.zim.api.service._
import org.bitlap.zim.domain._
import org.bitlap.zim.domain.input._
import org.bitlap.zim.domain.model._
import org.bitlap.zim.infrastructure.repository.RStream
import org.bitlap.zim.server.FileUtil
import org.bitlap.zim.server.route.ZimUserEndpoint._
import sttp.model.HeaderNames._
import sttp.model.Uri
import sttp.model.headers._
import sttp.tapir.server.akkahttp._
import zio._
import zio.stream._

/** 用户API
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 2.0
 */
final class ZimUserApi(apiService: ApiService[RStream, Task])(implicit
  materializer: Materializer
) extends ApiJsonCodec
    with ApiErrorMapping {

  implicit val ec: ExecutionContext = materializer.executionContext

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
      ~ findUsersRoute
      ~ findGroupsRoute
      ~ findMyGroupsRoute
      ~ uploadFileRoute
      ~ uploadImageRoute
      ~ uploadGroupAvatarRoute
      ~ updateAvatarRoute
  )

  lazy val userGetRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.userGetOneEndpoint.serverLogic { id =>
      val userStream = apiService.findUserById(id.toInt)
      buildMonoResponse[User]()(userStream)
    })

  lazy val uploadFileRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.uploadFileEndpoint.serverLogic { _ => file =>
      val resultStream = apiService.uploadFile(file)
      buildMonoResponse()(resultStream)
    })

  lazy val uploadImageRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.uploadImageEndpoint.serverLogic { _ => file =>
      val resultStream = apiService.uploadImage(file)
      buildMonoResponse()(resultStream)
    })

  lazy val uploadGroupAvatarRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.uploadGroupAvatarEndpoint.serverLogic { _ => file =>
      val resultStream = apiService.uploadGroupAvatar(file)
      buildMonoResponse()(resultStream)
    })

  lazy val updateAvatarRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.updateAvatarEndpoint.serverLogic { user => file =>
      val resultStream = apiService.updateAvatar(file, user.id)
      buildMonoResponse()(resultStream)
    })

  lazy val findUsersRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.findUsersEndpoint.serverLogic { _ => input =>
      val userIO = apiService.findUsers(
        input._2,
        if (input._3.isEmpty) None else Some(Try(input._3.toInt).getOrElse(0)),
        input._1
      )
      buildPagesResponse(userIO)
    })

  lazy val findGroupsRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.findGroupsEndpoint.serverLogic { _ => input =>
      val userIO = apiService.findGroups(input._2, input._1)
      buildPagesResponse(userIO)
    })

  lazy val findMyGroupsRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.findMyGroupsEndpoint.serverLogic { _ => input =>
      val userIO = apiService.findMyGroups(input._2, input._1)
      buildPagesResponse(userIO)
    })

  lazy val findAddInfoRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.findAddInfoEndpoint.serverLogic { _ => input =>
      val userIO = apiService.findAddInfo(input._1, input._2)
      buildPagesResponse(userIO)
    })

  lazy val chatLogRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.chatLogEndpoint.serverLogic { user => input =>
      val userIO = apiService.chatLog(input._1, input._2, input._3, user.id)
      buildPagesResponse(userIO)
    })

  lazy val agreeFriendRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.agreeFriendEndpoint.serverLogic { user => input =>
      val userStream = apiService.agreeFriend(input.uid, input.from_group, input.group, input.messageBoxId, user.id)
      buildBooleanMonoResponse()(userStream)
    })

  lazy val refuseFriendRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.refuseFriendEndpoint.serverLogic { user => input =>
      val userStream = apiService.refuseFriend(input.messageBoxId, input.to, user.username)
      buildBooleanMonoResponse()(userStream)
    })

  lazy val changeGroupRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.changeGroupEndpoint.serverLogic { user => input =>
      val userStream = apiService.changeGroup(input.groupId, input.userId, user.id)
      buildBooleanMonoResponse()(userStream)
    })

  lazy val removeFriendRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.removeFriendEndpoint.serverLogic { user => input =>
      val userStream = apiService.removeFriend(input.friendId, user.id)
      buildBooleanMonoResponse()(userStream)
    })

  lazy val leaveOutGroupRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.leaveOutGroupEndpoint.serverLogic { _ => input =>
      val userStream = apiService.leaveOutGroup(input.groupId, input.uid)
      buildIntMonoResponse()(userStream)
    })

  lazy val updateSignRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.updateSignEndpoint.serverLogic { user => input =>
      val userStream = apiService.updateSign(input.sign, user.id)
      buildBooleanMonoResponse()(userStream)
    })

  lazy val getMembersRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.getMembersEndpoint.serverLogic { _ => id =>
      val userStream = apiService.getMembers(id)
      buildMonoResponse()(userStream)
    })

  lazy val createGroupRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.createGroupEndpoint.serverLogic { _ => input =>
      val userStream = apiService.createGroup(input)
      buildIntMonoResponse()(userStream)
    })

  lazy val createUserGroupRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.createUserGroupEndpoint.serverLogic { _ => input =>
      val userStream = apiService.createUserGroup(input)
      buildIntMonoResponse()(userStream)
    })

  lazy val activeRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.activeUserEndpoint.serverLogic { activeCode =>
      // FIXME 这里用了unsafeRun，只能try stream exception
      val resultStream = apiService.activeUser(activeCode)
      val str =
        try
          Unsafe.unsafe { implicit runtime =>
            // FIXME remove runHead
            Runtime.default.unsafe.run(resultStream.runHead).getOrThrowFiberFailure()
          }
        catch { case _: Throwable => Some(0) }
      val ret = buildIntMonoResponse()(ZStream.succeed(str.getOrElse(0)))
      ret.map {
        case Right(s)    => Right(Tuple2(Uri.unsafeParse(s"/#tologin?status=${str.getOrElse(0)}"), s))
        case Left(value) => Left(value.msg)
      }
    })

  lazy val registerRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.registerEndpoint.serverLogic { input =>
      val resultStream = apiService.register(input)
      buildBooleanMonoResponse(msg = SystemConstant.REGISTER_FAIL)(resultStream)
    })

  lazy val getOffLineMessageRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.getOffLineMessageEndpoint.serverLogic { user => _ =>
      val resultStream = apiService.getOffLineMessage(user.id)
      buildFlowResponse(resultStream)
    })

  // 二阶高阶函数
  // user是一阶入参 表示登录用户
  // uid是二阶入参 表示接口的参数
  lazy val findUserByIdRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.findUserEndpoint.serverLogic { _ => uid =>
      val resultStream = apiService.findUserById(uid)
      buildMonoResponse()(resultStream)
    })

  lazy val existEmailRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.existEmailEndpoint.serverLogic { input =>
      val resultStream = apiService.existEmail(input.email)
      buildBooleanMonoResponse()(resultStream)
    })

  lazy val updateInfoRoute: Route =
    AkkaHttpServerInterpreter().toRoute(ZimUserEndpoint.updateInfoEndpoint.serverLogic { _ => input =>
      val resultStream = apiService.updateInfo(input)
      val ret          = buildBooleanMonoResponse()(resultStream)
      ret.map {
        case Right(s) =>
          Right(
            Tuple2(
              CookieValueWithMeta.unsafeApply(
                value = "deleted",
                expires = Some(Instant.ofEpochMilli(DateTime.MinValue.clicks)),
                httpOnly = true,
                secure = false
              ),
              s
            )
          )
        case Left(value) => Left(value)

      }
    })

  lazy val loginRoute: Route = AkkaHttpServerInterpreter().toRoute(
    ZimUserEndpoint.loginEndpoint.serverLogic { input =>
      val resultStream = apiService.login(input)
      val ret          = buildMonoResponse[User](true)(resultStream)
      ret.map {
        case Right(s) =>
          Right(
            Tuple2(
              CookieValueWithMeta.unsafeApply(
                value = input.toCookieValue,
                maxAge = Some(30 * 60 * 7L),
                httpOnly = true,
                secure = false
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
      val userStream = apiService.init(input)
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
          getFromResource("static/" + resource) // 项目自带的文件 映射到类路径
        } ~
          pathPrefix("static" / Remaining) { resource =>
            getFromFile("./static/" + resource) // 上传的文件 映射到本地目录
          }
      }
    )

  // FIXME 暂时先这样搞
  lazy val indexRoute: Route = get {
    pathPrefix(USER / "index") {
      cookie(Authorization) { user =>
        val checkFuture = authenticate(UserToken(user.value))(authorityCacheFunction).map(_.getOrElse(null))
        onComplete(checkFuture) {
          case scala.util.Success(u) if u != null =>
            // 这是不使用任何渲染模板
            val resp =
              HttpEntity(
                ContentTypes.`text/html(UTF-8)`,
                FileUtil.getFileAndInjectData(
                  "static/html/index.html",
                  "${uid}",
                  s"${u.id}"
                )
              )
            val httpResp = HttpResponse(OK, entity = resp) /*.addAttribute(AttributeKey("uid"), u.id)*/
            complete(httpResp)
          case _ =>
            deleteCookie(Authorization) {
              getFromResource("static/html/403.html")
            }

        }
      }
    }
  }

  // FIXME 暂时先这样搞
  lazy val chatLogIndexRoute: Route = get {
    pathPrefix(USER / "chatLogIndex") {
      parameters("id".as[Int], "type".as[String].withDefault(SystemConstant.FRIEND_TYPE)) { (id, `type`) =>
        cookie(Authorization) { user =>
          val checkFuture = authenticate(UserToken(user.value))(authorityCacheFunction).map(_.getOrElse(null))
          onComplete(checkFuture) {
            case scala.util.Success(u) if u != null =>
              try {
                val typ = if (`type` == "undefined") SystemConstant.FRIEND_TYPE else `type`
                val pages: Option[Int] = Unsafe.unsafe { implicit runtime =>
                  // FIXME remove runHead
                  Runtime.default.unsafe
                    .run(apiService.chatLogIndex(u.id, typ, id).runHead)
                    .getOrThrowFiberFailure()
                }
                val resp =
                  HttpEntity(
                    ContentTypes.`text/html(UTF-8)`,
                    FileUtil.getFileAndInjectData(
                      "static/html/chatlog.html",
                      "${id}"    -> id.toString,
                      "${type}"  -> typ,
                      "${pages}" -> pages.getOrElse(1).toString
                    )
                  )
                complete(HttpResponse(OK, entity = resp))
              } catch {
                case _: Exception =>
                  getFromResource("static/html/404.html")
              }
            case _ =>
              deleteCookie(Authorization) {
                getFromResource("static/html/403.html")
              }
          }
        }
      }
    }
  }
}

object ZimUserApi {

  def apply(app: ApiService[RStream, Task])(implicit materializer: Materializer): ZimUserApi =
    new ZimUserApi(app)

  lazy val live: ZLayer[ApiService[RStream, Task] with Materializer, Nothing, ZimUserApi] =
    ZLayer(
      for {
        apiService   <- ZIO.service[ApiService[RStream, Task]]
        materializer <- ZIO.service[Materializer]
      } yield ZimUserApi(apiService)(materializer)
    )
}
