package org.bitlap.zim.domain.model
import io.circe.generic.semiauto._
import io.circe.{ Decoder, Encoder }
import scalikejdbc.{ WrappedResultSet, _ }

/**
 * 用户创建的好友列表
 *
 * @see table:t_friend_group
 * @param id        分组ID
 * @param uid       用户id，该分组所属的用户ID
 * @param groupname 群组名称
 */
case class FriendGroup(id: Int, uid: Int, groupname: String)

object FriendGroup extends SQLSyntaxSupport[FriendGroup] {

  override def tableName: String = "t_friend_group"

  implicit val decoder: Decoder[FriendGroup] = deriveDecoder[FriendGroup]
  implicit val encoder: Encoder[FriendGroup] = deriveEncoder[FriendGroup]

  def apply(rs: WrappedResultSet): FriendGroup = FriendGroup(
    rs.int("id"),
    rs.int("uid"),
    rs.string("group_name")
  )
}
