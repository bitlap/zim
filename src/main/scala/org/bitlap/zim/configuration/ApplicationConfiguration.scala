package org.bitlap.zim.configuration

import org.bitlap.zim.application.{ UserApplication, UserService }
import org.bitlap.zim.configuration.InfrastructureConfiguration.ZInfrastructureConfiguration
import zio._

/**
 * 应用程序配置
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
final class ApplicationConfiguration(infrastructureConfiguration: InfrastructureConfiguration) {
  // 应用程序管理多个application，这里只有一个（模块化）
  val userApplication: UserApplication = UserService(
    infrastructureConfiguration.userRepository,
    infrastructureConfiguration.groupRepository,
    infrastructureConfiguration.mailService
  )
}

/**
 * 应用程序依赖管理
 */
object ApplicationConfiguration {

  def apply(infrastructureConfiguration: InfrastructureConfiguration): ApplicationConfiguration =
    new ApplicationConfiguration(infrastructureConfiguration)

  type ZApplicationConfiguration = Has[ApplicationConfiguration]

  val userApplication: URIO[ZApplicationConfiguration, UserApplication] =
    ZIO.access(_.get.userApplication)

  val live: ZLayer[ZInfrastructureConfiguration, Nothing, ZApplicationConfiguration] =
    ZLayer.fromService[InfrastructureConfiguration, ApplicationConfiguration](ApplicationConfiguration(_))

  def make(infrastructureConfiguration: InfrastructureConfiguration): ULayer[ZApplicationConfiguration] =
    ZLayer.succeed(infrastructureConfiguration) >>> live

}
