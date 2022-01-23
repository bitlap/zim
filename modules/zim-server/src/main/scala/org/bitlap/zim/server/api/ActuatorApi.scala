package org.bitlap.zim.server.api

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.DebuggingDirectives
import org.bitlap.zim.ZimBuildInfo
import org.bitlap.zim.server.api.endpoint.ActuatorEndpoint
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.concurrent.Future

/**
 * Actuator端点的API
 * http://host:port/api/v1.0/health
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 2.0
 */
final class ActuatorApi {

  // https://doc.akka.io/docs/akka-http/current/routing-dsl/directives/debugging-directives/logRequestResult.html
  lazy val route: Route = DebuggingDirectives.logRequestResult("actuator-logger") {
    AkkaHttpServerInterpreter().toRoute(
      ActuatorEndpoint.healthEndpoint.serverLogic[Future](_ => Future.successful(Right(ZimBuildInfo.toMap)))
    )
  }

}

object ActuatorApi {
  def apply(): ActuatorApi = new ActuatorApi()
}
