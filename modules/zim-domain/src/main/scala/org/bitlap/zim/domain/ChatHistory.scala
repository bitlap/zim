package org.bitlap.zim.domain

import io.circe._
import io.circe.generic.semiauto._

/**
 * 聊天记录
 *
 * @param id        用户id
 * @param username  用户名
 * @param avatar    用户头像
 * @param content   消息内容
 * @param timestamp 时间
 */
case class ChatHistory(
  id: Int,
  username: String,
  avatar: String,
  content: String,
  timestamp: Long
)

object ChatHistory {

  implicit val decoder: Decoder[ChatHistory] = deriveDecoder[ChatHistory]
  implicit val encoder: Encoder[ChatHistory] = deriveEncoder[ChatHistory]

}
