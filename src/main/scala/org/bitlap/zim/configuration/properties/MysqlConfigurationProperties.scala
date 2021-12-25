package org.bitlap.zim.configuration.properties

import com.typesafe.config.{ Config, ConfigFactory }

/**
 * mysql配置
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
case class MysqlConfigurationProperties(
                                         url: String,
                                         user: String,
                                         password: String,
                                         databaseName: String,
                                         initialSize: Integer,
                                         maxSize: Integer,
                                         connectionTimeoutMillis: Long,
                                         validationQuery: String,
                                         driverName: String

                                       ) {

  def withUrl(url: String): MysqlConfigurationProperties =
    copy(url = url)

  def withUser(user: String): MysqlConfigurationProperties =
    copy(user = user)

  def withPassword(password: String): MysqlConfigurationProperties =
    copy(password = password)

  def withDatabaseName(databaseName: String): MysqlConfigurationProperties =
    copy(databaseName = databaseName)

  def withInitialSize(initialSize: Integer): MysqlConfigurationProperties =
    copy(initialSize = initialSize)

  def withMaxSize(maxSize: Integer): MysqlConfigurationProperties =
    copy(maxSize = maxSize)

  def withConnectionTimeoutMillis(connectionTimeoutMillis: Long): MysqlConfigurationProperties =
    copy(connectionTimeoutMillis = connectionTimeoutMillis)

  def withValidationQuery(validationQuery: String): MysqlConfigurationProperties =
    copy(validationQuery = validationQuery)

  def withDriverName(driverName: String): MysqlConfigurationProperties =
    copy(driverName = driverName)

}

object MysqlConfigurationProperties {

  def apply(config: Config = ConfigFactory.load().getConfig("infrastructure.mysql")): MysqlConfigurationProperties =
    MysqlConfigurationProperties(
      url = config.getString("url"),
      user = config.getString("user"),
      password = config.getString("password"),
      databaseName = config.getString("databaseName"),
      initialSize = config.getInt("connection.initialPoolSize"),
      maxSize = config.getInt("connection.maxPoolSize"),
      connectionTimeoutMillis = config.getLong("connection.timeoutMillis"),
      validationQuery = config.getString("connection.validationQuery"),
      driverName = config.getString("connection.driver")
    )

}