package org.bitlap.zim.domain

import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }
import io.circe.{ Decoder, Encoder }

/**
 * @author 梦境迷离
 * @since 2022/1/1
 * @version 1.0
 */
final case class UploadResult(src: String, name: String)

object UploadResult {

  implicit val decoder: Decoder[UploadResult] = deriveDecoder[UploadResult]
  implicit val encoder: Encoder[UploadResult] = deriveEncoder[UploadResult]
}
