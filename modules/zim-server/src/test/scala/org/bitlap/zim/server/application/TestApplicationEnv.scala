/*
 * Copyright 2021 bitlap
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

package org.bitlap.zim.server.application

import org.bitlap.zim.server.application.impl.UserService
import org.bitlap.zim.server.application.impl.UserService.ZUserApplication
import org.bitlap.zim.server.configuration.InfrastructureConfiguration
import org.bitlap.zim.server.repository.TangibleAddMessageRepository.ZAddMessageRepository
import org.bitlap.zim.server.repository.TangibleFriendGroupFriendRepository.ZFriendGroupFriendRepository
import org.bitlap.zim.server.repository.TangibleFriendGroupRepository.ZFriendGroupRepository
import org.bitlap.zim.server.repository.TangibleGroupMemberRepository.ZGroupMemberRepository
import org.bitlap.zim.server.repository.TangibleGroupRepository.ZGroupRepository
import org.bitlap.zim.server.repository.TangibleReceiveRepository.ZReceiveRepository
import org.bitlap.zim.server.repository.TangibleUserRepository.ZUserRepository
import zio.{ Layer, TaskLayer, ULayer, ZLayer }

/**
 * 测试service的所有layer
 *
 * @author 梦境迷离
 * @since 2022/2/11
 * @version 1.0
 */
trait TestApplicationEnv {

  lazy val infra = InfrastructureConfiguration()

  val friendGroupLayer: ULayer[ZFriendGroupRepository] = ZLayer.succeed(infra.friendGroupRepository)

  val groupLayer: ULayer[ZGroupRepository] = ZLayer.succeed(infra.groupRepository)

  val receiveLayer: ULayer[ZReceiveRepository] = ZLayer.succeed(infra.receiveRepository)

  val groupMemberLayer: ULayer[ZGroupMemberRepository] = ZLayer.succeed(infra.groupMemberRepository)

  val friendGroupMemberLayer: ULayer[ZFriendGroupFriendRepository] = ZLayer.succeed(infra.friendGroupFriendRepository)

  val addMessageLayer: ULayer[ZAddMessageRepository] = ZLayer.succeed(infra.addMessageRepository)

  val userLayer: ZLayer[Any, Throwable, ZUserRepository] =
    ZLayer.succeed(infra.userRepository)

  val repositoryLayer: Layer[
    Throwable,
    ZUserRepository with ZGroupRepository with ZReceiveRepository with ZFriendGroupRepository with ZFriendGroupFriendRepository with ZGroupMemberRepository with ZAddMessageRepository
  ] = userLayer ++ groupLayer ++ receiveLayer ++ friendGroupLayer ++
    friendGroupMemberLayer ++ groupMemberLayer ++ addMessageLayer

  lazy val userApplicationLayer: TaskLayer[ZUserApplication] = repositoryLayer >>> UserService.live

}
