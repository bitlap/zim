package org.bitlap.zim.domain.model

import io.circe._
import io.circe.generic.semiauto._
import org.bitlap.zim.domain.Group
import scalikejdbc.{ WrappedResultSet, _ }

/**
 * 群组信息
 *
 * @see table:t_group
 * @param id        群组id
 * @param groupname 群组名称
 * @param avatar    头像
 * @param createId  创建人ID
 */
final case class GroupList(
  override val id: Int,
  override val groupname: String,
  avatar: String,
  createId: Int
) extends Group(id, groupname)

object GroupList extends SQLSyntaxSupport[GroupList] {

  // 数据库列名和字段名不同，使用DSL时需要指定列表，如：`g.column("group_name")` 而不是 `g.groupname`
  override lazy val columns: collection.Seq[String] = Seq("id", "group_name", "avatar", "create_id")

  override def tableName: String = "t_group"

  implicit val decoder: Decoder[GroupList] = deriveDecoder[GroupList]
  implicit val encoder: Encoder[GroupList] = deriveEncoder[GroupList]

  def apply(rs: WrappedResultSet): GroupList = GroupList(
    rs.int("id"),
    rs.string("group_name"),
    rs.string("avatar"),
    rs.int("create_id")
  )

}
