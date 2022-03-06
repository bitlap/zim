/*
 * Copyright 2021 bitlap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
final case class Message(`type`: String, mine: Mine, to: To, msg: String)

object Message {

  implicit val decoder: Decoder[Message] = (c: HCursor) =>
    if (!c.succeeded) null
    else
      for {
        typ <- c.downField("type").as[String]
        mine <- {
          if (checkJsonValue(c, "mine")) {
            c.downField("mine").as[Mine]
          } else {
            Right(null)
          }
        }
        to <-
          if (checkJsonValue(c, "to")) {
            c.downField("to").as[To]
          } else {
            Right(null)
          }
        msg <- c.getOrElse("msg")("{}")
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

  @inline private def checkJsonValue(c: HCursor, field: String): Boolean =
    c.downField(field).succeeded && c.downField(field).as[Json].isRight &&
      c.downField(field).as[Json].getOrElse(Json.fromString("null")) != Json.fromString("null")
}
