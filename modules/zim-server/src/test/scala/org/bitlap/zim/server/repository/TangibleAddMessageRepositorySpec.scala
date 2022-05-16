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
import org.bitlap.zim.server.BaseData
import org.bitlap.zim.server.repository.TangibleAddMessageRepositorySpec.TangibleAddMessageRepositoryConfigurationSpec
import scalikejdbc._
import zio.ULayer
import org.bitlap.zim.infrastructure.repository._
import org.bitlap.zim.infrastructure.repository.TangibleAddMessageRepository.ZAddMessageRepository

/** @author
 *    梦境迷离
 *  @since 2022/2/8
 *  @version 1.0
 */
class TangibleAddMessageRepositorySpec extends TangibleAddMessageRepositoryConfigurationSpec {

  behavior of "Tangible AddMessage Repository"

  it should "findById ok" in {
    val actual = unsafeRun(
      (for {
        _   <- TangibleAddMessageRepository.saveAddMessage(mockAddMessage)
        msg <- TangibleAddMessageRepository.findById(1)
      } yield msg).runHead
        .provideLayer(env)
    )
    actual.map(u => u.id -> u.remark) shouldBe Some(mockAddMessage.id -> mockAddMessage.remark)
  }

  it should "findAddInfo ok" in {
    val actual = unsafeRun(
      (for {
        _   <- TangibleAddMessageRepository.saveAddMessage(mockAddMessage)
        msg <- TangibleAddMessageRepository.findAddInfo(2)
      } yield msg).runHead
        .provideLayer(env)
    )
    actual.map(u => u.id -> u.remark) shouldBe Some(mockAddMessage.id -> mockAddMessage.remark)
  }

  it should "updateAgree ok" in {
    val actual = unsafeRun(
      (for {
        _   <- TangibleAddMessageRepository.saveAddMessage(mockAddMessage)
        _   <- TangibleAddMessageRepository.updateAgree(1, 2)
        msg <- TangibleAddMessageRepository.findById(1)
      } yield msg).runHead
        .provideLayer(env)
    )
    actual.map(u => u.id -> u.agree) shouldBe Some(mockAddMessage.id -> 2)
  }

  it should "countUnHandMessage by to_uid" in {
    val actual = unsafeRun(
      (for {
        _   <- TangibleAddMessageRepository.saveAddMessage(mockAddMessage)
        _   <- TangibleAddMessageRepository.countUnHandMessage(2, None)
        msg <- TangibleAddMessageRepository.findById(1)
      } yield msg).runHead
        .provideLayer(env)
    )
    actual.map(u => u.id -> u.agree) shouldBe Some(mockAddMessage.id -> 0)
  }

  it should "countUnHandMessage by to_uid and agree" in {
    val actual = unsafeRun(
      (for {
        _   <- TangibleAddMessageRepository.saveAddMessage(mockAddMessage)
        _   <- TangibleAddMessageRepository.saveAddMessage(mockAddMessage.copy(agree = 1))
        _   <- TangibleAddMessageRepository.saveAddMessage(mockAddMessage.copy(agree = 1))
        ret <- TangibleAddMessageRepository.countUnHandMessage(2, Some(1))
      } yield ret).runHead
        .provideLayer(env)
    )
    actual shouldBe Some(1)
  }
}

object TangibleAddMessageRepositorySpec {

  trait TangibleAddMessageRepositoryConfigurationSpec extends BaseData {

    override val sqlAfter: SQL[_, NoExtractor] =
      sql"""
         drop table if exists `t_add_message`;
         """

    override val sqlBefore: SQL[_, NoExtractor] =
      sql"""
          DROP TABLE IF EXISTS `t_add_message`;
          CREATE TABLE `t_add_message` (
            `id` int(10) NOT NULL AUTO_INCREMENT,
            `from_uid` int(10) NOT NULL COMMENT '谁发起的请求',
            `to_uid` int(10) NOT NULL COMMENT '发送给谁的申请,可能是群，那么就是创建该群组的用户',
            `group_id` int(10) NOT NULL COMMENT '如果是添加好友则为from_id的分组id，如果为群组则为群组id',
            `remark` varchar(255) DEFAULT NULL COMMENT '附言',
            `agree` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0未处理，1同意，2拒绝',
            `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '类型，可能是添加好友或群组',
            `time` datetime NOT NULL COMMENT '申请时间',
            PRIMARY KEY (`id`),
            UNIQUE KEY `add_friend_unique` (`from_uid`,`to_uid`,`group_id`,`type`) USING BTREE
          ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
         """

    val env: ULayer[ZAddMessageRepository] =
      TangibleAddMessageRepository.make(h2ConfigurationProperties.databaseName)
  }

}
