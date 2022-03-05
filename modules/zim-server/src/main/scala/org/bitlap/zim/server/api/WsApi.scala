package org.bitlap.zim.server.api
import akka.NotUsed
import akka.http.scaladsl.model.ws.{ Message, TextMessage }
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import org.bitlap.zim.tapir.WsEndpoint
import org.bitlap.zim.server.application.ws.WsService
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.bitlap.zim.server.zioRuntime

/**
 * @author 梦境迷离
 * @since 2022/1/16
 * @version 1.0
 */
final class WsApi()(implicit materializer: Materializer) {

  lazy val route: Route = AkkaHttpServerInterpreter().toRoute(WsEndpoint.wsEndpoint.serverLogic[Future] { uid =>
    val ret: Right[Nothing, Flow[Message, String, NotUsed]] =
      Right(zioRuntime.unsafeRun(WsService.openConnection(uid)))
    val either = ret.withLeft[Unit]
    Future.apply(either)
  })

}

object WsApi {

  def apply()(implicit materializer: Materializer): WsApi = new WsApi()

}
