package org.bitlap.zim.repository

import org.bitlap.zim.domain.model.User
import scalikejdbc._
import scalikejdbc.streams._
import zio._
import zio.interop.reactivestreams._
import zio.stream.ZStream

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions

/**
 * 用户的操作实现
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
private final class TangibleUserRepository(databaseName: String) extends UserRepository[User] {

  override def insert(dbo: User): stream.Stream[Throwable, Int] = ???

  override def findAll(): stream.Stream[Throwable, User] =
    queryFindAll(User.table)

  override def deleteById(id: Long): stream.Stream[Throwable, Int] = ???

  override def findById(id: Long): stream.Stream[Throwable, User] =
    queryFindById(User.table, id)

  /**
   * scalikejdbc更新并返回主键的转换
   *
   * @param sqlUpdateWithGeneratedKey
   * @return
   */
  private[repository] implicit def executeOperation(
    sqlUpdateWithGeneratedKey: SQLUpdateWithGeneratedKey
  ): stream.Stream[Throwable, Long] =
    ZStream.fromEffect(
      Task.effect(NamedDB(Symbol(databaseName)).autoCommit(implicit session => sqlUpdateWithGeneratedKey.apply()))
    )

  /**
   * scalikejdbc更新操作转换
   *
   * @param sqlUpdate
   * @return
   */
  private[repository] implicit def executeUpdateOperation(sqlUpdate: SQLUpdate): stream.Stream[Throwable, Int] =
    ZStream.fromEffect(
      Task.effect(NamedDB(Symbol(databaseName)).autoCommit(implicit session => sqlUpdate.apply()))
    )

  /**
   * scalikejdbc 单对象转换
   *
   * @param sql
   * @tparam T
   * @return
   */
  private[repository] implicit def executeSQLOperation[T](sql: SQL[T, HasExtractor]): stream.Stream[Throwable, T] =
    ZStream.fromIterable(NamedDB(Symbol(databaseName)).autoCommit(implicit session => sql.list().apply()))

  /**
   * scalikejdbc流转换
   *
   * @param streamReadySQL
   * @tparam T
   * @return
   */
  private[repository] implicit def executeStreamOperation[T](
    streamReadySQL: StreamReadySQL[T]
  ): stream.Stream[Throwable, T] =
    (NamedDB(Symbol(databaseName)) readOnlyStream streamReadySQL).toStream()
}

object TangibleUserRepository {

  def apply(databaseName: String): UserRepository[User] =
    new TangibleUserRepository(databaseName)

  type ZUserRepository = Has[UserRepository[User]]

  /**
   * todo 这里只留公开方法
   * @return
   */
  def findAll(): stream.ZStream[ZUserRepository, Throwable, User] =
    stream.ZStream.accessStream(_.get.findAll())

  def findById(id: Int): stream.ZStream[ZUserRepository, Throwable, User] =
    stream.ZStream.accessStream(_.get.findById(id))

  def insert(query: User): stream.ZStream[ZUserRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.insert(query))

  def deleteById(id: Int): stream.ZStream[ZUserRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.deleteById(id))

  val live: ZLayer[Has[String], Nothing, ZUserRepository] =
    ZLayer.fromService[String, UserRepository[User]](TangibleUserRepository(_))

  def make(databaseName: String): ULayer[ZUserRepository] =
    ZLayer.succeed(databaseName) >>> live

}
