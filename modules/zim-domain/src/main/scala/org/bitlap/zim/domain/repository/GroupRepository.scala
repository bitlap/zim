package org.bitlap.zim.domain.repository
import zio.stream

/**
 * 群组的操作定义
 *
 * @author 梦境迷离
 * @since 2021/12/29
 * @version 1.0
 */
trait GroupRepository[T] extends BaseRepository[T] {

  def deleteGroup(id: Int): stream.Stream[Throwable, Int]

  def countGroup(groupName: Option[String]): stream.Stream[Throwable, Int]

  def createGroupList(group: T): stream.Stream[Throwable, Long]

  def findGroup(groupName: Option[String]): stream.Stream[Throwable, T]

  def findGroupById(gid: Int): stream.Stream[Throwable, T]

  def findGroupsById(uid: Int): stream.Stream[Throwable, T]

}
