package org.bitlap.zim.server.configuration

import akka.actor
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import org.bitlap.zim.server.api.OpenApi
import org.bitlap.zim.server.configuration.ActorSystemConfiguration.ZActorSystemConfiguration
import zio._

/**
 * akka http配置
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
final class AkkaHttpConfiguration(actorSystem: ActorSystem) {
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
          s"Server online at http://${infoConf.interface}:${infoConf.port}/${OpenApi().openapi}"
        )
      )
      _ <- server.join
    } yield ()

}

object AkkaHttpConfiguration {

  def apply(actorSystem: ActorSystem): AkkaHttpConfiguration =
    new AkkaHttpConfiguration(actorSystem)

  type ZAkkaHttpConfiguration = Has[AkkaHttpConfiguration]
  type ZMaterializer = Has[Materializer]

  def httpServer(route: Route): RIO[ZAkkaHttpConfiguration with ZActorSystemConfiguration, Unit] =
    ZIO.accessM(_.get.httpServer(route))

  val materializerLive: URLayer[ZActorSystemConfiguration, ZMaterializer] =
    ZLayer.fromService[ActorSystem, Materializer](Materializer.matFromSystem(_))

  val live: URLayer[ZActorSystemConfiguration, ZAkkaHttpConfiguration] =
    ZLayer.fromService[ActorSystem, AkkaHttpConfiguration](AkkaHttpConfiguration(_))

}
