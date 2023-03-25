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

package org.bitlap.zim.server.service
import org.bitlap.zim.api.repository._
import org.bitlap.zim.api.service.UserService
import org.bitlap.zim.infrastructure._
import org.bitlap.zim.infrastructure.repository.RStream
import zio._

/** 测试service的所有layer
 *
 *  @author
 *    梦境迷离
 *  @since 2022/2/11
 *  @version 1.0
 */
trait TestServiceEnv {

  lazy val infra: InfrastructureConfiguration = new InfrastructureConfiguration()

  val friendGroupLayer: ULayer[FriendGroupRepository[RStream]] = ZLayer.succeed(infra.friendGroupRepository)

  val groupLayer: ULayer[GroupRepository[RStream]] = ZLayer.succeed(infra.groupRepository)

  val receiveLayer: ULayer[ReceiveRepository[RStream]] = ZLayer.succeed(infra.receiveRepository)

  val groupMemberLayer: ULayer[GroupMemberRepository[RStream]] = ZLayer.succeed(infra.groupMemberRepository)

  val friendGroupMemberLayer: ULayer[FriendGroupFriendRepository[RStream]] =
    ZLayer.succeed(infra.friendGroupFriendRepository)

  val addMessageLayer: ULayer[AddMessageRepository[RStream]] = ZLayer.succeed(infra.addMessageRepository)

  val userLayer: ZLayer[Any, Throwable, UserRepository[RStream]] =
    ZLayer.succeed(infra.userRepository)

  lazy val userServiceLayer: ZLayer[Any, Throwable, UserService[RStream]] =
    ZLayer.succeed(infra) >>> UserServiceImpl.live

}
