package org.bitlap.zim.tapir

import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.capabilities.akka.AkkaStreams.Pipe
import sttp.tapir.Codec.parsedString
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.{ endpoint, query, _ }
import sttp.ws.WebSocketFrame

import scala.concurrent.duration.DurationInt
import akka.http.scaladsl.model.ws.TextMessage

/**
 * @author 梦境迷离
 * @since 2022/1/16
 * @version 2.0
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
