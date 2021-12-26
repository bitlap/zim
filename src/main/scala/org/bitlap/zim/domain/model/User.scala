package org.bitlap.zim.domain.model

import io.circe._
import io.circe.generic.semiauto._

/**
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
case class User(id: Option[Long], username: String)

object User {

  // 由于经过了中间处理，需要显示调用解码器
  implicit val userDecoder: Decoder[User] = deriveDecoder[User]
  implicit val userEncoder: Encoder[User] = deriveEncoder[User]

}