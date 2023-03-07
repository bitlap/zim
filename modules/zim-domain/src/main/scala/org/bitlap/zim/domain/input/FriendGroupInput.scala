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

package org.bitlap.zim.domain.input

import io.circe._
import io.circe.generic.semiauto._

/** 好友分组 输入
 *
 *  @param uid
 *    创建人
 *  @param groupname
 *    分组名称
 */
final case class FriendGroupInput(uid: Int, groupname: String)

object FriendGroupInput {

  implicit val decoder: Decoder[FriendGroupInput] = deriveDecoder[FriendGroupInput]

}
