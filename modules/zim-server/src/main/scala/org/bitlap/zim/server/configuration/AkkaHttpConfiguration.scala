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

package org.bitlap.zim.server.configuration

import akka.actor
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.settings.ServerSettings
import akka.stream.Materializer
import akka.util.ByteString
import org.bitlap.zim.domain.ws.protocol.OnlineUserMessage
import org.bitlap.zim.server.ZMaterializer
import org.bitlap.zim.server.api.ZimOpenApi
import org.bitlap.zim.server.configuration.AkkaActorSystemConfiguration.ZAkkaActorSystemConfiguration
import org.bitlap.zim.server.util.LogUtil
import zio._
import zio.clock.Clock

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
             |Server online at http://${infoConf.webHost}:${infoConf.port}/${ZimOpenApi.zimOpenApiInstance.openapi}
             |Websocket Server online at http://${infoConf.interface}:${infoConf.port}/api/v1.0/wsDocs
             |""".stripMargin
        )
      )
      _ <- scheduleTask
      _ <- server.join
    } yield ()

  def scheduleTask: Task[Unit] = {
    val task = ZioActorSystemConfiguration.scheduleActor
      .flatMap(f => f ! OnlineUserMessage(Some("scheduleTask"))) repeat Schedule.secondOfMinute(0)

    task
      .foldM(
        e => LogUtil.error(s"error => $e").unit,
        _ => UIO.unit
      )
      .provideLayer(Clock.live)
  }
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
