package org.bitlap.zim.server.actor

import org.bitlap.zim.domain.ws.protocol.{ Command, OnlineUserMessage }
import org.bitlap.zim.server.application.ws.WsService
import org.bitlap.zim.server.util.LogUtil
import zio.actors.Actor.Stateful
import zio.actors.Context
import zio.{ UIO, ZIO }

/**
 * zio actor
 *
 * @author 梦境迷离
 * @version 1.0,2022/1/11
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
