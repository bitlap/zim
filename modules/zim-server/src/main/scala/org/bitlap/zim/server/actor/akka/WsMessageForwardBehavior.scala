/*
 * Copyright 2023 bitlap
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

package org.bitlap.zim.server.actor.akka

import org.bitlap.zim.domain.ws.protocol._
import org.bitlap.zim.infrastructure.util.LogUtil
import org.bitlap.zim.server.service.ws.WsService

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import io.circe.syntax.EncoderOps

import zio._

/** akka typed actor
 *
 *  @author
 *    梦境迷离
 *  @version 1.0,2022/1/16
 */
object WsMessageForwardBehavior {

  def apply(): Behavior[Command[_]] =
    Behaviors.receiveMessage {
      case tm: TransmitMessageProxy =>
        val tpe = Protocol.unStringify(Option(tm.getMessage).map(_.`type`).getOrElse(Protocol.unHandMessage.stringify))
        Unsafe.unsafe { implicit runtime =>
          Runtime.default.unsafe.run(LogUtil.info(s"TransmitMessageProxy=>$tm, type=>$tpe")).getOrThrowFiberFailure()
        }
        val zio = tpe match {
          case Protocol.readOfflineMessage => WsService.readOfflineMessage(tm.getMessage)
          case Protocol.message            => WsService.sendMessage(tm.getMessage)
          case Protocol.checkOnline =>
            WsService.checkOnline(tm.getMessage).flatMap { f =>
              tm.originActorRef.fold(ZIO.attempt(()))(a => WsService.sendMessage(f.asJson.noSpaces, a))
            }
          case Protocol.addGroup => WsService.addGroup(tm.uId, tm.getMessage)
          case Protocol.changOnline =>
            WsService.changeOnline(tm.uId, tm.getMessage.msg)
          case Protocol.addFriend => WsService.addFriend(tm.uId, tm.getMessage)
          case Protocol.agreeAddFriend =>
            val actor = WsService.actorRefSessions.get(tm.getMessage.to.id)
            WsService.sendMessage(tm.msg, actor).unless(actor == null)
          case Protocol.agreeAddGroup  => WsService.agreeAddGroup(tm.getMessage)
          case Protocol.refuseAddGroup => WsService.refuseAddGroup(tm.getMessage)
          case Protocol.unHandMessage =>
            WsService.countUnHandMessage(tm.uId).flatMap { f =>
              tm.originActorRef.fold(ZIO.attempt(()))(a => WsService.sendMessage(f.asJson.noSpaces, a))
            }
          case Protocol.delFriend => WsService.removeFriend(tm.uId, tm.getMessage.to.id)
          case _ =>
            ZIO.unit
        }

        Unsafe.unsafe { implicit runtime =>
          Runtime.default.unsafe.run(zio).getOrThrowFiberFailure()
        }
        Behaviors.same
      case _ =>
        Behaviors.same
    }
}
