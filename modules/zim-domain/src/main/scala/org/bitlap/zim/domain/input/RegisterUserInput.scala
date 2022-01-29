package org.bitlap.zim.domain.input
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

/**
 * 注册用户信息提交 输入
 *
 * @param username 用户名
 * @param email 邮箱
 * @param password 密码
 */
case class RegisterUserInput(
  username: String,
  password: String,
  email: String
)
object RegisterUserInput {

  implicit val decoder: Decoder[RegisterUserInput] = deriveDecoder[RegisterUserInput]
}
