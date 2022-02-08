package org.bitlap.zim.server

import org.bitlap.zim.domain.model
import org.bitlap.zim.domain.model.{ AddMessage, GroupList, User }
import org.bitlap.zim.server.configuration.properties.MysqlConfigurationProperties
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scalikejdbc._
import zio.BootstrapRuntime

import java.time.ZonedDateTime

/**
 * @author 梦境迷离
 * @since 2022/1/2
 * @version 1.0
 */
trait BaseData extends AnyFlatSpec with Matchers with BeforeAndAfter with BootstrapRuntime {

  // test SQL for unittest suit
  val sqlBefore: SQL[_, NoExtractor]

  val sqlAfter: SQL[_, NoExtractor]

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

  val mockAddMessage = AddMessage(
    id = 1,
    fromUid = 1,
    toUid = 2,
    groupId = 3,
    remark = "remark",
    agree = 0,
    `type` = 0,
    time = ZonedDateTime.now()
  )

  val mockUser =
    User(
      1,
      "zhangsan",
      "123456",
      null,
      "/static/image/avatar/avatar(3).jpg",
      "dreamylost@outlook.com",
      ZonedDateTime.now(),
      1,
      "online",
      "1ade893a1b1940a5bb8dc8447538a6a6a18ad80bcf84437a8cfb67213337202d"
    )

  val mockGroupList = GroupList(
    id = 1,
    groupName = "我的好友",
    avatar = "",
    createId = 1
  )

  val mockReceive = model.Receive(
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
    NamedDB(Symbol(h2ConfigurationProperties.databaseName)).autoCommit { implicit session =>
      sqlBefore.execute().apply()
    }
  }

  after {
    NamedDB(Symbol(h2ConfigurationProperties.databaseName)).autoCommit { implicit session =>
      sqlAfter.execute().apply()
    }
  }
}
