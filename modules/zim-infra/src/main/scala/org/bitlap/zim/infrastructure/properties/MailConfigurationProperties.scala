/*
 * Copyright 2022 bitlap
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

import com.typesafe.config.{ Config, ConfigFactory }
import org.simplejavamail.config.ConfigLoader
import zio.{ Has, UIO, ULayer, ZIO, ZLayer }

import java.util.Properties
import scala.util.Try

/** configuration for simple java mail
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/30
 *  @version 1.0
 */
final case class MailConfigurationProperties(
  host: String,
  username: String,
  password: String,
  sender: String,
  port: Int,
  threadPoolSize: Int,
  connectionPoolCoreSize: Int,
  debug: Boolean
) {
  lazy val toProperties: Properties = {
    val properties = new Properties()
    properties.setProperty(ConfigLoader.Property.SMTP_HOST.key(), this.host)
    properties.setProperty(ConfigLoader.Property.SMTP_USERNAME.key(), this.username)
    properties.setProperty(ConfigLoader.Property.SMTP_PASSWORD.key(), this.password)
    properties.setProperty(ConfigLoader.Property.SMTP_PORT.key(), s"${this.port}")
    properties
  }
}

object MailConfigurationProperties {

  type ZMailConfigurationProperties = Has[MailConfigurationProperties]

  lazy val config: Config = ConfigFactory.load().getConfig("infrastructure.javamail")

  val live: ULayer[ZMailConfigurationProperties] =
    ZLayer.succeed(config) >>> ZLayer.fromService[Config, MailConfigurationProperties](MailConfigurationProperties(_))

  def make: UIO[MailConfigurationProperties] =
    ZIO.serviceWith[MailConfigurationProperties](c => ZIO.succeed(c)).provideLayer(live)

  def apply(config: Config = config): MailConfigurationProperties =
    MailConfigurationProperties(
      host = config.getString("host"),
      username = config.getString("username"),
      password = config.getString("password"),
      sender = config.getString("sender"),
      port = config.getInt("port"),
      threadPoolSize = config.getInt("threadPoolSize"),
      connectionPoolCoreSize = config.getInt("connectionPoolCoreSize"),
      debug = Try(config.getBoolean("debug")).getOrElse(true)
    )
}
