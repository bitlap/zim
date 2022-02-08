package org.bitlap.zim.domain.repository
import org.bitlap.zim.domain.model.GroupMember
import zio.stream

/**
 * @author 梦境迷离
 * @since 2022/1/15
 * @version 1.0
 */
trait GroupMemberRepository extends BaseRepository[GroupMember] {

  def leaveOutGroup(groupMember: GroupMember): stream.Stream[Throwable, Int]

  def findGroupMembers(gid: Int): stream.Stream[Throwable, Int]

  def addGroupMember(groupMember: GroupMember): stream.Stream[Throwable, Int]

}
