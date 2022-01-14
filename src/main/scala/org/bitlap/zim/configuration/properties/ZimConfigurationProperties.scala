package org.bitlap.zim.configuration.properties

import com.typesafe.config.{ Config, ConfigFactory }
import zio.{ Has, ULayer, ZIO, ZLayer }
import zio.UIO

/**
 * 应用总体配置（不含数据库）
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
final case class ZimConfigurationProperties(
  name: String,
  interface: String,
  port: Int
) {

  def withName(name: String): ZimConfigurationProperties =
    copy(name = name)

  def withInterface(interface: String): ZimConfigurationProperties =
    copy(interface = interface)

  def withPort(port: Int): ZimConfigurationProperties =
    copy(port = port)

}

object ZimConfigurationProperties {

  lazy val config: Config = ConfigFactory.load().getConfig("application")

  type ZZimConfigurationProperties = Has[ZimConfigurationProperties]

  val layer: ULayer[ZZimConfigurationProperties] =
    ZLayer.succeed(config) >>> ZLayer.fromService[Config, ZimConfigurationProperties](ZimConfigurationProperties(_))

  def make: UIO[ZimConfigurationProperties] = ZIO.succeed(ZimConfigurationProperties(config))

  def apply(config: Config = config): ZimConfigurationProperties =
    new ZimConfigurationProperties(
      name = config.getString("name"),
      interface = config.getString("server.interface"),
      port = config.getInt("server.port")
    )

}
