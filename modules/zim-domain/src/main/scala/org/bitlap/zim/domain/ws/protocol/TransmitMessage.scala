package org.bitlap.zim.domain.ws.protocol

import akka.actor.ActorRef
import io.circe.parser.decode
import org.bitlap.zim.domain.Message

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
