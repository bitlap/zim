package org.bitlap.zim.domain.repository
import zio.stream

/**
 * @author 梦境迷离
 * @since 2022/1/15
 * @version 1.0
 */
trait GroupMemberRepository[T] extends BaseRepository[T] {

  def leaveOutGroup(groupMember: T): stream.Stream[Throwable, Int]

  def findGroupMembers(gid: Int): stream.Stream[Throwable, Int]

  def addGroupMember(groupMember: T): stream.Stream[Throwable, Int]

}
