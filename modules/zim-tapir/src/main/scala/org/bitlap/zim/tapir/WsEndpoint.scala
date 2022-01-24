package org.bitlap.zim.tapir

import akka.http.scaladsl.model.ws.TextMessage
import akka.stream.scaladsl.Flow
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir.Codec.parsedString
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.{ endpoint, query, _ }
import sttp.ws.WebSocketFrame

import scala.concurrent.duration.DurationInt

/**
 * @author 梦境迷离
 * @since 2022/1/16
 * @version 2.0
 */
trait WsEndpoint {

  // TODO use domain, not TextMessage
  lazy val wsEndpoint: PublicEndpoint[Int, Unit, Flow[
    TextMessage.Strict,
    TextMessage.Strict,
    Any
  ], Any with AkkaStreams with WebSockets] =
    endpoint
      .in("websocket" / query[Int]("uid"))
      .description("Websocket Endpoint")
      .name("Websocket")
      .out(out)

  lazy val out: WebSocketBodyOutput[Flow[
    TextMessage.Strict,
    TextMessage.Strict,
    Any
  ], TextMessage.Strict, TextMessage.Strict, Flow[
    TextMessage.Strict,
    TextMessage.Strict,
    Any
  ], AkkaStreams] =
    new WebSocketBodyBuilder[TextMessage.Strict, CodecFormat, TextMessage.Strict, CodecFormat]
      .apply(AkkaStreams)(
        requests = Codec.textWebSocketFrame(
          parsedString[TextMessage.Strict](t => TextMessage.Strict(t))
            .schema(implicitly[Schema[TextMessage.Strict]])
        ),
        responses = Codec.textWebSocketFrame(
          parsedString[TextMessage.Strict](t => TextMessage.Strict(t))
            .schema(implicitly[Schema[TextMessage.Strict]])
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
