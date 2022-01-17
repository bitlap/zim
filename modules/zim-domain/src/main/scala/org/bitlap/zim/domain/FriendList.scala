package org.bitlap.zim.domain

import io.circe.generic.semiauto._
import io.circe.{ Decoder, Encoder }
import org.bitlap.zim.domain.model.User

/**
 * 好友列表
 *
 * 好友列表也是一种group
 *
 * 一个好友列表有多个用户
 *
 * @param id        好友列表id
 * @param groupname 列表名称
 * @param list      用户列表
 */
case class FriendList(override val id: Int, override val groupname: String, list: List[User])
    extends Group(id, groupname)

object FriendList {

  implicit val decoder: Decoder[FriendList] = deriveDecoder[FriendList]
  implicit val encoder: Encoder[FriendList] = deriveEncoder[FriendList]

}
