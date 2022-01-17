package org.bitlap.zim.server.actor.akka

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import io.circe.syntax.EncoderOps
import org.bitlap.zim.domain.ws.protocol.{ protocol, Command, TransmitMessageProxy }
import org.bitlap.zim.server.application.ws.wsService
import zio.{ UIO, ZIO }

/**
 * akka typed actor
 *
 * @author 梦境迷离
 * @version 1.0,2022/1/16
 */
object WsMessageForwardBehavior {

  lazy val zioRuntime: zio.Runtime[zio.ZEnv] = zio.Runtime.default

  def apply(): Behavior[Command[_]] =
    Behaviors.receiveMessage {
      case tm: TransmitMessageProxy =>
        val tpe = protocol.unStringify(Option(tm.getMessage).map(_.`type`).getOrElse(protocol.unHandMessage.stringify))
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
            UIO.effectTotal(())
        }
        zioRuntime.unsafeRun(zio)
        Behaviors.same
      case _ =>
        Behaviors.same
    }
}
