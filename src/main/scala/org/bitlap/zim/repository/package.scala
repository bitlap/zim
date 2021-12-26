package org.bitlap.zim

import org.bitlap.zim.domain.model.User
import scalikejdbc.streams._
import scalikejdbc.{ NoExtractor, SQL, _ }

/**
 * 用户操作SQL
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
package object repository {

  private[repository] def queryFindById(table: TableDefSQLSyntax, id: Long): SQL[User, HasExtractor] =
    sql"SELECT * FROM ${table} WHERE id = ${id}".list().map(rs => User(rs))

  private[repository] def queryFindAll(table: TableDefSQLSyntax): StreamReadySQL[User] =
    sql"SELECT * FROM ${table}".list().map(r => User(r)).iterator()

  private[repository] def queryDeleteById(table: TableDefSQLSyntax, id: Long): SQL[Nothing, NoExtractor] =
    sql"DELETE FROM ${table} WHERE id = ${id}"

}
