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

package org.bitlap.zim.server

import org.bitlap.zim.domain.model
import org.bitlap.zim.domain.model._
import org.bitlap.zim.infrastructure.properties._
import org.bitlap.zim.server.util._
import org.scalatest._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import scalikejdbc._

/** @author
 *    梦境迷离
 *  @since 2022/1/2
 *  @version 1.0
 */
trait BaseData extends AnyFlatSpec with Matchers with BeforeAndAfter with CommonTestSupport {

  // test SQL for unittest suit
  def sqlBefore: SQL[_, NoExtractor]

  def sqlAfter: SQL[_, NoExtractor]

  val h2ConfigurationProperties: MysqlConfigurationProperties = MysqlConfigurationProperties()

  ConnectionPool.add(
    Symbol(h2ConfigurationProperties.databaseName),
    h2ConfigurationProperties.url,
    h2ConfigurationProperties.user,
    h2ConfigurationProperties.password,
    ConnectionPoolSettings(
      initialSize = h2ConfigurationProperties.initialSize,
      maxSize = h2ConfigurationProperties.maxSize,
      connectionTimeoutMillis = h2ConfigurationProperties.connectionTimeoutMillis,
      validationQuery = h2ConfigurationProperties.validationQuery,
      driverName = h2ConfigurationProperties.driverName
    )
  )

  val mockAddMessage: AddMessage = AddMessage(
    id = 1,
    fromUid = 1,
    toUid = 2,
    groupId = 3,
    remark = "remark",
    agree = 0,
    `type` = 0,
    time = DateHelper.getConstantTime
  )

  val mockUser: User =
    User(
      1,
      "zhangsan",
      "123456",
      "",
      "/static/image/avatar/avatar(3).jpg",
      "dreamylost@outlook.com",
      DateHelper.getConstantTime,
      1,
      "online",
      "1ade893a1b1940a5bb8dc8447538a6a6a18ad80bcf84437a8cfb67213337202d"
    )

  val mockGroupList: GroupList = GroupList(
    id = 1,
    groupName = "我的好友",
    avatar = "",
    createId = 1
  )

  val mockReceive: Receive = model.Receive(
    toid = 1,
    mid = 2,
    username = null,
    avatar = null,
    `type` = "friend",
    content = "receive",
    cid = 0,
    mine = false,
    fromid = 2,
    timestamp = 10086L,
    status = 0
  )

  before {
    //  val stmt = NamedDB(Symbol(h2ConfigurationProperties.databaseName)).conn.createStatement()
    //  sqlBefore.statement.split(";").toList.foreach(stmt.addBatch)
    //  stmt.executeBatch()
    NamedDB(Symbol(h2ConfigurationProperties.databaseName)).autoCommit { implicit session =>
      sqlBefore.execute.apply()
    }
  }

  after {

    NamedDB(Symbol(h2ConfigurationProperties.databaseName)).autoCommit { implicit session =>
      sqlAfter.execute.apply()
    }
  }
}
