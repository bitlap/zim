/*
 * Copyright 2023 bitlap
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
import org.bitlap.zim.api.repository.ReceiveRepository
import org.bitlap.zim.domain.model.Receive
import org.bitlap.zim.infrastructure.repository._
import org.bitlap.zim.server.BaseData
import org.bitlap.zim.server.repository.TangibleReceiveRepositorySpec.TangibleReceiveRepositoryConfigurationSpec
import scalikejdbc._
import zio._

/** t_message表操作的单测
 *
 *  @author
 *    梦境迷离
 *  @since 2022/1/3
 *  @version 1.0
 */
final class TangibleReceiveRepositorySpec extends TangibleReceiveRepositoryConfigurationSpec {

  behavior of "Tangible Receive Repository"

  it should "findHistoryMessage by fromid and toid" in {
    val actual: Option[Receive] = unsafeRun(
      (for {
        id <- TangibleReceiveRepository.saveMessage(mockReceive)
        dbReceive <- TangibleReceiveRepository
          .findHistoryMessage(Some(mockReceive.fromid), Some(mockReceive.toid), Some(mockReceive.`type`))
      } yield dbReceive).runHead
        .provideLayer(env)
    )
    actual.map(r => r.fromid -> r.content) shouldBe Some(mockReceive.fromid -> mockReceive.content)
  }

  it should "countHistoryMessage by fromid toid" in {
    val actual: Option[Int] = unsafeRun(
      (for {
        id <- TangibleReceiveRepository.saveMessage(mockReceive)
        dbReceive <- TangibleReceiveRepository
          .countHistoryMessage(Some(mockReceive.fromid), Some(mockReceive.toid), Some(mockReceive.`type`))
      } yield dbReceive).runHead
        .provideLayer(env)
    )
    actual shouldBe Some(1)
  }

  it should "findOffLineMessage by fromid toid" in {
    val actual: Option[Receive] = unsafeRun(
      (for {
        id        <- TangibleReceiveRepository.saveMessage(mockReceive)
        dbReceive <- TangibleReceiveRepository.findOffLineMessage(mockReceive.toid, mockReceive.status)
      } yield dbReceive).runHead
        .provideLayer(env)
    )
    actual.map(r => r.fromid -> r.status) shouldBe Some(mockReceive.fromid -> mockReceive.status)
  }

  it should "readMessage by fromid toid" in {
    val actual: Option[Receive] = unsafeRun(
      (for {
        _         <- TangibleReceiveRepository.saveMessage(mockReceive)
        _         <- TangibleReceiveRepository.readMessage(mockReceive.fromid, mockReceive.toid, mockReceive.`type`)
        dbReceive <- TangibleReceiveRepository.findById(1)
      } yield dbReceive).runHead
        .provideLayer(env)
    )
    actual.map(r => r.fromid -> r.status) shouldBe Some(mockReceive.fromid -> 1)
  }
}

object TangibleReceiveRepositorySpec {

  trait TangibleReceiveRepositoryConfigurationSpec extends BaseData {

    override val sqlAfter: SQL[_, NoExtractor] =
      sql"""
        drop table if exists t_message;
         """

    override val sqlBefore: SQL[_, NoExtractor] =
      sql"""
            DROP TABLE IF EXISTS `t_message`;
            CREATE TABLE `t_message` (
              `id` int(10) NOT NULL AUTO_INCREMENT,
              `toid` int(10) NOT NULL COMMENT '发送给哪个用户或者组id',
              `mid` int(10) NOT NULL COMMENT '消息的来源ID（如果是私聊，则是用户id，如果是群聊，则是群组id）',
              `fromid` int(10) NOT NULL COMMENT '消息的发送者id（比如群组中的某个消息发送者）',
              `content` varchar(512) NOT NULL COMMENT '消息内容',
              `type` varchar(10) NOT NULL DEFAULT '' COMMENT '聊天窗口来源类型',
              `timestamp` bigint(25) NOT NULL COMMENT '服务器动态时间',
              `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否已读',
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
         """

    val env: ULayer[ReceiveRepository[RStream]] = TangibleReceiveRepository.make(h2ConfigurationProperties.databaseName)

  }

}
