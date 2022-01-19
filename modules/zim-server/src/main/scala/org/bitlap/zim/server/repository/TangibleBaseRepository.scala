package org.bitlap.zim.server.repository

import org.bitlap.zim.domain.model.BaseModel
import org.bitlap.zim.domain.repository.BaseRepository
import scalikejdbc._
import zio.stream
import org.bitlap.zim.domain.repository.Condition._

abstract class TangibleBaseRepository[T](M: BaseModel[T]) extends BaseRepository[T] {

  implicit val dbName: String
  implicit val sp: QuerySQLSyntaxProvider[SQLSyntaxSupport[T], T]

  override def findById(id: Long): stream.Stream[Throwable, T] =
    withSQL {
      select
        .from(M as sp)
        .where(
          sqls.eq(sp.id, id)
        )
    }.map(M(_)).toSQLOperation

  def find(params: Option[ZCondition]*): stream.Stream[Throwable, T] =
    withSQL {
      select
        .from(M as sp)
        .where(
          sqls.toAndConditionOpt(
            params.view
              .filter(_.isDefined)
              .map(_.get.value)
              .map(cd => cd.key -> cd.value)
              .map {
                case (_, null) => None
                case (k, Some(v)) =>
                  v match {
                    case syntax: SQLSyntax => Some(syntax)
                    case _                 => Some(sqls"${sp.column(k)} = $v")
                  }
                case (_, None)              => None
                case (_, syntax: SQLSyntax) => Some(syntax)
                case (k, vo)                => Option(vo).map(v => sqls"${sp.column(k)} = $v")
              }
              .toSeq: _*
          )
        )
    }.map(M(_)).toSQLOperation

  def count(params: Option[ZCondition]*): stream.Stream[Throwable, Int] =
    withSQL {
      select(SQLSyntax.count(sp.id))
        .from(M as sp)
        .where(
          sqls.toAndConditionOpt(
            params.view
              .filter(_.isDefined)
              .map(_.get.value)
              .map(cd => cd.key -> cd.value)
              .map {
                case (_, null) => None
                case (k, Some(v)) =>
                  v match {
                    case syntax: SQLSyntax => Some(syntax)
                    case _                 => Some(sqls"${sp.column(k)} = $v")
                  }
                case (_, None)              => None
                case (_, syntax: SQLSyntax) => Some(syntax)
                case (k, vo)                => Option(vo).map(v => sqls"${sp.column(k)} = $v")
              }
              .toSeq: _*
          )
        )
    }.map(rs => rs.int(1)).toSQLOperation
}
