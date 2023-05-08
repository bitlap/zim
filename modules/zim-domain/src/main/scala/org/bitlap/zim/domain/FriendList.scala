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

import org.bitlap.zim.domain.model.User

import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax.EncoderOps

import zio.schema._

/** 好友列表
 *
 *  好友列表也是一种group
 *
 *  一个好友列表有多个用户
 *
 *  @param id
 *    好友列表id
 *  @param groupName
 *    列表名称
 *  @param list
 *    用户列表
 */
final case class FriendList(override val id: Int, override val groupName: String, list: List[User])
    extends Group(id, groupName)

object FriendList {

  implicit val decoder: Decoder[FriendList] = deriveDecoder[FriendList]

  implicit val encoder: Encoder[FriendList] = (a: FriendList) =>
    if (a == null) Json.Null
    else
      Json.obj(
        ("id", Json.fromInt(a.id)),
        ("groupname", Json.fromString(a.groupName)),
        ("list", a.list.asJson)
      )
  implicit val schema: Schema[FriendList] = DeriveSchema.gen[FriendList]

}
