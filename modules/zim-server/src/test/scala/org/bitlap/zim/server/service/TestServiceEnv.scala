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

package org.bitlap.zim.server.service
import org.bitlap.zim.infrastructure._
import org.bitlap.zim.infrastructure.repository.TangibleAddMessageRepository.ZAddMessageRepository
import org.bitlap.zim.infrastructure.repository.TangibleFriendGroupFriendRepository.ZFriendGroupFriendRepository
import org.bitlap.zim.infrastructure.repository.TangibleFriendGroupRepository.ZFriendGroupRepository
import org.bitlap.zim.infrastructure.repository.TangibleGroupMemberRepository.ZGroupMemberRepository
import org.bitlap.zim.infrastructure.repository.TangibleGroupRepository.ZGroupRepository
import org.bitlap.zim.infrastructure.repository.TangibleReceiveRepository.ZReceiveRepository
import org.bitlap.zim.infrastructure.repository.TangibleUserRepository.ZUserRepository
import org.bitlap.zim.server.service.UserServiceImpl.ZUserApplication
import zio.{ Layer, TaskLayer, ULayer, ZLayer }

/** 测试service的所有layer
 *
 *  @author
 *    梦境迷离
 *  @since 2022/2/11
 *  @version 1.0
 */
trait TestServiceEnv {

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
    ZUserRepository
      with ZGroupRepository
      with ZReceiveRepository
      with ZFriendGroupRepository
      with ZFriendGroupFriendRepository
      with ZGroupMemberRepository
      with ZAddMessageRepository
  ] = userLayer ++ groupLayer ++ receiveLayer ++ friendGroupLayer ++
    friendGroupMemberLayer ++ groupMemberLayer ++ addMessageLayer

  lazy val userApplicationLayer: TaskLayer[ZUserApplication] = repositoryLayer >>> UserServiceImpl.live

}
