/*
 * Copyright 2023 bitlap
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

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import org.bitlap.zim.ZimBuildInfo
import org.bitlap.zim.api._

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import sttp.apispec.asyncapi.circe.yaml.RichAsyncAPI
import sttp.apispec.openapi._
import sttp.apispec.openapi.circe.yaml.RichOpenAPI
import sttp.tapir.AnyEndpoint
import sttp.tapir.docs.asyncapi.AsyncAPIInterpreter
import sttp.tapir.docs.openapi._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import sttp.tapir.swagger._

/** Open API
 *  @see
 *    http://host:port/api/v1.0/docs
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 2.1
 */
final class ZimOpenApi {

  private lazy val info: ZimBuildInfo.type = ZimBuildInfo
  private lazy val contextPath             = "docs"
  private lazy val wsContextPath           = "wsDocs"
  lazy val openapi: String                 = s"${ApiEndpoint.apiResource}/${ApiEndpoint.apiVersion}/$contextPath"

  private lazy val endpoints: Seq[AnyEndpoint] =
    Seq(
      ActuatorEndpoint.healthEndpoint,
      ZimUserEndpoint.userGetOneEndpoint,
      ZimUserEndpoint.existEmailEndpoint,
      ZimUserEndpoint.loginEndpoint,
      ZimUserEndpoint.registerEndpoint,
      ZimUserEndpoint.activeUserEndpoint
    ) ++ Seq(
      ZimUserEndpoint.agreeFriendEndpoint,
      ZimUserEndpoint.changeGroupEndpoint,
      ZimUserEndpoint.chatLogEndpoint,
      ZimUserEndpoint.createGroupEndpoint,
      ZimUserEndpoint.createUserGroupEndpoint,
      ZimUserEndpoint.findAddInfoEndpoint,
      ZimUserEndpoint.findGroupsEndpoint,
      ZimUserEndpoint.findMyGroupsEndpoint,
      ZimUserEndpoint.findUserEndpoint,
      ZimUserEndpoint.findUsersEndpoint,
      ZimUserEndpoint.getMembersEndpoint,
      ZimUserEndpoint.getOffLineMessageEndpoint,
      ZimUserEndpoint.initEndpoint,
      ZimUserEndpoint.leaveOutGroupEndpoint,
      ZimUserEndpoint.refuseFriendEndpoint,
      ZimUserEndpoint.removeFriendEndpoint,
      ZimUserEndpoint.updateAvatarEndpoint,
      ZimUserEndpoint.updateInfoEndpoint,
      ZimUserEndpoint.updateSignEndpoint,
      ZimUserEndpoint.uploadFileEndpoint,
      ZimUserEndpoint.uploadImageEndpoint,
      ZimUserEndpoint.uploadGroupAvatarEndpoint
    ).map(_.endpoint)

  private lazy val openApiYaml: String = OpenAPIDocsInterpreter()
    .toOpenAPI(
      endpoints,
      Info(
        title = info.name,
        version = info.version,
        description = Some("zim is a ZIO-based IM"),
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

  lazy val wsDocs: String =
    AsyncAPIInterpreter().toAsyncAPI(WsEndpoint.wsEndpoint, "zim websocket endpoint", info.version).toYaml

  lazy val route: Route =
    AkkaHttpServerInterpreter().toRoute(
      SwaggerUI[Future](openApiYaml, SwaggerUIOptions(pathPrefix = openapi.split("/").toList, "docs.yaml", Nil, true))
    )

  lazy val wsDocsRoute: Route = pathPrefix(ApiEndpoint.apiResource / ApiEndpoint.apiVersion / wsContextPath) {
    get {
      complete(wsDocs)
    }
  }

}

object ZimOpenApi {

  lazy val zimOpenApiInstance: ZimOpenApi = new ZimOpenApi()
}
