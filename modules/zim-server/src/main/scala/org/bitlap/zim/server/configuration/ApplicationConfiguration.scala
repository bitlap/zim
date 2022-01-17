package org.bitlap.zim.server.configuration

import org.bitlap.zim.server.application.{ ApiApplication, ApiService, UserApplication, UserService }
import org.bitlap.zim.server.configuration.InfrastructureConfiguration.ZInfrastructureConfiguration
import zio.{ URIO, _ }

/**
 * application configuration
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
final class ApplicationConfiguration(infrastructureConfiguration: InfrastructureConfiguration) {

  // multi application
  val userApplication: UserApplication = UserService(
    infrastructureConfiguration.userRepository,
    infrastructureConfiguration.groupRepository,
    infrastructureConfiguration.receiveRepository,
    infrastructureConfiguration.friendGroupRepository,
    infrastructureConfiguration.friendGroupFriendRepository,
    infrastructureConfiguration.groupMemberRepository,
    infrastructureConfiguration.addMessageRepository
  )

  val apiApplication: ApiApplication = ApiService(userApplication)

}

/**
 * application dependencies
 */
object ApplicationConfiguration {

  def apply(infrastructureConfiguration: InfrastructureConfiguration): ApplicationConfiguration =
    new ApplicationConfiguration(infrastructureConfiguration)

  type ZApplicationConfiguration = Has[ApplicationConfiguration]

  val userApplication: URIO[ZApplicationConfiguration, UserApplication] =
    ZIO.access(_.get.userApplication)

  val apiApplication: URIO[ZApplicationConfiguration, ApiApplication] =
    ZIO.access(_.get.apiApplication)

  val live: URLayer[ZInfrastructureConfiguration, ZApplicationConfiguration] =
    ZLayer.fromService[InfrastructureConfiguration, ApplicationConfiguration](ApplicationConfiguration(_))

  def make(infrastructureConfiguration: InfrastructureConfiguration): ULayer[ZApplicationConfiguration] =
    ZLayer.succeed(infrastructureConfiguration) >>> live

}
