package org.bitlap.zim.domain

import io.circe.{ Decoder, Encoder }

import java.util.Date
import scala.util.Try

/**
 *
 * @author 梦境迷离
 * @since 2021/12/26
 * @version 1.0
 */
package object model {

  implicit val encodeInstant: Encoder[Date] = Encoder.encodeString.contramap[Date](_.getTime.toString)

  implicit val decodeInstant: Decoder[Date] = Decoder.decodeString.emapTry { str =>
    Try {
      val d = new Date()
      d.setTime(str.toLong)
      d
    }
  }
}
