package org.bitlap.zim.server.actor
import org.bitlap.zim.domain.ws.protocol.{ Command, UserStatusChange }
import org.bitlap.zim.server.application.ws.wsService
import zio.UIO
import zio.actors.Actor.Stateful
import zio.actors.Context

/**
 * zio actor
 *
 * @author 梦境迷离
 * @version 1.0,2022/1/11
 */
object UserStatusStateful {

  val stateful: Stateful[Any, Unit, Command] = new Stateful[Any, Unit, Command] {

    override def receive[A](state: Unit, msg: Command[A], context: Context): UIO[(Unit, A)] = {
      val taskIO = msg match {
        case _ @UserStatusChange(uId, typ) =>
          wsService.changeOnline(uId, typ)
        case _ => UIO.effectTotal(false)
      }
      taskIO.orDie.as((), "".asInstanceOf[A])
    }
  }
}
