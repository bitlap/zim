package org.bitlap.zim.domain.model

import io.circe._
import io.circe.generic.semiauto._
import scalikejdbc.{ WrappedResultSet, _ }

import java.time.{ LocalDateTime, ZonedDateTime }

/**
 * 用户
 *
 * @see table:t_user
 * @param id
 * @param username   用户名
 * @param password   密码
 * @param sign       签名
 * @param avatar     头像
 * @param email      邮箱
 * @param createDate 创建时间
 * @param sex        性别
 * @param status     状态
 * @param active     激活码
 */
case class User(
  id: Int,
  username: String,
  password: String,
  sign: String,
  avatar: String,
  email: String,
  createDate: ZonedDateTime,
  sex: Int,
  status: String,
  active: String
)

object User extends SQLSyntaxSupport[User] {

  override val tableName = "t_user"

  // 由于经过了中间处理，需要显示调用解码器
  implicit val decoder: Decoder[User] = deriveDecoder[User]
  implicit val encoder: Encoder[User] = deriveEncoder[User]

  def apply(rs: WrappedResultSet): User = User(
    rs.int("id"),
    rs.string("username"),
    rs.string("password"),
    rs.string("sign"),
    rs.string("avatar"),
    rs.string("email"),
    rs.dateTime("create_date"),
    rs.int("sex"),
    rs.string("status"),
    rs.string("active")
  )

  def apply(id: Int, status: String): User =
    User(
      id = id,
      username = null,
      password = null,
      sign = null,
      avatar = null,
      email = null,
      createDate = null,
      sex = 0,
      status = status,
      active = null
    )
}
