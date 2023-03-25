/*
 * Copyright 2023 bitlap
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

import io.circe._

/** 发送给...的信息
 *
 *  @param id
 *    对方的id
 *  @param username
 *    名字
 *  @param sign
 *    签名
 *  @param avatar
 *    头像
 *  @param status
 *    状态
 *  @param `type`
 *    聊天类型，一般分friend和group两种，group即群聊
 */
final case class To(
  id: Int,
  username: String,
  sign: String,
  avatar: String,
  status: String,
  `type`: String
)
object To {

  implicit val decoder: Decoder[To] = (c: HCursor) =>
    if (!c.succeeded) null
    else
      for {
        id       <- c.getOrElse[Int]("id")(0)
        username <- c.getOrElse[String]("username")("")
        sign     <- c.getOrElse[String]("sign")("")
        avatar   <- c.getOrElse[String]("avatar")("")
        status   <- c.getOrElse[String]("status")("")
        typ      <- c.getOrElse[String]("type")("")
      } yield To(id, username, sign, avatar, status, typ)

  implicit val encoder: Encoder[To] = (a: To) =>
    if (a == null) Json.Null
    else
      Json.obj(
        ("id", Json.fromInt(a.id)),
        ("username", Json.fromString(a.username)),
        ("sign", Json.fromString(a.sign)),
        ("avatar", Json.fromString(a.avatar)),
        ("status", Json.fromString(a.status)),
        ("type", Json.fromString(a.`type`))
      )

}
