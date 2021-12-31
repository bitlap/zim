package org.bitlap.zim.repository

import zio.stream

/**
 * 好友分组操作定义
 *
 * @author LittleTear
 * @since 2021/12/31
 * @version 1.0
 */
trait FriendGroupRepository[T] extends BaseRepository[T] {
  def createFriendGroup(receive: T): stream.Stream[Throwable, Int]

  def findFriendGroupsById(uid: Int): stream.Stream[Throwable, T]
}
