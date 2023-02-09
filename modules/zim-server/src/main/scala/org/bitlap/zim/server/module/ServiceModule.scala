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

package org.bitlap.zim.server.module

import org.bitlap.zim.api.service._
import org.bitlap.zim.infrastructure._
import org.bitlap.zim.infrastructure.repository.RStream
import org.bitlap.zim.server.service._
import zio._

/** Service
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 1.0
 */
final class ServiceModule(infrastructureConfiguration: InfrastructureConfiguration) {

  // multi application
  val userService: UserService[RStream] = UserServiceImpl(
    infrastructureConfiguration.userRepository,
    infrastructureConfiguration.groupRepository,
    infrastructureConfiguration.receiveRepository,
    infrastructureConfiguration.friendGroupRepository,
    infrastructureConfiguration.friendGroupFriendRepository,
    infrastructureConfiguration.groupMemberRepository,
    infrastructureConfiguration.addMessageRepository
  )

  val apiService: ApiService[RStream, Task] = new ApiServiceImpl(userService)

}

/** application dependencies
 */
object ServiceModule {

  def apply(infrastructureConfiguration: InfrastructureConfiguration): ServiceModule =
    new ServiceModule(infrastructureConfiguration)

  val live: URLayer[InfrastructureConfiguration, ServiceModule] = ZLayer {
    for {
      infra <- ZIO.service[InfrastructureConfiguration]
    } yield ServiceModule(infra)
  }
}
