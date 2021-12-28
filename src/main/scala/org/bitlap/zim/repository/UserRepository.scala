package org.bitlap.zim.repository

import zio.stream

/**
 * 用户的操作定义
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait UserRepository[T] extends BaseRepository[T] {

//  def countUser(username: String, sex: Option[Int]): stream.Stream[Throwable, Int]

}
