package org.bitlap.zim.domain

import io.circe.generic.semiauto._
import io.circe.{ Decoder, Encoder }
import org.bitlap.zim.domain.model.{ GroupList, User }

/** 好友和群组整个信息集
 *
 * @param mine   我的信息
 * @param friend 好友列表
 * @param group  群组信息列表
 */
case class FriendAndGroupInfo(
  mine: User,
  friend: List[FriendList],
  group: List[GroupList]
)

object FriendAndGroupInfo {

  implicit val decoder: Decoder[FriendAndGroupInfo] = deriveDecoder[FriendAndGroupInfo]
  implicit val encoder: Encoder[FriendAndGroupInfo] = deriveEncoder[FriendAndGroupInfo]

}
