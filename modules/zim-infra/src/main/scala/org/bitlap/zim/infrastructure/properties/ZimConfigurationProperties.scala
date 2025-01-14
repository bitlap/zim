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

package org.bitlap.zim.infrastructure.properties

import com.typesafe.config.{Config, ConfigFactory}

import zio._

/** application configuration（exclude database）
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 1.0
 */
final case class ZimConfigurationProperties(
    name: String,
    interface: String,
    port: Int,
    webHost: String
)

object ZimConfigurationProperties {

  lazy val config: Config = ConfigFactory.load().getConfig("application")

  lazy val live: ULayer[ZimConfigurationProperties] = ZLayer.succeed(ZimConfigurationProperties(config))

  def apply(config: Config = config): ZimConfigurationProperties =
    ZimConfigurationProperties(
      name = config.getString("name"),
      interface = config.getString("server.interface"),
      port = config.getInt("server.port"),
      webHost = config.getString("server.webHost")
    )

}
