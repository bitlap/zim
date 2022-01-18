package org.bitlap.zim.domain.repository

import scalikejdbc.interpolation.SQLSyntax
import zio.stream

/**
 * 基础操作类
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait BaseRepository[T] {

  /**
   * find by id
   */
  def findById(id: Long): stream.Stream[Throwable, T]

  /**
   * find by params
   */
//  def find(params: Map[String, QueryParamValue]): stream.Stream[Throwable, T]
}
