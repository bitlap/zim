package org.bitlap.zim.configuration

import akka.actor
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import org.bitlap.zim.api.OpenApi
import org.bitlap.zim.configuration.ActorSystemConfiguration.ZActorSystemConfiguration
import org.bitlap.zim.configuration.InfrastructureConfiguration.ZInfrastructureConfiguration
import zio._

/**
 * akka http配置
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
final class AkkaHttpConfiguration(infrastructureConfiguration: InfrastructureConfiguration, actorSystem: ActorSystem) {
  def httpServer(route: Route): Task[Unit] =
    for {
      eventualBinding <- Task {
        implicit lazy val untypedSystem: actor.ActorSystem = actorSystem
        Http()
          .newServerAt(
            infrastructureConfiguration.zimConfigurationProperties.interface,
            infrastructureConfiguration.zimConfigurationProperties.port
          )
          .bind(route)
      }
      server <- Task
        .fromFuture(_ => eventualBinding)
        .tapError(exception =>
          UIO(
            actorSystem.log.error(
              s"Server could not start with parameters [host:port]=[${infrastructureConfiguration.zimConfigurationProperties.interface},${infrastructureConfiguration.zimConfigurationProperties.port}]",
              exception
            )
          )
        )
        .forever
        .fork
      _ <- UIO(
        actorSystem.log.info(
          s"Server online at http://${infrastructureConfiguration.zimConfigurationProperties.interface}:${infrastructureConfiguration.zimConfigurationProperties.port}/${OpenApi().openapi}"
        )
      )
      _ <- server.join
    } yield ()

}

object AkkaHttpConfiguration {

  def apply(infrastructureConfiguration: InfrastructureConfiguration, actorSystem: ActorSystem): AkkaHttpConfiguration =
    new AkkaHttpConfiguration(infrastructureConfiguration, actorSystem)

  type ZAkkaHttpConfiguration = Has[AkkaHttpConfiguration]
  type ZMaterializer = Has[Materializer]

  def httpServer(route: Route): RIO[ZAkkaHttpConfiguration with ZActorSystemConfiguration, Unit] =
    ZIO.accessM(_.get.httpServer(route))

  val materializerLive: ZLayer[ZActorSystemConfiguration, Nothing, ZMaterializer] =
    ZLayer.fromService[ActorSystem, Materializer](Materializer.matFromSystem(_))

  val live: ZLayer[ZInfrastructureConfiguration with ZActorSystemConfiguration, Nothing, ZAkkaHttpConfiguration] =
    ZLayer.fromServices[InfrastructureConfiguration, ActorSystem, AkkaHttpConfiguration](AkkaHttpConfiguration(_, _))

}
