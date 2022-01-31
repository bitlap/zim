package org.bitlap.zim.domain.input
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

/**
 * 群组 输入
 *
 * @param groupname 分组名称
 * @param avatar    头像
 * @param createId  创建人 creator Id
 */
case class GroupInput(groupname: String, avatar: String, createId: Int)

object GroupInput {

  implicit val decoder: Decoder[GroupInput] = deriveDecoder[GroupInput]

}
