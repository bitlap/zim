/*
 * Copyright 2021 bitlap
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
import io.circe.{ Decoder, Encoder }
import scalikejdbc.{ WrappedResultSet, _ }

/**
 * 添加好友
 * @see table:t_friend_group_friends
 * @param id 表ID 无意义
 * @param uid  用户ID
 * @param fgid 分组ID
 */
final case class AddFriend(uid: Int, fgid: Int, id: Int = 0)

object AddFriend extends BaseModel[AddFriend] {

  override lazy val columns: collection.Seq[String] = autoColumns[AddFriend]()

  override def tableName: String = "t_friend_group_friends"

  implicit val decoder: Decoder[AddFriend] = deriveDecoder[AddFriend]
  implicit val encoder: Encoder[AddFriend] = deriveEncoder[AddFriend]

  override def apply(rs: WrappedResultSet)(implicit sp: SyntaxProvider[AddFriend]): AddFriend =
    autoConstruct[AddFriend](rs, sp)
}
