package org.bitlap.zim
import org.bitlap.zim.configuration.properties.MysqlConfigurationProperties
import org.bitlap.zim.domain.model.User
import org.scalatest.BeforeAndAfterEach
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
trait BaseData extends AnyFlatSpec with Matchers with BeforeAndAfterEach with BootstrapRuntime {

  val table: SQL[_, NoExtractor]
  val h2ConfigurationProperties: MysqlConfigurationProperties = MysqlConfigurationProperties()

  val mockUser =
    User.apply(1, "zhangsan", "", null, "/static/image/avatar/avatar(3).jpg", "", ZonedDateTime.now(), 0, "online", "")

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

  override protected def beforeEach(): Unit =
    NamedDB(Symbol(h2ConfigurationProperties.databaseName)).autoCommit { implicit session =>
      table.execute().apply()
    }

}
