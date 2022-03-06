/*
 * Copyright 2021 bitlap
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

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import io.circe.syntax.EncoderOps
import org.bitlap.zim.domain.ws.protocol.{ protocol, Command, TransmitMessageProxy }
import org.bitlap.zim.server.application.ws.wsService
import org.bitlap.zim.server.util.LogUtil
import zio.{ UIO, ZIO }
import org.bitlap.zim.server.zioRuntime

/**
 * akka typed actor
 *
 * @author 梦境迷离
 * @version 1.0,2022/1/16
 */
object WsMessageForwardBehavior {

  def apply(): Behavior[Command[_]] =
    Behaviors.receiveMessage {
      case tm: TransmitMessageProxy =>
        val tpe = protocol.unStringify(Option(tm.getMessage).map(_.`type`).getOrElse(protocol.unHandMessage.stringify))
        zioRuntime.unsafeRun(LogUtil.info(s"TransmitMessageProxy=>$tm, type=>$tpe"))
        val zio = tpe match {
          case protocol.readOfflineMessage => wsService.readOfflineMessage(tm.getMessage)
          case protocol.message            => wsService.sendMessage(tm.getMessage)
          case protocol.checkOnline =>
            wsService.checkOnline(tm.getMessage).flatMap { f =>
              tm.originActorRef.fold(ZIO.effect(()))(a => wsService.sendMessage(f.asJson.noSpaces, a))
            }
          case protocol.addGroup => wsService.addGroup(tm.uId, tm.getMessage)
          case protocol.changOnline =>
            wsService.changeOnline(tm.uId, tm.getMessage.msg)
          case protocol.addFriend => wsService.addFriend(tm.uId, tm.getMessage)
          case protocol.agreeAddFriend =>
            val actor = wsService.WsService.actorRefSessions.get(tm.getMessage.to.id)
            wsService.sendMessage(tm.msg, actor).unless(actor == null)
          case protocol.agreeAddGroup  => wsService.agreeAddGroup(tm.getMessage)
          case protocol.refuseAddGroup => wsService.refuseAddGroup(tm.getMessage)
          case protocol.unHandMessage =>
            wsService.countUnHandMessage(tm.uId).flatMap { f =>
              tm.originActorRef.fold(ZIO.effect(()))(a => wsService.sendMessage(f.asJson.noSpaces, a))
            }
          case protocol.delFriend => wsService.removeFriend(tm.uId, tm.getMessage.to.id)
          case _ =>
            UIO.unit
        }
        zioRuntime.unsafeRun(zio)
        Behaviors.same
      case _ =>
        Behaviors.same
    }
}
