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

package org.bitlap.zim.domain.input
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

/**
 * 群组 输入
 *
 * @param groupname 分组名称
 * @param avatar    头像
 * @param createId  创建人 creator Id
 */
final case class GroupInput(groupname: String, avatar: String, createId: Int)

object GroupInput {

  implicit val decoder: Decoder[GroupInput] = deriveDecoder[GroupInput]

  implicit val encoder: Encoder[GroupInput] = deriveEncoder[GroupInput]

}
