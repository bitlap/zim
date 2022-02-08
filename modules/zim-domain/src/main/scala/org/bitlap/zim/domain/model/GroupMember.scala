package org.bitlap.zim.domain.model

import io.circe.generic.semiauto._
import io.circe.{ Decoder, Encoder }
import scalikejdbc.{ WrappedResultSet, _ }

/**
 * 群组成员
 *
 * @see table:t_group_members
 * @param id 表ID 无意义
 * @param gid 群组编号
 * @param uid 用户编号
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
