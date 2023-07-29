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

import scala.concurrent._

import org.bitlap.zim.api._
import org.bitlap.zim.server.service.ws._

import akka._
import akka.http.scaladsl.model.ws._
import akka.http.scaladsl.server._
import akka.stream._
import akka.stream.scaladsl.Flow

import sttp.tapir.server.akkahttp._

import zio._

/** @author
 *    梦境迷离
 *  @since 2022/1/16
 *  @version 1.0
 */
final class ZimWsApi()(implicit materializer: Materializer) {

  implicit val ec: ExecutionContext = materializer.executionContext

  lazy val route: Route = AkkaHttpServerInterpreter().toRoute(WsEndpoint.wsEndpoint.serverLogic[Future] { uid =>
    val ret: Either[Unit, Flow[Message, String, NotUsed]] =
      try
        Unsafe.unsafe { implicit runtime =>
          Right(Runtime.default.unsafe.run(WsService.openConnection(uid)).getOrThrowFiberFailure())
        }
      catch { case _: Exception => Left(()) }
    Future.successful(ret)
  })

}
