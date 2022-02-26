package org.bitlap.zim.domain

import io.circe.{ Decoder, Encoder, HCursor, Json }

/**
 * 发送给...的信息
 *
 * @param id       对方的id
 * @param username 名字
 * @param sign     签名
 * @param avatar   头像
 * @param status   状态
 * @param `type`   聊天类型，一般分friend和group两种，group即群聊
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
        id <- c.getOrElse[Int]("id")(0)
        username <- c.getOrElse[String]("username")("")
        sign <- c.getOrElse[String]("sign")("")
        avatar <- c.getOrElse[String]("avatar")("")
        status <- c.getOrElse[String]("status")("")
        typ <- c.getOrElse[String]("type")("")
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
