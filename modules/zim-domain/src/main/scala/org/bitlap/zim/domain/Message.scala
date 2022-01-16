package org.bitlap.zim.domain

import io.circe.generic.semiauto._
import io.circe.{ Decoder, Encoder }

/**
 * 消息
 *
 * @see table:t_message
 * @param `type` 随便定义，用于在服务端区分消息类型
 * @param mine   我的信息
 * @param to     对方信息
 * @param msg    额外的信息
 */
case class Message(`type`: String, mine: Mine, to: To, msg: String)

object Message {

  implicit val decoder: Decoder[Message] = deriveDecoder[Message]
  implicit val encoder: Encoder[Message] = deriveEncoder[Message]
}
