package org.bitlap.zim.actor.protocol

import akka.actor.ActorRef
import org.bitlap.zim.domain.Message
import io.circe.parser.decode

/**
 * 消息转发
 *
 * @param uId
 * @param msg
 * @param originActorRef
 */
case class TransmitMessage(uId: Int, msg: String, originActorRef: ActorRef) {

  def getMessage: Message = decode[Message](msg).getOrElse(null)
}
