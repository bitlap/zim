package org.bitlap.zim.domain.input

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

/**
 * @author 梦境迷离
 * @since 2022/2/5
 * @version 1.0
 */
final case class ChangeGroupInput(groupId: Int, userId: Int)

object ChangeGroupInput {

  implicit val decoder: Decoder[ChangeGroupInput] = deriveDecoder[ChangeGroupInput]

}
