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
import zio._
import org.bitlap.zim.infrastructure.InfrastructureConfiguration

/** akka actor configuration
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 1.0
 */
object AkkaActorSystemConfiguration {

  /** create actorSystem，convert to classic actor when use it in akkahttp
   */
  private lazy val actorSystem: RIO[InfrastructureConfiguration, ActorSystem] =
    ZIO.attempt(ActorSystem("akkaActorSystem"))

  val live: RLayer[InfrastructureConfiguration, ActorSystem] = ZLayer(ZIO.scoped {
    ZIO.acquireRelease(actorSystem)(actorSystem => ZIO.succeed(actorSystem.terminate()).ignore)
  })

  def make: Task[ActorSystem] = AkkaActorSystemConfiguration.actorSystem.provideLayer(InfrastructureConfiguration.live)
}
