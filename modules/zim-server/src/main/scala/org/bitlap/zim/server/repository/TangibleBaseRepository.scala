package org.bitlap.zim.server.repository

import org.bitlap.zim.domain.model.BaseModel
import org.bitlap.zim.domain.repository.BaseRepository
import scalikejdbc._
import zio.stream

abstract class TangibleBaseRepository[T](t: BaseModel[T]) extends BaseRepository[T] {

  implicit val dbName: String
  implicit val sp: QuerySQLSyntaxProvider[SQLSyntaxSupport[T], T]

  override def findById(id: Long): stream.Stream[Throwable, T] = {
    sql"SELECT ${sp.result.*} FROM ${t as sp} WHERE id = ${id}".list().map(rs => t.extract(rs))
      .toSQLOperation
  }
}
