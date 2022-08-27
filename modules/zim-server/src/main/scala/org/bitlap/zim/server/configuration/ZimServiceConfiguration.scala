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

import akka.actor.ActorSystem
import akka.stream._
import org.bitlap.zim.infrastructure._
import zio.{ TaskLayer, ULayer, ZLayer }

/** global configuration to collect all service or system layer
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 1.0
 */
trait ZimServiceConfiguration {

  protected lazy val akkaActorSystemLayer: TaskLayer[ActorSystem] =
    InfrastructureConfiguration.live >>>
      AkkaActorSystemConfiguration.live

  protected lazy val akkaHttpConfigurationLayer: TaskLayer[AkkaHttpConfiguration] =
    akkaActorSystemLayer >>> AkkaHttpConfiguration.live

  protected lazy val materializerLayer: TaskLayer[Materializer] =
    akkaActorSystemLayer >>>
      AkkaHttpConfiguration.materializerLive

  protected lazy val applicationConfigurationLayer: ULayer[ApplicationConfiguration] =
    ZLayer.make[ApplicationConfiguration](InfrastructureConfiguration.live, ApplicationConfiguration.live)

  protected lazy val apiConfigurationLayer: TaskLayer[ApiConfiguration with AkkaHttpConfiguration] =
    ZLayer.make[ApiConfiguration with AkkaHttpConfiguration](
      applicationConfigurationLayer,
      akkaHttpConfigurationLayer,
      materializerLayer,
      ApiConfiguration.live
    )
}
