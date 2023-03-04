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

import akka.actor.typed.ActorSystem
import zio._

/** akka actor configuration
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 2.0
 */
object AkkaModule {

  private lazy val akkaActorSystem: ActorSystem[Nothing] = ActorSystem.wrap(akka.actor.ActorSystem("akka_actor_system"))

  private lazy val wsAkkaActorSystem: ActorSystem[Nothing] =
    ActorSystem.wrap(akka.actor.ActorSystem("ws_akka_actor_system"))

  lazy val live: ZLayer[Any with Any with Scope, Throwable, ActorSystem[Nothing]] =
    ZLayer.fromZIO(ZIO.acquireRelease(ZIO.attempt(akkaActorSystem))(a => ZIO.attempt(a.terminate()).ignore))

  lazy val wslive: ZLayer[Any, Throwable, ActorSystem[Nothing]] =
    ZLayer.fromZIO(ZIO.attempt(wsAkkaActorSystem))

  def make: ZIO[Any, Throwable, ActorSystem[Nothing]] = Scope.global.use {
    ZIO.service[ActorSystem[Nothing]].provideLayer(wslive)
  }
}
