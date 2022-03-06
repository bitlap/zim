/*
 * Copyright 2021 bitlap
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

package org.bitlap.zim.server.configuration.properties

import com.typesafe.config.{ Config, ConfigFactory }

/**
 * mysql configuration
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
