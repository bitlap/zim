package org.bitlap.zim.domain.model

import io.circe.generic.semiauto._
import io.circe.{ Decoder, Encoder }
import scalikejdbc.{ WrappedResultSet, _ }

/**
 * 添加好友
 * @see table:t_friend_group_friends
 * @param mid  自己的id
 * @param mgid 分组id
 * @param tid  对方用户id
 * @param tgid 对方分组id
 */
case class AddFriends(mid: Int, mgid: Int, tid: Int, tgid: Int)

object AddFriends extends SQLSyntaxSupport[AddFriends] {

  override lazy val columns: collection.Seq[String] = autoColumns[AddFriends]()

  override def tableName: String = "t_friend_group_friends"

  implicit val decoder: Decoder[AddFriends] = deriveDecoder[AddFriends]
  implicit val encoder: Encoder[AddFriends] = deriveEncoder[AddFriends]

  def apply(rs: WrappedResultSet): AddFriends = AddFriends(
    rs.int("mid"),
    rs.int("mgid"),
    rs.int("tid"),
    rs.int("tgid")
  )

}
