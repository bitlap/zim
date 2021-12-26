package org.bitlap.zim.domain

import io.circe.generic.semiauto._
import io.circe.{ Decoder, Encoder }

/** 群组
 *
 * @param id        群组id
 * @param groupname 群组名
 */
@SerialVersionUID(1L) class Group(val id: Int, val groupname: String)

object Group {

  implicit val decoder: Decoder[Group] = deriveDecoder[Group]
  implicit val encoder: Encoder[Group] = deriveEncoder[Group]

}
