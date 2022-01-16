package org.bitlap.zim.server.repository

import org.bitlap.zim.domain.model.BaseModel
import org.bitlap.zim.domain.repository.BaseRepository
import scalikejdbc._
import zio.stream

abstract class TangibleBaseRepository[T](M: BaseModel[T]) extends BaseRepository[T] {

  implicit val dbName: String
  implicit val sp: QuerySQLSyntaxProvider[SQLSyntaxSupport[T], T]

  override def findById(id: Long): stream.Stream[Throwable, T] = {
    withSQL {
      select.from(M as sp)
        .where(
          sqls.eq(sp.id, id)
        )
    }.map(M(_)).toSQLOperation
  }

  def find(conditions: Seq[Option[SQLSyntax]]): stream.Stream[Throwable, T] = {
    withSQL {
      select
        .from(M as sp)
        .where(sqls.toAndConditionOpt(conditions: _*))
    }.map(M(_)).toSQLOperation
  }

  def find(params: Map[String, Any]): stream.Stream[Throwable, T] = {
    withSQL {
      select.from(M as sp)
        .where(
          sqls.toAndConditionOpt(
            params.map {
              case (k, Some(v)) =>
                v match {
                  case syntax: SQLSyntax => Some(syntax)
                  case _ => Some(sqls"${sp.column(k)} = $v")
                }
              case (_, None) => None
              case (k, vo) => Option(vo).map(v => sqls"${sp.column(k)} = $v")
            }.toSeq: _*
          )
        )
    }.map(M(_)).toSQLOperation
  }

  def count(conditions: Seq[Option[SQLSyntax]]): stream.Stream[Throwable, Int] = {
    withSQL {
      select(SQLSyntax.count(sp.id))
        .from(M as sp)
        .where(sqls.toAndConditionOpt(conditions: _*))
    }.map(rs => rs.int(1)).toSQLOperation
  }

  def count(params: Map[String, Any]): stream.Stream[Throwable, Int] = {
    withSQL {
      select(SQLSyntax.count(sp.id))
        .from(M as sp)
        .where(
          sqls.toAndConditionOpt(
            params.map {
              case (k, Some(v)) =>
                v match {
                  case syntax: SQLSyntax => Some(syntax)
                  case _ => Some(sqls"${sp.column(k)} = $v")
                }
              case (_, None) => None
              case (k, vo) => Option(vo).map(v => sqls"${sp.column(k)} = $v")
            }.toSeq: _*
          )
        )
    }.map(rs => rs.int(1)).toSQLOperation
  }

  implicit final class StringArrow(private val self: String) {
    @inline def like (y: Option[String]): (String, Option[SQLSyntax]) = (self, y.map(sqls.like(sp.column(self), _)))
    @inline def like (y: String): (String, Option[SQLSyntax]) = like(Option(y))
    @inline def === [B: ParameterBinderFactory](y: Option[B]): (String, Option[SQLSyntax]) = (self, y.map(sqls.eq(sp.column(self), _)))
    @inline def === [B: ParameterBinderFactory](y: B): (String, Option[SQLSyntax]) = ===(Option(y))
  }
}
