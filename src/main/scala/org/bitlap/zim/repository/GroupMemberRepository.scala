package org.bitlap.zim.repository

import zio.stream

trait GroupMemberRepository[T] extends BaseRepository[T] {

  def leaveOutGroup(groupMember: T): stream.Stream[Throwable, Int]

  def findGroupMembers(gid: Int): stream.Stream[Throwable, Int]

  def addGroupMember(groupMember: T): stream.Stream[Throwable, Int]

}
