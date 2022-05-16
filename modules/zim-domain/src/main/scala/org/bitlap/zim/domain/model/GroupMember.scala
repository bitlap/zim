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
import io.circe.{ Decoder, Encoder }
import scalikejdbc.{ WrappedResultSet, _ }

/** 群组成员
 *
 *  @see
 *    table:t_group_members
 *  @param id
 *    表ID 无意义
 *  @param gid
 *    群组编号
 *  @param uid
 *    用户编号
 */
final case class GroupMember(gid: Int, uid: Int, id: Int = 0)

object GroupMember extends BaseModel[GroupMember] {

  override lazy val columns: collection.Seq[String] = autoColumns[GroupMember]()

  override def apply(rs: WrappedResultSet)(implicit sp: SyntaxProvider[GroupMember]): GroupMember =
    autoConstruct[GroupMember](rs, sp)

  override def tableName: String = "t_group_members"

  implicit val decoder: Decoder[GroupMember] = deriveDecoder[GroupMember]
  implicit val encoder: Encoder[GroupMember] = deriveEncoder[GroupMember]

}
