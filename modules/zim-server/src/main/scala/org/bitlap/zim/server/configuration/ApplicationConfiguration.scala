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

package org.bitlap.zim.server.configuration

import org.bitlap.zim.infrastructure.InfrastructureConfiguration
import org.bitlap.zim.infrastructure.InfrastructureConfiguration.ZInfrastructureConfiguration
import org.bitlap.zim.server.service.ApiApplication
import zio._
import org.bitlap.zim.server.service.impl.{ ApiService, UserService }
import org.bitlap.zim.server.service.UserApplication
import org.bitlap.zim.infrastructure.repository.RStream

/** application configuration
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 1.0
 */
final class ApplicationConfiguration(infrastructureConfiguration: InfrastructureConfiguration) {

  // multi application
  val userApplication: UserApplication[RStream] = UserService(
    infrastructureConfiguration.userRepository,
    infrastructureConfiguration.groupRepository,
    infrastructureConfiguration.receiveRepository,
    infrastructureConfiguration.friendGroupRepository,
    infrastructureConfiguration.friendGroupFriendRepository,
    infrastructureConfiguration.groupMemberRepository,
    infrastructureConfiguration.addMessageRepository
  )

  val apiApplication: ApiApplication[RStream] = ApiService(userApplication)

}

/** application dependencies
 */
object ApplicationConfiguration {

  def apply(infrastructureConfiguration: InfrastructureConfiguration): ApplicationConfiguration =
    new ApplicationConfiguration(infrastructureConfiguration)

  type ZApplicationConfiguration = Has[ApplicationConfiguration]

  val userApplication: URIO[ZApplicationConfiguration, UserApplication[RStream]] =
    ZIO.access(_.get.userApplication)

  val apiApplication: URIO[ZApplicationConfiguration, ApiApplication[RStream]] =
    ZIO.access(_.get.apiApplication)

  val live: URLayer[ZInfrastructureConfiguration, ZApplicationConfiguration] =
    ZLayer.fromService[InfrastructureConfiguration, ApplicationConfiguration](ApplicationConfiguration(_))

  def make(infrastructureConfiguration: InfrastructureConfiguration): ULayer[ZApplicationConfiguration] =
    ZLayer.succeed(infrastructureConfiguration) >>> live

}
