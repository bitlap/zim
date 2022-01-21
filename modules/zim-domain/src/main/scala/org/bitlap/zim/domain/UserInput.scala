package org.bitlap.zim.domain

import io.circe.Decoder
import io.circe.generic.semiauto._

/**
 * 返回个人信息更新
 *
 * @param id
 * @param username 用户名
 * @param password 密码
 * @param oldpwd   旧密码
 * @param sign     签名
 * @param sex      性别
 */
case class UserInput(
  id: Int,
  username: String,
  password: String,
  oldpwd: String,
  sign: String,
  sex: String
)
object UserInput {

  implicit val decoder: Decoder[UserInput] = deriveDecoder[UserInput]
}
