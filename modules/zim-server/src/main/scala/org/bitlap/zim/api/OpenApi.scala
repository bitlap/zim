package org.bitlap.zim.api

import akka.http.scaladsl.server.Route
import org.bitlap.zim.api.endpoint.{ ActuatorEndpoint, ApiEndpoint, UserEndpoint }
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.openapi.{ Contact, Info, License }
import sttp.tapir.swagger.akkahttp.SwaggerAkka

/**
 * Open API
 * @see http://localhost:9000/api/v1.0/docs
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
final class OpenApi {

  private lazy val openApiYaml: String = OpenAPIDocsInterpreter
    .toOpenAPI(
      Seq(
        ActuatorEndpoint.healthEndpoint,
        UserEndpoint.userGetOneEndpoint,
        UserEndpoint.findUserEndpoint,
        UserEndpoint.indexEndpoint,
        UserEndpoint.existEmailEndpoint,
        UserEndpoint.activeUserEndpoint,
        UserEndpoint.agreeFriendEndpoint,
        UserEndpoint.refuseFriendEndpoint,
        UserEndpoint.changeGroupEndpoint,
        UserEndpoint.chatLogEndpoint,
        UserEndpoint.chatLogIndexEndpoint,
        UserEndpoint.createGroupEndpoint,
        UserEndpoint.createUserGroupEndpoint,
        UserEndpoint.findAddInfoEndpoint,
        UserEndpoint.findGroupsEndpoint,
        UserEndpoint.findMyGroupsEndpoint,
        UserEndpoint.findUsersEndpoint,
        UserEndpoint.getMembersEndpoint,
        UserEndpoint.getOffLineMessageEndpoint,
        UserEndpoint.uploadImageEndpoint,
        UserEndpoint.updateInfoEndpoint,
        UserEndpoint.updateSignEndpoint,
        UserEndpoint.uploadFileEndpoint,
        UserEndpoint.updateAvatarEndpoint,
        UserEndpoint.uploadGroupAvatarEndpoint,
        UserEndpoint.removeFriendEndpoint,
        UserEndpoint.leaveOutGroupEndpoint,
        UserEndpoint.loginEndpoint,
        UserEndpoint.registerEndpoint,
        UserEndpoint.initEndpoint
      ),
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

  private lazy val contextPath = "docs"

  lazy val openapi: String = s"${ApiEndpoint.apiResource}/${ApiEndpoint.apiVersion}/$contextPath"

  lazy val route: Route = new SwaggerAkka(openApiYaml, contextPath = openapi).routes

}

object OpenApi {

  def apply(): OpenApi = new OpenApi()
}
