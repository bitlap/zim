package org.bitlap.zim.configuration.properties

import com.typesafe.config.{ Config, ConfigFactory }
import org.simplejavamail.config.ConfigLoader

import java.util.Properties

/**
 * simple java mail配置
 *
 * @author 梦境迷离
 * @since 2021/12/30
 * @version 1.0
 */
case class MailConfigurationProperties(
  host: String,
  username: String,
  password: String,
  port: Int,
  threadPoolSize: Int,
  connectionPoolCoreSize: Int
) {
  lazy val toProperties: Properties = {
    val properties = new Properties()
    properties.setProperty(ConfigLoader.Property.SMTP_HOST.key(), this.host)
    properties.setProperty(ConfigLoader.Property.SMTP_USERNAME.key(), this.username)
    properties.setProperty(ConfigLoader.Property.SMTP_PASSWORD.key(), this.password)
    properties.setProperty(ConfigLoader.Property.SMTP_PORT.key(), this.port + "")
    properties
  }
}

object MailConfigurationProperties {

  def apply(config: Config = ConfigFactory.load().getConfig("infrastructure.javamail")): MailConfigurationProperties =
    MailConfigurationProperties(
      host = config.getString("host"),
      username = config.getString("username"),
      password = config.getString("password"),
      port = config.getInt("port"),
      threadPoolSize = config.getInt("threadPoolSize"),
      connectionPoolCoreSize = config.getInt("connectionPoolCoreSize")
    )
}
