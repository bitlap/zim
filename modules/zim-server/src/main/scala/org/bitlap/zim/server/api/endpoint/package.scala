package org.bitlap.zim.server.api
import io.circe.generic.semiauto._
import io.circe.Decoder

/**
 * @author 梦境迷离
 * @version 1.0,2022/1/21
 */
package object endpoint {

  case class ExistEmailInput(email: String)

  object ExistEmailInput {
    implicit val decoder: Decoder[ExistEmailInput] = deriveDecoder[ExistEmailInput]
  }
}
