package org.bitlap.zim.domain.input
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

/**
 * @author 梦境迷离
 * @since 2022/2/5
 * @version 1.0
 */
case class UpdateSignInput(sign: String)

object UpdateSignInput {

  implicit val decoder: Decoder[UpdateSignInput] = deriveDecoder[UpdateSignInput]

}
