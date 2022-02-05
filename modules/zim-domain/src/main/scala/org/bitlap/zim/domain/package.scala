package org.bitlap.zim

import io.circe.{ Decoder, Encoder }

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import scala.util.Try

/**
 * @author 梦境迷离
 * @since 2022/2/2
 * @version 1.0
 */
package object domain {

  implicit val encodeDate: Encoder[ZonedDateTime] =
    Encoder.encodeString.contramap[ZonedDateTime](t =>
      if (t == null) "" else t.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    )

  implicit val decodeDate: Decoder[ZonedDateTime] = Decoder.decodeString.emapTry { str =>
    Try {
      if (str == null) ZonedDateTime.now() else ZonedDateTime.parse(str)
    }
  }

}
