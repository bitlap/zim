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

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import org.bitlap.zim.server.configuration.ApplicationConfiguration.ZApplicationConfiguration
import org.bitlap.zim.server.ZMaterializer
import org.bitlap.zim.server.route.{ ZimActuatorApi, ZimOpenApi, ZimUserApi, ZimWsApi }
import zio._

/** api configuration
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 1.0
 */
final class ApiConfiguration(applicationConfiguration: ApplicationConfiguration)(implicit materializer: Materializer) {

  val zimUserApi: ZimUserApi = ZimUserApi(applicationConfiguration.apiApplication)

  val zimActuatorApi: ZimActuatorApi = ZimActuatorApi()

  val zimOpenApi: ZimOpenApi = ZimOpenApi.zimOpenApiInstance

  val wsApi: ZimWsApi = ZimWsApi()

}

/** api dependencies
 */
object ApiConfiguration {

  def apply(applicationConfiguration: ApplicationConfiguration, materializer: Materializer): ApiConfiguration =
    new ApiConfiguration(applicationConfiguration)(materializer)

  type ZApiConfiguration = Has[ApiConfiguration]

  val routes: URIO[ZApiConfiguration, Route] =
    for {
      userRoute     <- ZIO.access[ZApiConfiguration](_.get.zimUserApi.route)
      actuatorRoute <- ZIO.access[ZApiConfiguration](_.get.zimActuatorApi.route)
      openRoute     <- ZIO.access[ZApiConfiguration](_.get.zimOpenApi.route)
      wsDocsRoute   <- ZIO.access[ZApiConfiguration](_.get.zimOpenApi.wsDocsRoute)
      wsRoute       <- ZIO.access[ZApiConfiguration](_.get.wsApi.route)
    } yield openRoute ~ actuatorRoute ~ wsRoute ~ wsDocsRoute ~ userRoute

  val live: RLayer[ZApplicationConfiguration with ZMaterializer, ZApiConfiguration] =
    ZLayer.fromServices[ApplicationConfiguration, Materializer, ApiConfiguration](ApiConfiguration(_, _))

  def make(
    applicationConfiguration: ApplicationConfiguration,
    materializer: Materializer
  ): TaskLayer[ZApiConfiguration] =
    ZLayer.succeed(applicationConfiguration) ++ ZLayer.succeed(materializer) >>> live

}
