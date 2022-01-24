package org.bitlap.zim.tapir

import sttp.tapir.{ EndpointInput, _ }

/**
 * Open API的端点
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait ApiEndpoint {

  lazy val apiResource: String = "api"
  lazy val apiVersion: String = "v1.0"
  private[tapir] lazy val apiNameResource: String = "api-resource"
  private[tapir] lazy val apiDescriptionResource: String = "Api Resources"
  private[tapir] lazy val baseApiEndpoint: EndpointInput[Unit] = apiResource / apiVersion

  private[tapir] lazy val baseEndpoint: PublicEndpoint[Unit, Unit, Unit, Any] =
    endpoint.in(baseApiEndpoint).name(apiNameResource).description(apiDescriptionResource)

}

object ApiEndpoint extends ApiEndpoint
