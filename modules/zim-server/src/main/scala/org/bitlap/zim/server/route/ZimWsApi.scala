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

import akka.NotUsed
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import org.bitlap.zim.server.service.ws.WsService
import org.bitlap.zim.server.zioRuntime
import org.bitlap.zim.tapir.WsEndpoint
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.concurrent.Future

/** @author
 *    梦境迷离
 *  @since 2022/1/16
 *  @version 1.0
 */
final class ZimWsApi()(implicit materializer: Materializer) {

  lazy val route: Route = AkkaHttpServerInterpreter().toRoute(WsEndpoint.wsEndpoint.serverLogic[Future] { uid =>
    val ret: Either[Unit, Flow[Message, String, NotUsed]] =
      try Right(zioRuntime.unsafeRun(WsService.openConnection(uid)))
      catch { case _: Exception => Left(()) }
    Future.successful(ret)
  })

}

object ZimWsApi {

  def apply()(implicit materializer: Materializer): ZimWsApi = new ZimWsApi()

}
