package org.bitlap.zim.domain.input
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

import java.nio.charset.Charset
import java.util.Base64

/**
 * 用户登录 输入
 * cookie
 * @param cookie token 目前是邮箱+密码
 */
case class UserSecurity(cookie: String)

object UserSecurity {

  implicit val decoder: Decoder[UserSecurity] = deriveDecoder[UserSecurity]

  case class UserSecurityInfo(email: String, password: String) {
    def toCookieValue: String = {
      val base64 = Base64.getEncoder.encode(s"$email:$password".getBytes(Charset.forName("utf8")))
      new String(base64)
    }
  }

  object UserSecurityInfo {
    implicit val decoder: Decoder[UserSecurityInfo] = deriveDecoder[UserSecurityInfo]
  }

}
