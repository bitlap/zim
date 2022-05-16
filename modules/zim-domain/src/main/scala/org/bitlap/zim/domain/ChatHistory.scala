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

package org.bitlap.zim.domain

import io.circe._
import io.circe.generic.semiauto._

/** 聊天记录
 *
 *  @param id
 *    用户id
 *  @param username
 *    用户名
 *  @param avatar
 *    用户头像
 *  @param content
 *    消息内容
 *  @param timestamp
 *    时间
 */
final case class ChatHistory(
  id: Int,
  username: String,
  avatar: String,
  content: String,
  timestamp: Long
)

object ChatHistory {

  implicit val decoder: Decoder[ChatHistory] = deriveDecoder[ChatHistory]
  implicit val encoder: Encoder[ChatHistory] = deriveEncoder[ChatHistory]
}
