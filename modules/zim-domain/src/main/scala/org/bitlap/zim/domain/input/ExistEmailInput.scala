package org.bitlap.zim.domain.input

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

/**
 * 校验注册邮件 输入
 * @param email
 */
case class ExistEmailInput(email: String)

object ExistEmailInput {

  implicit val decoder: Decoder[ExistEmailInput] = deriveDecoder[ExistEmailInput]
}
