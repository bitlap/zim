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

import akka.actor.typed.ActorSystem
import org.bitlap.zim.infrastructure._
import zio._

import scala.concurrent.Future

/** akka actor configuration
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 2.0
 */
object AkkaActorSystemConfiguration {

  // we do not need guardian actor.
  private lazy val akkaActorSystem = ActorSystem.wrap(akka.actor.ActorSystem("akkaActorSystem"))

  // akka http
  lazy val live: TaskLayer[ActorSystem[_]] =
    InfrastructureConfiguration.layer >>> ZLayer.scoped {
      ZIO.acquireReleaseExit(ZIO.attempt(akkaActorSystem))((release, _) =>
        ZIO.fromFuture(implicit ec => Future(release.terminate())).ignore
      )
    }

  def make: Task[ActorSystem[_]] = ZIO.succeed(akkaActorSystem)
}
