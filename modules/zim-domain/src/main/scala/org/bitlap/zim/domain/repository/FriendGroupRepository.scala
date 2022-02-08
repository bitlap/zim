package org.bitlap.zim.domain.repository
import org.bitlap.zim.domain.model.FriendGroup
import zio.stream

/**
 * 好友分组操作定义
 *
 * @author LittleTear
 * @since 2021/12/31
 * @version 1.0
 */
trait FriendGroupRepository extends BaseRepository[FriendGroup] {

  def createFriendGroup(friend: FriendGroup): stream.Stream[Throwable, Int]

  def findFriendGroupsById(uid: Int): stream.Stream[Throwable, FriendGroup]
}
