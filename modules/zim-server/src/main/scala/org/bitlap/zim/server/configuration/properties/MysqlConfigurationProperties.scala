package org.bitlap.zim.server.configuration.properties

import com.typesafe.config.{ Config, ConfigFactory }

/**
 * mysql配置
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
final case class MysqlConfigurationProperties(
  url: String,
  user: String,
  password: String,
  databaseName: String,
  initialSize: Integer,
  maxSize: Integer,
  connectionTimeoutMillis: Long,
  validationQuery: String,
  driverName: String
)

object MysqlConfigurationProperties {

  lazy val conf: Config = ConfigFactory.load().getConfig("infrastructure.mysql")

  def apply(config: Config = conf): MysqlConfigurationProperties =
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
