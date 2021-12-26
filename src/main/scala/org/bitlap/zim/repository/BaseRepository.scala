package org.bitlap.zim.repository

import zio.stream

/**
 * 基础操作类
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait BaseRepository[T] {

  def insert(dbo: T): stream.Stream[Throwable, Long]

  def deleteById(id: Long): stream.Stream[Throwable, Int]

  def findById(id: Long): stream.Stream[Throwable, T]

  def findAll(): stream.Stream[Throwable, T]

}
