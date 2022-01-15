package org.bitlap.zim.server.configuration.properties

import com.typesafe.config.{ Config, ConfigFactory }
import org.simplejavamail.config.ConfigLoader
import zio.{ Has, ULayer, ZIO, ZLayer }

import java.util.Properties
import scala.util.Try
import zio.UIO

/**
 * simple java mail配置
 *
 * @author 梦境迷离
 * @since 2021/12/30
 * @version 1.0
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

  lazy val config: Config = ConfigFactory.load().getConfig("application.javamail")

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
