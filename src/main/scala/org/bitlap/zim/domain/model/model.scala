package org.bitlap.zim.domain

import io.circe.{ Decoder, Encoder }

import scala.util.Try
import java.time.ZonedDateTime

/**
 *
 * @author 梦境迷离
 * @since 2021/12/26
 * @version 1.0
 */
package object model {

  implicit val encodeInstant: Encoder[ZonedDateTime] =
    Encoder.encodeString.contramap[ZonedDateTime](t => if (t == null) "" else t.toString)

  implicit val decodeInstant: Decoder[ZonedDateTime] = Decoder.decodeString.emapTry { str =>
    Try {
      if (str == null) ZonedDateTime.now() else ZonedDateTime.parse(str)
    }
  }
}
