package org.bitlap.zim.domain.ws.protocol

import akka.actor.ActorRef
import io.circe.jawn
import org.bitlap.zim.domain.Message

/**
 * ws actor command
 */
sealed trait Command[+Any]

/**
 * proxy
 * @param uId
 * @param  msg Now, default type is `String`, we use toJson to convert anything, should fix it in the future.
 * @param originActorRef
 */
case class TransmitMessageProxy(
  uId: Int,
  msg: String,
  originActorRef: Option[ActorRef]
) extends Command[String] {

  def getMessage: Message = jawn.decode[Message](msg).getOrElse(null)

}

/**
 * 在线用户
 */
case class OnlineUserMessage() extends Command[Unit]

/**
 * 用户状态变更
 */
case class UserStatusChange(uId: Int, typ: String) extends Command[Unit]

/**
 * DONE
 */
case class Done() extends Command[String]
