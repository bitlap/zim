package org.bitlap.zim.domain.model

import scalikejdbc._

case class UserDBO(username: String, id: Option[Long] = None)

object UserDBO extends SQLSyntaxSupport[UserDBO] {

  override val tableName = "t_user"

  def apply(rs: WrappedResultSet) = new UserDBO(rs.string("username"), rs.longOpt("id"))

}