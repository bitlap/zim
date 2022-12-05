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

import org.bitlap.zim.api.repository.FriendGroupRepository
import org.bitlap.zim.infrastructure.repository.{RStream, TangibleFriendGroupRepository}
import org.bitlap.zim.server.ZIOBaseSuit
import org.bitlap.zim.server.repository.TangibleFriendGroupRepositorySpec.TangibleFriendGroupRepositorySpec
import scalikejdbc._
import zio._
import zio.test.Assertion._
import zio.test._

object TangibleFriendGroupRepositoryMainSpec extends TangibleFriendGroupRepositorySpec {
  override def spec = suite("Tangible friendGroup repository")(
    test("find by id") {
      for {
        _  <- TangibleFriendGroupRepository.createFriendGroup(mockFriendGroup).runHead
        fg <- TangibleFriendGroupRepository.findById(1).runHead
      } yield assert(fg)(equalTo(Some(mockFriendGroup)))
    } @@ TestAspect.before(ZIO.succeed(before)),
    test("find friendGroups by id") {
      for {
        fg <- TangibleFriendGroupRepository.findFriendGroupsById(mockFriendGroup.uid).runHead
      } yield assert(fg)(equalTo(Some(mockFriendGroup)))
    } @@ TestAspect.after(ZIO.succeed(after))
  ).provideLayer(env) @@ TestAspect.sequential
}

object TangibleFriendGroupRepositorySpec {
  trait TangibleFriendGroupRepositorySpec extends ZIOBaseSuit {
    override val sqlAfter: SQL[_, NoExtractor] =
      sql"""
           |        drop table if exists t_friend_group_friends;
           |        drop table if exists t_friend_group;
           |""".stripMargin

    override val sqlBefore: SQL[_, NoExtractor] =
      sql"""
       DROP TABLE IF EXISTS `t_friend_group_friends`;
            CREATE TABLE `t_friend_group_friends` (
              `id` int(10) NOT NULL AUTO_INCREMENT,
              `fgid` int(10) NOT NULL COMMENT '分组id',
              `uid` int(10) NOT NULL COMMENT '用户id',
              PRIMARY KEY (`id`),
              UNIQUE KEY `g_uid_unique` (`fgid`,`uid`)
            ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

            DROP TABLE IF EXISTS `t_friend_group`;
            CREATE TABLE `t_friend_group` (
              `id` int(10) NOT NULL AUTO_INCREMENT,
              `uid` int(10) NOT NULL COMMENT '该分组所属的用户ID',
              `group_name` varchar(64) NOT NULL COMMENT '分组名称',
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
       """

    val env: ULayer[FriendGroupRepository[RStream]] =
      TangibleFriendGroupRepository.make(h2ConfigurationProperties.databaseName)
  }

}
