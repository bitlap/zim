package org.bitlap.zim.server.api.endpoint

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
  private[api] lazy val apiNameResource: String = "api-resource"
  private[api] lazy val apiDescriptionResource: String = "Api Resources"
  private[api] lazy val baseApiEndpoint: EndpointInput[Unit] = apiResource / apiVersion

  private[api] lazy val baseEndpoint: Endpoint[Unit, Unit, Unit, Any] =
    endpoint.in(baseApiEndpoint).name(apiNameResource).description(apiDescriptionResource)

}

object ApiEndpoint extends ApiEndpoint
