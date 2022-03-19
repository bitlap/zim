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

import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }
import io.circe.{ Decoder, Encoder }

/**
 * 添加好友、群组
 *
 * @param groupId 好友列表id或群组id
 * @param remark  附言
 * @param `type`  类型，好友或群组
 */
final case class Add(groupId: Int, remark: String, `type`: Int)

object Add {

  implicit val decoder: Decoder[Add] = deriveDecoder[Add]
  implicit val encoder: Encoder[Add] = deriveEncoder[Add]

}
