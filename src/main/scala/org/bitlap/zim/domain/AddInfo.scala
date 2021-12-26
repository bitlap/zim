package org.bitlap.zim.domain

import io.circe.generic.semiauto._
import io.circe.{ Decoder, Encoder }
import org.bitlap.zim.domain.model.User

/** 返回添加好友、群组消息
 *
 * @param id
 * @param uid        用户id
 * @param content    消息内容
 * @param from       消息发送者id
 * @param from_group 消息发送者申请加入的群id
 * @param `type`     消息类型
 * @param remark     附言
 * @param href       来源，没使用，未知
 * @param read       是否已读
 * @param time       时间
 * @param user       消息发送者
 */
case class AddInfo(
  id: Int,
  uid: Int,
  content: String,
  from: Int,
  from_group: Int,
  `type`: Int,
  remark: String,
  href: String,
  read: Int,
  time: String,
  user: User
)
object AddInfo {

  implicit val decoder: Decoder[AddInfo] = deriveDecoder[AddInfo]
  implicit val encoder: Encoder[AddInfo] = deriveEncoder[AddInfo]

}
