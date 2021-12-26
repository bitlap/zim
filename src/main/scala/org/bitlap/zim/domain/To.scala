package org.bitlap.zim.domain

import io.circe.generic.semiauto._
import io.circe.{ Decoder, Encoder }

/** 发送给...的信息
 *
 * @param id       对方的id
 * @param username 名字
 * @param sign     签名
 * @param avatar   头像
 * @param status   状态
 * @param `type`   聊天类型，一般分friend和group两种，group即群聊
 */
case class To(
  id: Int,
  username: String,
  sign: String,
  avatar: String,
  status: String,
  `type`: String
)
object To {

  implicit val decoder: Decoder[To] = deriveDecoder[To]
  implicit val encoder: Encoder[To] = deriveEncoder[To]

}
