package org.bitlap.zim

import org.bitlap.zim.domain.model.UserDBO
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

  private[repository] def queryFindById(table: TableDefSQLSyntax, id: Long): SQL[UserDBO, HasExtractor] =
    sql"SELECT * FROM ${table} WHERE id = ${id}".list().map(rs => UserDBO(rs))

  private[repository] def queryFindAll(table: TableDefSQLSyntax): StreamReadySQL[UserDBO] =
    sql"SELECT * FROM ${table}".list().map(r => UserDBO(r)).iterator()

  private[repository] def queryDeleteById(table: TableDefSQLSyntax, id: Long): SQL[Nothing, NoExtractor] =
    sql"DELETE FROM ${table} WHERE id = ${id}"

}
