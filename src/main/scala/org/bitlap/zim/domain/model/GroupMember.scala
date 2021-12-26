package org.bitlap.zim.domain.model
import io.circe.generic.semiauto._
import io.circe.{ Decoder, Encoder }
import scalikejdbc.{ WrappedResultSet, _ }

/** 群组成员
 *
 * @see table:t_group_members
 * @param gid 群组编号
 * @param uid 用户编号
 */
case class GroupMember(gid: Int, uid: Int)

object GroupMember extends SQLSyntaxSupport[GroupMember] {

  override def tableName: String = "t_group_members"

  implicit val decoder: Decoder[GroupMember] = deriveDecoder[GroupMember]
  implicit val encoder: Encoder[GroupMember] = deriveEncoder[GroupMember]

  def apply(rs: WrappedResultSet): GroupMember = GroupMember(
    rs.int("gid"),
    rs.int("uid")
  )
}
