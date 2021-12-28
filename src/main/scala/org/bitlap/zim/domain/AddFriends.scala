package org.bitlap.zim.domain

import io.circe.generic.semiauto._
import io.circe.{ Decoder, Encoder }

/**
 * 添加好友
 *
 * @param mid  自己的id
 * @param mgid 分组id
 * @param tid  对方用户id
 * @param tgid 对方分组id
 */
case class AddFriends(mid: Int, mgid: Int, tid: Int, tgid: Int)

object AddFriends {

  implicit val decoder: Decoder[AddFriends] = deriveDecoder[AddFriends]
  implicit val encoder: Encoder[AddFriends] = deriveEncoder[AddFriends]

}
