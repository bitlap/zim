package org.bitlap.zim.domain.ws.protocol

import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }
import io.circe.{ Decoder, Encoder }
import org.bitlap.zim.domain.Mine

/**
 * 同意添加群
 */
case class AddRefuseMessage(toUid: Int, groupId: Int, messageBoxId: Int, mine: Mine)

object AddRefuseMessage {

  implicit val decoder: Decoder[AddRefuseMessage] = deriveDecoder[AddRefuseMessage]
  implicit val encoder: Encoder[AddRefuseMessage] = deriveEncoder[AddRefuseMessage]

}
