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

import org.bitlap.zim.api.repository._
import org.bitlap.zim.domain.model._
import org.bitlap.zim.infrastructure.repository.{
  RStream,
  TangibleFriendGroupFriendRepository,
  TangibleFriendGroupRepository
}
import org.bitlap.zim.server.ZIOBaseSuit
import org.bitlap.zim.server.repository.TangibleFriendGroupFriendRepositorySpec.TangibleFriendGroupFriendRepositorySpec
import scalikejdbc._
import zio._
import zio.test.Assertion._
import zio.test._

object TangibleFriendGroupFriendRepositoryMainSpec extends TangibleFriendGroupFriendRepositorySpec {
  def spec: Spec[Any, Throwable] = suite("Tangible friendGroupFriend repository")(
    test("find user group") {
      for {
        _ <- TangibleFriendGroupFriendRepository
          .addFriend(AddFriend(id = 1, uid = 1, fgid = 2), AddFriend(id = 2, uid = 2, fgid = 1))
          .runHead
        _  <- TangibleFriendGroupRepository.createFriendGroup(FriendGroup(id = 2, uid = 1, groupName = "zim")).runHead
        id <- TangibleFriendGroupFriendRepository.findUserGroup(1, 1).runHead
      } yield assert(id)(equalTo(Some(2)))
    } @@ TestAspect.before(ZIO.succeed(before)),
    test("find by id ") {
      for {
        af <- TangibleFriendGroupFriendRepository.findById(2).runHead
      } yield assert(af.map(_.fgid))(equalTo(Some(1)))
    },
    test("remove friend") {
      for {
        _  <- TangibleFriendGroupFriendRepository.removeFriend(1, 1).runHead
        af <- TangibleFriendGroupFriendRepository.findById(2).runHead
      } yield assert(af)(equalTo(None))
    },
    test("change group ") {
      for {
        _  <- TangibleFriendGroupFriendRepository.changeGroup(3, 1).runHead
        af <- TangibleFriendGroupFriendRepository.findById(1).runHead
      } yield assert(af.map(_.fgid))(equalTo(Some(3)))
    } @@ TestAspect.after(ZIO.succeed(after))
  ).provideLayer(env) @@ TestAspect.sequential
}

object TangibleFriendGroupFriendRepositorySpec {
  trait TangibleFriendGroupFriendRepositorySpec extends ZIOBaseSuit {
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

    val friendGroupFriendLayer = TangibleFriendGroupFriendRepository.make(h2ConfigurationProperties.databaseName)
    val friendGroupLayer       = TangibleFriendGroupRepository.make(h2ConfigurationProperties.databaseName)

    val env: ULayer[FriendGroupRepository[RStream] with FriendGroupFriendRepository[RStream]] =
      friendGroupFriendLayer ++ friendGroupLayer
  }
}
