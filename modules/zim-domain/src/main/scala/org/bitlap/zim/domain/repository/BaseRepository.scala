package org.bitlap.zim.domain.repository

import zio.stream

/**
 * 基础操作类
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait BaseRepository[T] {

  def findById(id: Long): stream.Stream[Throwable, T]
}
