package org.bitlap.zim.domain

import io.circe.syntax.EncoderOps
import io.circe.{ Decoder, Encoder, HCursor, Json }

/**
 * 消息
 *
 * @see table:t_message
 * @param `type` 随便定义，用于在服务端区分消息类型
 * @param mine   我的信息
 * @param to     对方信息
 * @param msg    额外的信息
 */
case class Message(`type`: String, mine: Mine, to: To, msg: String)

object Message {

  implicit val decoder: Decoder[Message] = (c: HCursor) =>
    if (!c.succeeded) null
    else
      for {
        typ <- c.downField("type").as[String]
        mine <-
          if (c.downField("mine").succeeded && c.downField("mine").values.nonEmpty) c.downField("mine").as[Mine]
          else Right(null)
        to <-
          if (c.downField("to").succeeded && c.downField("to").values.nonEmpty) c.downField("to").as[To]
          else Right(null)
        msg <- c.downField("msg").as[String]
      } yield Message(`type` = typ, mine = mine, to = to, msg = msg)

  implicit val encoder: Encoder[Message] = (a: Message) =>
    if (a == null) Json.Null
    else
      Json.obj(
        ("type", Json.fromString(a.`type`)),
        ("mine", Json.fromString(if (a.mine != null) a.mine.asJson.noSpaces else Json.Null.noSpaces)),
        ("to", Json.fromString(if (a.to != null) a.to.asJson.noSpaces else Json.Null.noSpaces)),
        ("msg", Json.fromString(a.msg))
      )

}
