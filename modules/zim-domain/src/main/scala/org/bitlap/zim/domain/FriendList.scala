package org.bitlap.zim.domain

import io.circe.generic.semiauto._
import io.circe.syntax.EncoderOps
import io.circe.{ Decoder, Encoder, Json }
import org.bitlap.zim.domain.model.User

/**
 * 好友列表
 *
 * 好友列表也是一种group
 *
 * 一个好友列表有多个用户
 *
 * @param id        好友列表id
 * @param groupName 列表名称
 * @param list      用户列表
 */
case class FriendList(override val id: Int, override val groupName: String, list: List[User])
    extends Group(id, groupName)

object FriendList {

  implicit val decoder: Decoder[FriendList] = deriveDecoder[FriendList]
  implicit val encoder: Encoder[FriendList] = (a: FriendList) => {
    if (a == null) Json.Null
    else
      Json.obj(
        ("id", Json.fromInt(a.id)),
        ("groupname", Json.fromString(a.groupName)),
        ("list", a.list.asJson)
      )
  }

}
