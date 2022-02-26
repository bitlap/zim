package org.bitlap.zim.domain

import io.circe.{ Decoder, Encoder, HCursor, Json }

/**
 * 我发送的消息和我的信息
 *
 * @param id       我的id
 * @param username 我的昵称
 * @param mine     是否我发的消息
 * @param avatar   我的头像
 * @param content  消息内容
 */
final case class Mine(id: Int, username: String, mine: Boolean, avatar: String, content: String)

object Mine {

  implicit val decoder: Decoder[Mine] = (c: HCursor) =>
    if (!c.succeeded) null
    else
      for {
        id <- c.getOrElse[Int]("id")(0)
        username <- c.getOrElse[String]("username")("")
        mine <- c.getOrElse[Boolean]("mine")(false)
        avatar <- c.getOrElse[String]("avatar")("")
        content <- c.getOrElse[String]("content")("")
      } yield Mine(id, username, mine, avatar, content)

  implicit val encoder: Encoder[Mine] = (a: Mine) =>
    if (a == null) Json.Null
    else
      Json.obj(
        ("id", Json.fromInt(a.id)),
        ("username", Json.fromString(a.username)),
        ("mine", Json.fromBoolean(a.mine)),
        ("avatar", Json.fromString(a.avatar)),
        ("content", Json.fromString(a.content))
      )

}
