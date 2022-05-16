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

import io.circe.generic.semiauto._
import io.circe.{ Decoder, Encoder, Json }
import scalikejdbc.{ WrappedResultSet, _ }

/** 用户创建的好友列表
 *
 *  @see
 *    table:t_friend_group
 *  @param id
 *    分组ID
 *  @param uid
 *    用户id，该分组所属的用户ID
 *  @param groupName
 *    群组名称
 */
final case class FriendGroup(id: Int, uid: Int, groupName: String)

object FriendGroup extends BaseModel[FriendGroup] {

  override lazy val columns: collection.Seq[String] = autoColumns[FriendGroup]()

  override def apply(rs: WrappedResultSet)(implicit sp: SyntaxProvider[FriendGroup]): FriendGroup =
    autoConstruct[FriendGroup](rs, sp)

  override def tableName: String = "t_friend_group"

  implicit val decoder: Decoder[FriendGroup] = deriveDecoder[FriendGroup]
  implicit val encoder: Encoder[FriendGroup] = (a: FriendGroup) =>
    if (a == null) Json.Null
    else
      Json.obj(
        ("id", Json.fromInt(a.id)),
        ("uid", Json.fromInt(a.id)),
        ("groupname", Json.fromString(a.groupName))
      )
}
