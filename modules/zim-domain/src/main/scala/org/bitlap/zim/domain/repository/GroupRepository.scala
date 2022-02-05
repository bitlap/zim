package org.bitlap.zim.domain.repository
import org.bitlap.zim.domain.model.GroupList
import zio.stream

/**
 * 群组的操作定义
 *
 * @author 梦境迷离
 * @since 2021/12/29
 * @version 1.0
 */
trait GroupRepository extends BaseRepository[GroupList] {

  def deleteGroup(id: Int): stream.Stream[Throwable, Int]

  def countGroup(groupName: Option[String]): stream.Stream[Throwable, Int]

  def createGroupList(group: GroupList): stream.Stream[Throwable, Long]

  def findGroups(groupName: Option[String]): stream.Stream[Throwable, GroupList]

  def findGroupById(gid: Int): stream.Stream[Throwable, GroupList]

  def findGroupsById(uid: Int): stream.Stream[Throwable, GroupList]

}
