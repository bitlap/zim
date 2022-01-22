package org.bitlap.zim.domain.input
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

/**
 * 用户登录 输入
 * cookie
 * @param email 邮箱
 * @param password 密码
 */
case class UserSecurity(value: String)

object UserSecurity {

  implicit val decoder: Decoder[UserSecurity] = deriveDecoder[UserSecurity]

  case class UserSecurityInfo(email: String, password: String)

  object UserSecurityInfo {
    implicit val decoder: Decoder[UserSecurityInfo] = deriveDecoder[UserSecurityInfo]

  }

}
