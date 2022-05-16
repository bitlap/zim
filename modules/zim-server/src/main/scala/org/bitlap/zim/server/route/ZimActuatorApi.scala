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

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.DebuggingDirectives
import org.bitlap.zim.ZimBuildInfo
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import org.bitlap.zim.tapir.ActuatorEndpoint

import scala.concurrent.Future

/** Actuator端点的API http://host:port/api/v1.0/health
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 2.0
 */
final class ZimActuatorApi {

  // https://doc.akka.io/docs/akka-http/current/routing-dsl/directives/debugging-directives/logRequestResult.html
  lazy val route: Route = DebuggingDirectives.logRequestResult("actuator-logger") {
    AkkaHttpServerInterpreter().toRoute(
      ActuatorEndpoint.healthEndpoint.serverLogic[Future](_ => Future.successful(Right(ZimBuildInfo.toMap)))
    )
  }

}

object ZimActuatorApi {
  def apply(): ZimActuatorApi = new ZimActuatorApi()
}
