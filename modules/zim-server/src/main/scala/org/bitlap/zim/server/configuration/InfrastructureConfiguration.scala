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

package org.bitlap.zim.server.configuration

import org.bitlap.zim.domain.model.Receive
import org.bitlap.zim.domain.repository.{
  AddMessageRepository,
  FriendGroupFriendRepository,
  FriendGroupRepository,
  GroupMemberRepository,
  GroupRepository,
  ReceiveRepository,
  UserRepository
}
import org.bitlap.zim.server.configuration.properties.{
  MailConfigurationProperties,
  MysqlConfigurationProperties,
  ZimConfigurationProperties
}
import org.bitlap.zim.server.repository.{
  TangibleAddMessageRepository,
  TangibleFriendGroupFriendRepository,
  TangibleFriendGroupRepository,
  TangibleGroupMemberRepository,
  TangibleGroupRepository,
  TangibleReceiveRepository,
  TangibleUserRepository
}
import scalikejdbc.{ ConnectionPool, ConnectionPoolSettings }
import zio._

/**
 * infrastructure configuration
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
final class InfrastructureConfiguration {

  ConnectionPool.add(
    Symbol(mysqlConfigurationProperties.databaseName),
    mysqlConfigurationProperties.url,
    mysqlConfigurationProperties.user,
    mysqlConfigurationProperties.password,
    ConnectionPoolSettings(
      initialSize = mysqlConfigurationProperties.initialSize,
      maxSize = mysqlConfigurationProperties.maxSize,
      connectionTimeoutMillis = mysqlConfigurationProperties.connectionTimeoutMillis,
      validationQuery = mysqlConfigurationProperties.validationQuery,
      driverName = mysqlConfigurationProperties.driverName
    )
  )

  lazy val mysqlConfigurationProperties: MysqlConfigurationProperties = MysqlConfigurationProperties()

  lazy val userRepository: UserRepository = TangibleUserRepository(mysqlConfigurationProperties.databaseName)

  lazy val groupRepository: GroupRepository = TangibleGroupRepository(
    mysqlConfigurationProperties.databaseName
  )
  lazy val receiveRepository: ReceiveRepository[Receive] = TangibleReceiveRepository(
    mysqlConfigurationProperties.databaseName
  )

  lazy val friendGroupRepository: FriendGroupRepository = TangibleFriendGroupRepository(
    mysqlConfigurationProperties.databaseName
  )

  lazy val friendGroupFriendRepository: FriendGroupFriendRepository = TangibleFriendGroupFriendRepository(
    mysqlConfigurationProperties.databaseName
  )

  lazy val groupMemberRepository: GroupMemberRepository = TangibleGroupMemberRepository(
    mysqlConfigurationProperties.databaseName
  )
  lazy val addMessageRepository: AddMessageRepository = TangibleAddMessageRepository(
    mysqlConfigurationProperties.databaseName
  )
}

/**
 * infrastructure dependencies
 */
object InfrastructureConfiguration {

  def apply(): InfrastructureConfiguration = new InfrastructureConfiguration()

  type ZInfrastructureConfiguration = Has[InfrastructureConfiguration]

  // ==================================system configuration============================================
  val mysqlConfigurationProperties: URIO[ZInfrastructureConfiguration, MysqlConfigurationProperties] =
    ZIO.access(_.get.mysqlConfigurationProperties)

  val zimConfigurationProperties: UIO[ZimConfigurationProperties] =
    ZimConfigurationProperties.make

  val mailConfigurationProperties: UIO[MailConfigurationProperties] =
    MailConfigurationProperties.make

  // ==================================数据库============================================
  val userRepository: URIO[ZInfrastructureConfiguration, UserRepository] =
    ZIO.access(_.get.userRepository)

  val groupRepository: URIO[ZInfrastructureConfiguration, GroupRepository] =
    ZIO.access(_.get.groupRepository)

  val receiveRepository: URIO[ZInfrastructureConfiguration, ReceiveRepository[Receive]] =
    ZIO.access(_.get.receiveRepository)

  val friendGroupFriendRepository: URIO[ZInfrastructureConfiguration, FriendGroupFriendRepository] =
    ZIO.access(_.get.friendGroupFriendRepository)

  val groupMemberRepository: URIO[ZInfrastructureConfiguration, GroupMemberRepository] =
    ZIO.access(_.get.groupMemberRepository)

  val addMessageRepository: URIO[ZInfrastructureConfiguration, AddMessageRepository] =
    ZIO.access(_.get.addMessageRepository)

  val live: ULayer[ZInfrastructureConfiguration] =
    ZLayer.succeed[InfrastructureConfiguration](InfrastructureConfiguration())

}
