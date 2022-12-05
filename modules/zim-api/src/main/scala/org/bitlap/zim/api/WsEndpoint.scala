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

package org.bitlap.zim.api

import akka.http.scaladsl.model.ws.TextMessage
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.capabilities.akka.AkkaStreams.Pipe
import sttp.tapir.Codec.parsedString
import sttp.tapir._
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.ws.WebSocketFrame

import scala.concurrent.duration.DurationInt

/** @author
 *    梦境迷离
 *  @since 2022/1/16
 *  @version 2.0
 */
trait WsEndpoint {

  // TODO typed ws
  lazy val wsEndpoint
    : PublicEndpoint[Int, Unit, Pipe[TextMessage.Strict, String], Any with AkkaStreams with WebSockets] =
    endpoint
      .in("websocket" / query[Int]("uid"))
      .description("Websocket Endpoint")
      .name("Websocket")
      .out(out)

  lazy val out: WebSocketBodyOutput[Pipe[TextMessage.Strict, String], TextMessage.Strict, String, Pipe[
    TextMessage.Strict,
    String
  ], AkkaStreams] =
    new WebSocketBodyBuilder[TextMessage.Strict, CodecFormat.TextPlain, String, CodecFormat.TextPlain]
      .apply(AkkaStreams)(
        requests = Codec.textWebSocketFrame(
          parsedString[TextMessage.Strict](TextMessage.Strict)
            .schema(implicitly[Schema[TextMessage.Strict]])
        ),
        responses = Codec.textWebSocketFrame(
          Codec.string
        )
      )
      .concatenateFragmentedFrames(false)
      .ignorePong(false)
      .autoPongOnPing(true)
      .decodeCloseRequests(true)
      .decodeCloseResponses(true)
      .autoPing(Some((3.seconds, WebSocketFrame.ping)))
}

object WsEndpoint extends WsEndpoint
