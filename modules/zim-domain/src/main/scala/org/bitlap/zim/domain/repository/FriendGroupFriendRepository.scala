package org.bitlap.zim.domain.repository
import org.bitlap.zim.domain.model.AddFriend
import zio.stream

/**
 * 好友分组操作定义
 *
 * @author 梦境迷离
 * @since 2022/1/2
 * @version 1.0
 */
trait FriendGroupFriendRepository extends BaseRepository[AddFriend] {

  def removeFriend(friendId: Int, uId: Int): stream.Stream[Throwable, Int]

  def changeGroup(groupId: Int, originRecordId: Int): stream.Stream[Throwable, Int]

  def findUserGroup(uId: Int, mId: Int): stream.Stream[Throwable, Int]

  def addFriend(from: AddFriend, to: AddFriend): stream.Stream[Throwable, Int]
}
