package org.bitlap.zim.server

import org.bitlap.zim.domain.model
import org.bitlap.zim.domain.model.{ AddMessage, GroupList, User }
import org.bitlap.zim.infrastructure.properties.MysqlConfigurationProperties
import org.bitlap.zim.server.util.DateHelper
import scalikejdbc.{ ConnectionPool, ConnectionPoolSettings, NamedDB, NoExtractor, SQL }
import zio.test.DefaultRunnableSpec

trait BaseSuit extends DefaultRunnableSpec {
  val sqlBefore: SQL[_, NoExtractor] = null

  val sqlAfter: SQL[_, NoExtractor] = null

  lazy val before: Boolean =
    NamedDB(Symbol(h2ConfigurationProperties.databaseName)).autoCommit { implicit session =>
      sqlBefore.execute().apply()
    }

  lazy val after: Boolean =
    NamedDB(Symbol(h2ConfigurationProperties.databaseName)).autoCommit { implicit session =>
      sqlAfter.execute().apply()
    }

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
    time = DateHelper.getConstantTime
  )

  val mockUser =
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

  val mockGroupMembers = model.GroupMember(
    id = 1,
    gid = 1,
    uid = 1
  )

  val mockFriendGroup = model.FriendGroup(
    id = 1,
    uid = 1,
    groupName = "zim"
  )

  val mockAddFriend = model.AddFriend(
    uid = 1,
    fgid = 2,
    id = 1
  )

}
