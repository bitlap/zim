package org.bitlap.zim.domain.model

import io.circe.generic.semiauto._
import io.circe.{ Decoder, Encoder, Json }
import scalikejdbc.{ WrappedResultSet, _ }

/**
 * 用户创建的好友列表
 *
 * @see table:t_friend_group
 * @param id        分组ID
 * @param uid       用户id，该分组所属的用户ID
 * @param groupName 群组名称
 */
final case class FriendGroup(id: Int, uid: Int, groupName: String)

object FriendGroup extends SQLSyntaxSupport[FriendGroup] {

  override lazy val columns: collection.Seq[String] = autoColumns[FriendGroup]()

  override def tableName: String = "t_friend_group"

  implicit val decoder: Decoder[FriendGroup] = deriveDecoder[FriendGroup]
  implicit val encoder: Encoder[FriendGroup] = (a: FriendGroup) => {
    if (a == null) Json.Null
    else
      Json.obj(
        ("id", Json.fromInt(a.id)),
        ("uid", Json.fromInt(a.id)),
        ("groupname", Json.fromString(a.groupName))
      )
  }

  def apply(rs: WrappedResultSet): FriendGroup = FriendGroup(
    rs.int("id"),
    rs.int("uid"),
    rs.string("group_name")
  )
}
