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
import akka.http.scaladsl.server._
import akka.stream._
import org.bitlap.zim.server.route._
import zio._

/** api configuration
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 1.0
 */
final class ApiConfiguration(applicationConfiguration: ApplicationConfiguration)(implicit materializer: Materializer) {

  val zimUserApi: ZimUserApi = ZimUserApi(applicationConfiguration.apiService)

  val zimActuatorApi: ZimActuatorApi = ZimActuatorApi()

  val zimOpenApi: ZimOpenApi = ZimOpenApi.zimOpenApiInstance

  val wsApi: ZimWsApi = ZimWsApi()

}

/** api dependencies
 */
object ApiConfiguration {

  def apply(applicationConfiguration: ApplicationConfiguration, materializer: Materializer): ApiConfiguration =
    new ApiConfiguration(applicationConfiguration)(materializer)

  val routes: URIO[ApiConfiguration, Route] =
    for {
      userRoute     <- ZIO.environmentWith[ApiConfiguration](_.get.zimUserApi.route)
      actuatorRoute <- ZIO.environmentWith[ApiConfiguration](_.get.zimActuatorApi.route)
      openRoute     <- ZIO.environmentWith[ApiConfiguration](_.get.zimOpenApi.route)
      wsDocsRoute   <- ZIO.environmentWith[ApiConfiguration](_.get.zimOpenApi.wsDocsRoute)
      wsRoute       <- ZIO.environmentWith[ApiConfiguration](_.get.wsApi.route)
    } yield openRoute ~ actuatorRoute ~ wsRoute ~ wsDocsRoute ~ userRoute

  val live: RLayer[ApplicationConfiguration with Materializer, ApiConfiguration] = ZLayer {
    for {
      app          <- ZIO.service[ApplicationConfiguration]
      materializer <- ZIO.service[Materializer]
    } yield ApiConfiguration(app, materializer)
  }

  def make(
    applicationConfiguration: ApplicationConfiguration,
    materializer: Materializer
  ): TaskLayer[ApiConfiguration] =
    ZLayer.succeed(applicationConfiguration) ++ ZLayer.succeed(materializer) >>> live

}
