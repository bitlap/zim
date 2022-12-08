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
import org.bitlap.zim.api.repository.GroupMemberRepository
import org.bitlap.zim.infrastructure.repository.{RStream, TangibleGroupMemberRepository}
import org.bitlap.zim.server.ZIOBaseSuit
import org.bitlap.zim.server.repository.TangibleGroupMemberRepositorySpec.TangibleGroupMemberRepositoryConfigurationSpec
import scalikejdbc._
import zio._
import zio.test.Assertion._
import zio.test._

object TangibleGroupMemberRepositoryMainSpec extends TangibleGroupMemberRepositoryConfigurationSpec {
  override def spec: Spec[Any, Throwable] = suite("Tangible GroupMember Repository")(
    test("find by id") {
      for {
        _  <- TangibleGroupMemberRepository.addGroupMember(mockGroupMembers).runHead
        gm <- TangibleGroupMemberRepository.findById(mockGroupMembers.id).runHead
      } yield assert(gm)(equalTo(Some(mockGroupMembers)))
    } @@ TestAspect.before(ZIO.succeed(before)),
    test("find group members") {
      for {
        uid <- TangibleGroupMemberRepository.findGroupMembers(mockGroupMembers.gid).runHead
      } yield assert(uid)(equalTo(Some(1)))
    },
    test("leave out group") {
      for {
        _  <- TangibleGroupMemberRepository.leaveOutGroup(mockGroupMembers).runHead
        gm <- TangibleGroupMemberRepository.findById(mockGroupMembers.id).runHead
      } yield assert(gm)(equalTo(None))
    } @@ TestAspect.after(ZIO.succeed(after))
  ).provideLayer(env) @@ TestAspect.sequential

}

object TangibleGroupMemberRepositorySpec {
  trait TangibleGroupMemberRepositoryConfigurationSpec extends ZIOBaseSuit {
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
    val env: ULayer[GroupMemberRepository[RStream]] =
      TangibleGroupMemberRepository.make(h2ConfigurationProperties.databaseName)
  }

}
