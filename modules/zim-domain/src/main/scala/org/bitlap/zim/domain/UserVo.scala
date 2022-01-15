package org.bitlap.zim.domain

import io.circe.generic.semiauto._
import io.circe.{ Decoder, Encoder }

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
case class UserVo(
  id: Int,
  username: String,
  password: String,
  oldpwd: String,
  sign: String,
  sex: String
)
object UserVo {

  implicit val decoder: Decoder[UserVo] = deriveDecoder[UserVo]
  implicit val encoder: Encoder[UserVo] = deriveEncoder[UserVo]

}
