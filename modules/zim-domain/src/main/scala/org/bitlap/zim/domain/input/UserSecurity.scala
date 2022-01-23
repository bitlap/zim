package org.bitlap.zim.domain.input
import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }
import io.circe.{ Decoder, Encoder, HCursor }
import zio.schema.{ DeriveSchema, Schema }

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

  case class UserSecurityInfo(id: Int, email: String, password: String) {
    def toCookieValue: String = {
      val base64 = Base64.getEncoder.encode(s"$email:$password".getBytes(Charset.forName("utf8")))
      new String(base64)
    }
  }

  object UserSecurityInfo {

    implicit val encoder: Encoder[UserSecurityInfo] = deriveEncoder[UserSecurityInfo]

    implicit val decoder: Decoder[UserSecurityInfo] = (c: HCursor) => {
      if (!c.succeeded) null
      else
        for {
          id <- c.getOrElse("id")(0)
          email <- c.downField("email").as[String]
          password <- c.downField("password").as[String]
        } yield UserSecurityInfo(id, email, password)
    }

    implicit val userSecuritySchema: Schema[UserSecurityInfo] = DeriveSchema.gen[UserSecurityInfo]
  }

}
