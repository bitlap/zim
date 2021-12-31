package org.bitlap.zim.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.circe.syntax._
import org.bitlap.zim.api.endpoint.{ ActuatorEndpoint, ApiEndpoint, UserEndpoint }
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe._
import sttp.tapir.openapi.{ Contact, Info, License }

/**
 * Open API
 * @see http://localhost:9000/api/v1.0/docs
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
final class OpenApi {

  private lazy val openApi: String = OpenAPIDocsInterpreter
    .toOpenAPI(
      Seq(
        ActuatorEndpoint.healthEndpoint,
        UserEndpoint.userGetOneEndpoint,
        UserEndpoint.userGetAllEndpoint
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
    .asJson
    .toString()

  private lazy val contextPath = "docs"

  lazy val openapi = s"${ApiEndpoint.apiResource}/${ApiEndpoint.apiVersion}/$contextPath"

  lazy val route: Route = pathPrefix(ApiEndpoint.apiResource / ApiEndpoint.apiVersion / contextPath) {
    get {
      complete(openApi)
    }
  }

}

object OpenApi {

  def apply(): OpenApi = new OpenApi()
}
