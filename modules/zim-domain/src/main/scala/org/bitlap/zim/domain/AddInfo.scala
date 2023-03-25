/*
 * Copyright 2023 bitlap
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

package org.bitlap.zim.domain

import java.time.ZonedDateTime

import io.circe._
import io.circe.generic.semiauto._
import org.bitlap.zim.domain.model.User

/** 返回添加好友、群组消息
 *
 *  @param id
 *  @param uid
 *    用户id
 *  @param content
 *    消息内容
 *  @param from
 *    消息发送者id
 *  @param from_group
 *    消息发送者申请加入的群id
 *  @param `type`
 *    消息类型
 *  @param remark
 *    附言
 *  @param href
 *    来源，没使用，未知
 *  @param read
 *    是否已读
 *  @param time
 *    时间
 *  @param user
 *    消息发送者
 */
final case class AddInfo(
  id: Int,
  uid: Int,
  content: String,
  from: Int,
  from_group: Int,
  `type`: Int,
  remark: String,
  href: String,
  read: Int,
  time: ZonedDateTime,
  user: User
)
object AddInfo {

  implicit val decoder: Decoder[AddInfo] = deriveDecoder[AddInfo]
  implicit val encoder: Encoder[AddInfo] = deriveEncoder[AddInfo]
}
