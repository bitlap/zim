package org.bitlap.zim.repository

import zio.stream

/**
 * 好友分组操作定义
 *
 * @author 梦境迷离
 * @since 2022/1/2
 * @version 1.0
 */
trait FriendGroupFriendRepository[T] extends BaseRepository[T] {

  def removeFriend(friendId: Int, uId: Int): stream.Stream[Throwable, Int]

  def changeGroup(groupId: Int, originRecordId: Int): stream.Stream[Throwable, Int]

  def findUserGroup(uId: Int, mId: Int): stream.Stream[Throwable, Int]

  def addFriend(from: T, to: T): stream.Stream[Throwable, Int]
}
