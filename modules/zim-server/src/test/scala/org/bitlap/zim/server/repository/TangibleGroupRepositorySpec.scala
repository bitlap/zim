/*
 * Copyright 2022 bitlap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bitlap.zim.server.repository
import org.bitlap.zim.api.repository.{ GroupMemberRepository, GroupRepository }
import org.bitlap.zim.domain._
import org.bitlap.zim.domain.model._
import org.bitlap.zim.infrastructure.repository.{ RStream, TangibleGroupMemberRepository, TangibleGroupRepository }
import org.bitlap.zim.server.BaseData
import org.bitlap.zim.server.repository.TangibleGroupRepositorySpec.TangibleGroupRepositoryConfigurationSpec
import scalikejdbc._
import zio._

/** t_group表操作的单测
 *
 *  @author
 *    梦境迷离
 *  @since 2022/1/2
 *  @version 1.0
 */
final class TangibleGroupRepositorySpec extends TangibleGroupRepositoryConfigurationSpec {

  behavior of "Tangible Group Repository"

  it should "findGroupById by id" in {
    val actual: Option[GroupList] = unsafeRun(
      (for {
        id      <- TangibleGroupRepository.createGroupList(mockGroupList)
        dbGroup <- TangibleGroupRepository.findGroupById(id.toInt)
      } yield dbGroup).runHead
        .provideLayer(env)
    )
    actual.map(u => u.id -> u.groupName) shouldBe Some(mockGroupList.id -> mockGroupList.groupName)
  }

  it should "findGroup by groupname" in {
    val actual: Option[GroupList] = unsafeRun(
      (for {
        id      <- TangibleGroupRepository.createGroupList(mockGroupList)
        dbGroup <- TangibleGroupRepository.findGroup(Some(mockGroupList.groupName))
      } yield dbGroup).runHead
        .provideLayer(env)
    )
    actual.map(u => u.id -> u.groupName) shouldBe Some(mockGroupList.id -> mockGroupList.groupName)
  }

  it should "findGroupsById by uid" in {
    val actual: Option[GroupList] = unsafeRun(
      (for {
        id      <- TangibleGroupRepository.createGroupList(mockGroupList)
        _       <- TangibleGroupMemberRepository.addGroupMember(model.GroupMember(id.toInt, mockGroupList.createId))
        dbGroup <- TangibleGroupRepository.findGroupsById(mockGroupList.createId)
      } yield dbGroup).runHead
        .provideLayer(env)
    )
    actual.map(u => u.id -> u.groupName) shouldBe Some(mockGroupList.id -> mockGroupList.groupName)
  }

  it should "countGroup by groupname" in {
    val actual: Option[Int] = unsafeRun(
      (for {
        id      <- TangibleGroupRepository.createGroupList(mockGroupList)
        dbGroup <- TangibleGroupRepository.countGroup(Some(mockGroupList.groupName))
      } yield dbGroup).runHead
        .provideLayer(env)
    )
    actual shouldBe Some(1)
  }

  it should "deleteGroup by id" in {
    val actual: Option[GroupList] = unsafeRun(
      (for {
        id      <- TangibleGroupRepository.createGroupList(mockGroupList)
        _       <- TangibleGroupRepository.deleteGroup(id.toInt)
        dbGroup <- TangibleGroupRepository.findById(id.toInt)
      } yield dbGroup).runHead
        .provideLayer(env)
    )
    actual shouldBe None
  }

  it should "findGroupMembers by id" in {
    val actual: Chunk[Int] = unsafeRun(
      (for {
        id <- TangibleGroupRepository.createGroupList(mockGroupList)
        _  <- TangibleGroupMemberRepository.addGroupMember(model.GroupMember(id.toInt, mockGroupList.createId))
        u  <- TangibleGroupMemberRepository.findGroupMembers(id.toInt)
      } yield u).runCollect
        .provideLayer(env)
    )
    actual.headOption shouldBe Some(mockGroupList.createId)
  }

  it should "findById by id" in {
    val actual: Option[model.GroupMember] = unsafeRun(
      (for {
        id    <- TangibleGroupRepository.createGroupList(mockGroupList)
        _     <- TangibleGroupMemberRepository.addGroupMember(model.GroupMember(id.toInt, mockGroupList.createId))
        group <- TangibleGroupMemberRepository.findById(id.toInt)
      } yield group).runHead
        .provideLayer(env)
    )
    actual.map(_.uid) shouldBe Some(mockGroupList.createId)
  }

  it should "leaveOutGroup by uid and gid" in {
    val actual: Option[model.GroupMember] = unsafeRun(
      (for {
        id    <- TangibleGroupRepository.createGroupList(mockGroupList)
        _     <- TangibleGroupMemberRepository.addGroupMember(model.GroupMember(id.toInt, mockGroupList.createId))
        _     <- TangibleGroupMemberRepository.leaveOutGroup(model.GroupMember(id.toInt, mockGroupList.createId))
        group <- TangibleGroupMemberRepository.findById(id.toInt)
      } yield group).runHead
        .provideLayer(env)
    )
    actual.map(_.uid) shouldBe None
  }
}

object TangibleGroupRepositorySpec {

  trait TangibleGroupRepositoryConfigurationSpec extends BaseData {

    override val sqlAfter: SQL[_, NoExtractor] =
      sql"""
        drop table if exists t_group;
        drop table if exists t_group_members;
         """

    override val sqlBefore: SQL[_, NoExtractor] =
      sql"""
            DROP TABLE IF EXISTS `t_group`;
            CREATE TABLE `t_group` (
              `id` int(20) NOT NULL AUTO_INCREMENT,
              `group_name` varchar(64) NOT NULL COMMENT '群组名称',
              `avatar` varchar(255) NOT NULL DEFAULT '' COMMENT '群组图标',
              `create_id` int(20) NOT NULL COMMENT '创建者id',
              `create_time` timestamp NOT NULL DEFAULT '1971-01-01 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

            DROP TABLE IF EXISTS `t_group_members`;
            CREATE TABLE `t_group_members` (
              `id` int(20) NOT NULL AUTO_INCREMENT,
              `gid` int(20) NOT NULL COMMENT '群组ID',
              `uid` int(20) NOT NULL,
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
         """

    val groupMemberLayer: ULayer[GroupMemberRepository[RStream]] =
      TangibleGroupMemberRepository.make(h2ConfigurationProperties.databaseName)

    val env: ZLayer[Any, Throwable, GroupRepository[RStream] with GroupMemberRepository[RStream]] =
      groupMemberLayer ++ TangibleGroupRepository.make(h2ConfigurationProperties.databaseName)
  }

}
