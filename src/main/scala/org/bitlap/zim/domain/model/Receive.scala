package org.bitlap.zim.domain.model

import io.circe._
import io.circe.generic.semiauto._
import scalikejdbc.{ WrappedResultSet, _ }

/**
 * 收到的消息
 * @see table:t_message
 * @param toid      发送给哪个用户
 * @param id        消息的来源ID（如果是私聊，则是用户id，如果是群聊，则是群组id）
 * @param username  消息来源用户名
 * @param avatar    消息来源用户头像
 * @param `type`    聊天窗口来源类型，从发送消息传递的to里面获取
 * @param content   消息内容
 * @param cid       消息id，可不传。除非你要对消息进行一些操作（如撤回）
 * @param mine      是否我发送的消息，如果为true，则会显示在右方
 * @param fromid    消息的发送者id（比如群组中的某个消息发送者），可用于自动解决浏览器多窗口时的一些问题
 * @param timestamp 服务端动态时间戳
 * @param status    消息的状态
 */
final case class Receive(
  toid: Int,
  id: Int,
  username: String,
  avatar: String,
  `type`: String,
  content: String,
  cid: Int,
  mine: Boolean,
  fromid: Int,
  timestamp: Long,
  status: Int
)

object Receive extends SQLSyntaxSupport[Receive] {

  // 对字段和名字重写定义
  override lazy val columns: collection.Seq[String] =
    Seq("toid", "mid", "id", "type", "content", "fromid", "timestamp", "status")

  override val tableName = "t_message"

  implicit val decoder: Decoder[Receive] = deriveDecoder[Receive]
  implicit val encoder: Encoder[Receive] = deriveEncoder[Receive]

  def apply(rs: WrappedResultSet): Receive = Receive(
    rs.int("toid"),
    rs.int("mid"),
    username = null,
    avatar = null,
    `type` = rs.string("type"),
    content = rs.string("content"),
    cid = 0,
    mine = false,
    fromid = rs.int("fromid"),
    timestamp = rs.long("timestamp"),
    status = rs.int("status")
  )
}
