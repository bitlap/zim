package org.bitlap.zim.domain.ws.protocol

import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }
import io.circe.{ Decoder, Encoder }

/**
 * 添加群信息
 */
case class Group(groupId: Int, remark: String)

object Group {
  implicit val decoder: Decoder[Group] = deriveDecoder[Group]
  implicit val encoder: Encoder[Group] = deriveEncoder[Group]
}
