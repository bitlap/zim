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

package org.bitlap.zim.server.module

import java.util.concurrent.atomic._

import akka.actor.typed._
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.settings._
import akka.stream._
import akka.util._
import org.bitlap.zim.domain.ws.protocol._
import org.bitlap.zim.infrastructure.properties.ZimConfigurationProperties
import org.bitlap.zim.infrastructure.util._
import org.bitlap.zim.server.route._
import zio._

/** akka http configuration
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 1.0
 */
final class AkkaHttpModule(serviceModule: ServiceModule) {

  def httpServer(): ZIO[ActorSystem[Nothing] with ZimConfigurationProperties with Scope, Throwable, Unit] =
    for {
      actorSystem <- ZIO.service[ActorSystem[Nothing]]
      imServerSettings <- {
        val defaultSettings = ServerSettings(actorSystem)
        val pingCounter     = new AtomicInteger()
        val imWebsocketSettings = defaultSettings.websocketSettings.withPeriodicKeepAliveData(() =>
          ByteString(s"debug-ping-${pingCounter.incrementAndGet()}")
        )
        ZIO.succeed(defaultSettings.withWebsocketSettings(imWebsocketSettings))
      }
      sys = actorSystem.classicSystem
      m   = Materializer.matFromSystem(sys)
      route <- ZIO.attempt(
        ZimOpenApi.zimOpenApiInstance.route ~ ZimActuatorApi().route ~ new ZimWsApi()(
          m
        ).route ~ ZimOpenApi.zimOpenApiInstance.wsDocsRoute ~ ZimUserApi(serviceModule.apiService)(m).route
      )
      interface <- ZIO.serviceWith[ZimConfigurationProperties](_.interface)
      port      <- ZIO.serviceWith[ZimConfigurationProperties](_.port)
      webHost   <- ZIO.serviceWith[ZimConfigurationProperties](_.webHost)
      eventualBinding <- ZIO.attempt {
        implicit val s = sys
        Http()
          .newServerAt(
            interface,
            port
          )
          .withSettings(imServerSettings)
          .bind(route)
      }
      server <- ZIO
        .fromFuture(implicit ec => eventualBinding)
        .tapError(exception =>
          ZIO.attempt(
            actorSystem.log.error(
              s"Server could not start with parameters [host:port]=[$interface,$port]",
              exception
            )
          )
        )
        .forever
        .fork
      _ <- ZIO.attempt(
        actorSystem.log.info(
          s"""
             |Server online at http://${webHost}:${port}/${ZimOpenApi.zimOpenApiInstance.openapi}
             |Websocket Server online at http://${webHost}:${port}/api/v1.0/wsDocs
             |""".stripMargin
        )
      )
      _ <- scheduleTask()
      _ <- server.join
    } yield ()

  private def scheduleTask(): ZIO[Scope, Nothing, Unit] = {
    val task = ZioActorModule.scheduleActor
      .flatMap(f => f ! OnlineUserMessage(Some("scheduleTask"))) repeat Schedule.secondOfMinute(0)
    task
      .foldZIO(
        e => LogUtil.error(s"Found error => $e").unit,
        _ => ZIO.unit
      )
  }
}

object AkkaHttpModule {

  lazy val live: URLayer[ServiceModule, AkkaHttpModule] =
    ZLayer.fromFunction((am: ServiceModule) => new AkkaHttpModule(am))
}
