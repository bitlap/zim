package org.bitlap.zim.server.configuration

import org.bitlap.zim.infrastructure.properties.MysqlConfigurationProperties
import org.bitlap.zim.server.BaseSuit
import scalikejdbc._
import zio.test._
import zio.test.Assertion._

object MysqlConfigSpec extends BaseSuit {


  def spec = suite("MysqlConfigSpec")(
    test("test the mysql connect working state") {
      assert(isConnected)(equalTo(true))
    }
  )

  val mysqlConfigurationProperties = MysqlConfigurationProperties()

  ConnectionPool.add(
    Symbol(mysqlConfigurationProperties.databaseName),
    mysqlConfigurationProperties.url,
    mysqlConfigurationProperties.user,
    mysqlConfigurationProperties.password,
    ConnectionPoolSettings(
      initialSize = mysqlConfigurationProperties.initialSize,
      maxSize = mysqlConfigurationProperties.maxSize,
      connectionTimeoutMillis = mysqlConfigurationProperties.connectionTimeoutMillis,
      validationQuery = mysqlConfigurationProperties.validationQuery,
      driverName = mysqlConfigurationProperties.driverName
    )
  )

  val isConnected: Boolean = NamedDB(Symbol(mysqlConfigurationProperties.databaseName)).conn.isValid(0)


}




