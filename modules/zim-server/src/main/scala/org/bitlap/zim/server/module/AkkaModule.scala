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

import scala.concurrent.Future

import akka.actor.typed.ActorSystem
import akka.stream.Materializer
import org.bitlap.zim.infrastructure._
import zio._

/** akka actor
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 2.0
 */
object AkkaModule {

  // we do not need guardian actor.
  private lazy val akkaActorSystem = ActorSystem.wrap(akka.actor.ActorSystem("akkaActorSystem"))

  // akka system
  lazy val live: TaskLayer[ActorSystem[_]] =
    InfrastructureConfiguration.layer >>> ZLayer.scoped {
      ZIO.acquireReleaseExit(ZIO.attempt(akkaActorSystem))((release, _) =>
        ZIO.fromFuture(implicit ec => Future(release.terminate())).ignore
      )
    }

  lazy val materializerLive: URLayer[ActorSystem[_], Materializer] = ZLayer {
    ZIO.service[ActorSystem[_]].map(implicit actor => Materializer.matFromSystem)
  }

  def make: Task[ActorSystem[_]] = ZIO.succeed(akkaActorSystem)
}
