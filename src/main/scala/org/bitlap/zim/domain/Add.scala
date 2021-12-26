package org.bitlap.zim.domain

import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }
import io.circe.{ Decoder, Encoder }

/** 添加好友、群组
 *
 * @param groupId 好友列表id或群组id
 * @param remark  附言
 * @param `type`  类型，好友或群组
 */
case class Add(groupId: Int, remark: String, `type`: Int)

object Add {

  implicit val decoder: Decoder[Add] = deriveDecoder[Add]
  implicit val encoder: Encoder[Add] = deriveEncoder[Add]

}
