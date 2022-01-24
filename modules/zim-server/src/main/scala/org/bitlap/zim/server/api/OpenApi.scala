package org.bitlap.zim.server.api

import akka.http.scaladsl.server.Route
import org.bitlap.zim.tapir.{ ActuatorEndpoint, ApiEndpoint }
import sttp.tapir.AnyEndpoint
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.openapi.{ Contact, Info, License }
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import sttp.tapir.swagger.SwaggerUI

import scala.concurrent.Future
import org.bitlap.zim.server.api.endpoint.SecurityUserEndpoint

/**
 * Open API
 * @see http://localhost:9000/api/v1.0/docs
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 2.0
 */
final class OpenApi {

  private lazy val contextPath = "docs"
//  private lazy val wsContextPath = "wsDocs"
  lazy val openapi: String = s"${ApiEndpoint.apiResource}/${ApiEndpoint.apiVersion}/$contextPath"

//  import Directives._

  // 需要鉴权的不支持
  private lazy val endpoints: Seq[AnyEndpoint] = Seq(
    ActuatorEndpoint.healthEndpoint,
    SecurityUserEndpoint.userGetOneEndpoint,
//    UserEndpoint.findUserEndpoint,
//    UserEndpoint.indexEndpoint,
    SecurityUserEndpoint.existEmailEndpoint,
    SecurityUserEndpoint.activeUserEndpoint,
//    UserEndpoint.agreeFriendEndpoint,
//    UserEndpoint.refuseFriendEndpoint,
//    UserEndpoint.changeGroupEndpoint,
//    UserEndpoint.chatLogEndpoint,
//    UserEndpoint.chatLogIndexEndpoint,
//    UserEndpoint.createGroupEndpoint,
//    UserEndpoint.createUserGroupEndpoint,
//    UserEndpoint.findAddInfoEndpoint,
//    UserEndpoint.findGroupsEndpoint,
//    UserEndpoint.findMyGroupsEndpoint,
//    UserEndpoint.findUsersEndpoint,
//    UserEndpoint.getMembersEndpoint,
//    UserEndpoint.getOffLineMessageEndpoint,
//    UserEndpoint.uploadImageEndpoint,
//    UserEndpoint.updateInfoEndpoint,
//    UserEndpoint.updateSignEndpoint,
//    UserEndpoint.uploadFileEndpoint,
//    UserEndpoint.updateAvatarEndpoint,
//    UserEndpoint.uploadGroupAvatarEndpoint,
//    UserEndpoint.removeFriendEndpoint,
//    UserEndpoint.leaveOutGroupEndpoint,
    SecurityUserEndpoint.loginEndpoint,
    SecurityUserEndpoint.registerEndpoint
//    UserEndpoint.initEndpoint
  )
  private lazy val openApiYaml: String = OpenAPIDocsInterpreter()
    .toOpenAPI(
      endpoints,
      Info(
        title = "zim",
        version = "0.0.1",
        description = Some("A LayIM base on ZIO"),
        termsOfService = None,
        contact = Some(
          Contact(
            name = Some("bitlap"),
            email = Some("dreamylost@outlook.com"),
            url = Some("https://github.com/bitlap/zim")
          )
        ),
        license = Some(License(name = "Apache License 2.0", Some("https://github.com/bitlap/zim/blob/master/LICENSE")))
      )
    )
    .toYaml

//  lazy val wsDocs: String = AsyncAPIInterpreter.toAsyncAPI(WsEndpoint.wsEndpoint, "zim web socket", "0.1.0").toYaml

  lazy val route: Route =
    AkkaHttpServerInterpreter().toRoute(SwaggerUI[Future](openApiYaml, prefix = openapi.split("/").toList))
//
//  lazy val ws: Route = pathPrefix(ApiEndpoint.apiResource / ApiEndpoint.apiVersion / wsContextPath) {
//    Directives.get {
//      complete(wsDocs)
//    }
//  }

}

object OpenApi {

  def apply(): OpenApi = new OpenApi()
}
