package org.bitlap.zim.domain.input
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

/**
 * 好友分组 输入
 *
 * @param uid    创建人
 * @param groupname 分组名称
 */
final case class FriendGroupInput(uid: Int, groupname: String)

object FriendGroupInput {

  implicit val decoder: Decoder[FriendGroupInput] = deriveDecoder[FriendGroupInput]

}
