package org.bitlap.zim.domain.input

import io.circe.Decoder
import io.circe.generic.semiauto._

/**
 * 用户信息提交 输入
 *
 * @param id
 * @param username 用户名
 * @param password 密码
 * @param oldpwd   旧密码
 * @param sign     签名
 * @param sex      性别
 */
case class UpdateUserInput(
  id: Int,
  username: String,
  password: String,
  oldpwd: String,
  sign: String,
  sex: String
)
object UpdateUserInput {

  implicit val decoder: Decoder[UpdateUserInput] = deriveDecoder[UpdateUserInput]
}
