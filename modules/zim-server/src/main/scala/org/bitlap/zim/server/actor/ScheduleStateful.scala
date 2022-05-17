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

package org.bitlap.zim.server.actor

import org.bitlap.zim.domain.ws.protocol.{ Command, OnlineUserMessage }
import org.bitlap.zim.infrastructure.util.LogUtil
import org.bitlap.zim.server.service.ws.WsService
import zio.actors.Actor.Stateful
import zio.actors.Context
import zio.{ UIO, ZIO }

/** zio actor
 *
 *  @author
 *    梦境迷离
 *  @version 1.0,2022/1/11
 */
object ScheduleStateful {

  val stateful: Stateful[Any, Unit, Command] = new Stateful[Any, Unit, Command] {

    override def receive[A](state: Unit, msg: Command[A], context: Context): UIO[(Unit, A)] = {
      val taskIO = msg match {
        case OnlineUserMessage(descr) =>
          WsService.getConnections.flatMap { i =>
            LogUtil.debug(s"${descr.getOrElse("receive")} Total online user => $i")
          }
        case _ => UIO.unit
      }

      taskIO.foldM(
        e => LogUtil.error(s"ScheduleStateful $e").as(() -> "".asInstanceOf[A]),
        _ => ZIO.succeed(() -> "".asInstanceOf[A])
      )
    }
  }
}
