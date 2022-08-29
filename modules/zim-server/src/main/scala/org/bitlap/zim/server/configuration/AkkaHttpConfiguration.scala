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

import akka._
import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.server._
import akka.http.scaladsl.settings._
import akka.stream._
import akka.util._
import org.bitlap.zim.infrastructure._
import org.bitlap.zim.server.route._
import zio._

import java.util.concurrent.atomic._

/** akka http configuration
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 1.0
 */
final class AkkaHttpConfiguration(actorSystem: ActorSystem) {

  private lazy val imServerSettings: ServerSettings = {
    val defaultSettings = ServerSettings(actorSystem)
    val pingCounter     = new AtomicInteger()
    val imWebsocketSettings = defaultSettings.websocketSettings.withPeriodicKeepAliveData(() =>
      ByteString(s"debug-ping-${pingCounter.incrementAndGet()}")
    )
    defaultSettings.withWebsocketSettings(imWebsocketSettings)
  }

  def httpServer(route: Route): Task[Unit] =
    for {
      infoConf <- InfrastructureConfiguration.zimConfigurationProperties
      eventualBinding <- ZIO.attempt {
        implicit lazy val untypedSystem: actor.ActorSystem = actorSystem
        Http()
          .newServerAt(
            infoConf.interface,
            infoConf.port
          )
          .withSettings(imServerSettings)
          .bind(route)
      }
      server <- ZIO
        .fromFuture(implicit ec => eventualBinding)
        .tapError(exception =>
          ZIO.attempt(
            actorSystem.log.error(
              s"Server could not start with parameters [host:port]=[${infoConf.interface},${infoConf.port}]",
              exception
            )
          )
        )
        .forever
        .fork
      _ <- ZIO.attempt(
        actorSystem.log.info(
          s"""
             |Server online at http://${infoConf.webHost}:${infoConf.port}/${ZimOpenApi.zimOpenApiInstance.openapi}
             |Websocket Server online at http://${infoConf.webHost}:${infoConf.port}/api/v1.0/wsDocs
             |""".stripMargin
        )
      )
//      _ <- scheduleTask
      _ <- server.join
    } yield ()

  // zio-actors不支持zio2.0
//  def scheduleTask: Task[Unit] = {
//    val task = ZioActorSystemConfiguration.scheduleActor
//      .flatMap(f => f ! OnlineUserMessage(Some("scheduleTask"))) repeat Schedule.secondOfMinute(0)
//
//    task
//      .foldM(
//        e => LogUtil.error(s"error => $e").unit,
//        _ => UIO.unit
//      )
//      .provideLayer(Clock.live)
//  }
}

object AkkaHttpConfiguration {

  def apply(actorSystem: ActorSystem): AkkaHttpConfiguration =
    new AkkaHttpConfiguration(actorSystem)

  def httpServer(route: Route): RIO[AkkaHttpConfiguration, Unit] =
    ZIO.environmentWithZIO(_.get.httpServer(route))

  lazy val materializerLive: URLayer[ActorSystem, Materializer] = ZLayer {
    ZIO.service[ActorSystem].map(implicit actor => Materializer.matFromSystem)
  }

  lazy val live: URLayer[ActorSystem, AkkaHttpConfiguration] = ZLayer {
    ZIO.service[ActorSystem].map(AkkaHttpConfiguration.apply)
  }

}
