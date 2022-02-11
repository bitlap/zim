package org.bitlap.zim.server.configuration

import akka.actor
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.settings.ServerSettings
import akka.stream.Materializer
import akka.util.ByteString
import org.bitlap.zim.server.api.OpenApi
import org.bitlap.zim.server.configuration.AkkaActorSystemConfiguration.ZAkkaActorSystemConfiguration
import zio._
import org.bitlap.zim.server.ZMaterializer

import java.util.concurrent.atomic.AtomicInteger

/**
 * akka http configuration
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
final class AkkaHttpConfiguration(actorSystem: ActorSystem) {

  private lazy val imServerSettings: ServerSettings = {
    val defaultSettings = ServerSettings(actorSystem)
    val pingCounter = new AtomicInteger()
    val imWebsocketSettings = defaultSettings.websocketSettings.withPeriodicKeepAliveData(() =>
      ByteString(s"debug-ping-${pingCounter.incrementAndGet()}")
    )
    defaultSettings.withWebsocketSettings(imWebsocketSettings)
  }

  def httpServer(route: Route): Task[Unit] =
    for {
      infoConf <- InfrastructureConfiguration.zimConfigurationProperties
      eventualBinding <- Task {
        implicit lazy val untypedSystem: actor.ActorSystem = actorSystem
        Http()
          .newServerAt(
            infoConf.interface,
            infoConf.port
          )
          .withSettings(imServerSettings)
          .bind(route)
      }
      server <- Task
        .fromFuture(_ => eventualBinding)
        .tapError(exception =>
          UIO(
            actorSystem.log.error(
              s"Server could not start with parameters [host:port]=[${infoConf.interface},${infoConf.port}]",
              exception
            )
          )
        )
        .forever
        .fork
      _ <- UIO(
        actorSystem.log.info(
          s"""
             |Server online at http://${infoConf.interface}:${infoConf.port}/${OpenApi().openapi}
             |Websocket Server online at http://localhost:9000/api/v1.0/wsDocs
             |""".stripMargin
        )
      )
      _ <- server.join
    } yield ()

}

object AkkaHttpConfiguration {

  def apply(actorSystem: ActorSystem): AkkaHttpConfiguration =
    new AkkaHttpConfiguration(actorSystem)

  type ZAkkaHttpConfiguration = Has[AkkaHttpConfiguration]

  def httpServer(route: Route): RIO[ZAkkaHttpConfiguration with ZAkkaActorSystemConfiguration, Unit] =
    ZIO.accessM(_.get.httpServer(route))

  val materializerLive: URLayer[ZAkkaActorSystemConfiguration, ZMaterializer] =
    ZLayer.fromService[ActorSystem, Materializer](Materializer.matFromSystem(_))

  val live: URLayer[ZAkkaActorSystemConfiguration, ZAkkaHttpConfiguration] =
    ZLayer.fromService[ActorSystem, AkkaHttpConfiguration](AkkaHttpConfiguration(_))

}
