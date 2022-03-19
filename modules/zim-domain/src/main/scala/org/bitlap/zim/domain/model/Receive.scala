/*
 * Copyright 2022 bitlap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bitlap.zim.domain.model

import io.circe._
import io.circe.generic.semiauto._
import scalikejdbc.{ WrappedResultSet, _ }

/**
 * 收到的消息
 * @see table:t_message
 * @param toid      发送给哪个用户
 * @param mid        消息的来源ID（如果是私聊，则是用户id，如果是群聊，则是群组id）
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
  mid: Int,
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
  implicit val encoder: Encoder[Receive] = (a: Receive) =>
    if (a == null) Json.Null
    else
      Json.obj(
        ("id", Json.fromInt(a.mid)),
        ("toid", Json.fromInt(a.toid)),
        ("username", Json.fromString(a.username)),
        ("avatar", Json.fromString(a.avatar)),
        ("content", Json.fromString(a.content)),
        ("cid", Json.fromInt(a.cid)),
        ("mine", Json.fromBoolean(a.mine)),
        ("fromid", Json.fromInt(a.fromid)),
        ("timestamp", Json.fromLong(a.timestamp)),
        ("status", Json.fromInt(a.status))
      )

  def apply(rs: WrappedResultSet)(implicit r: QuerySQLSyntaxProvider[SQLSyntaxSupport[Receive], Receive]): Receive =
    Receive(
      rs.int(r.resultName.toid),
      rs.int(r.resultName.mid),
      username = null,
      avatar = null,
      `type` = rs.string(r.resultName.`type`),
      content = rs.string(r.resultName.content),
      cid = 0,
      mine = false,
      fromid = rs.int(r.resultName.fromid),
      timestamp = rs.long(r.resultName.timestamp),
      status = rs.int(r.resultName.status)
    )
}
