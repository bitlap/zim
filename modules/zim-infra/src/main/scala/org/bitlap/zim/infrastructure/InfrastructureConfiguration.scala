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

package org.bitlap.zim.infrastructure

import org.bitlap.zim.api.repository._
import org.bitlap.zim.infrastructure.properties._
import org.bitlap.zim.infrastructure.repository.{RStream, _}

import scalikejdbc._
import zio._

/** infrastructure configuration
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 1.0
 */
final class InfrastructureConfiguration {

  def initPool(): URIO[Any, Unit] = {
    ZIO.attempt {
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
    }.orDie
  }

  lazy val mysqlConfigurationProperties: MysqlConfigurationProperties = MysqlConfigurationProperties()

  lazy val userRepository: UserRepository[RStream] = TangibleUserRepository(mysqlConfigurationProperties.databaseName)

  lazy val groupRepository: GroupRepository[RStream] = TangibleGroupRepository(
    mysqlConfigurationProperties.databaseName
  )

  lazy val receiveRepository: ReceiveRepository[RStream] = TangibleReceiveRepository(
    mysqlConfigurationProperties.databaseName
  )

  lazy val friendGroupRepository: FriendGroupRepository[RStream] = TangibleFriendGroupRepository(
    mysqlConfigurationProperties.databaseName
  )

  lazy val friendGroupFriendRepository: FriendGroupFriendRepository[RStream] = TangibleFriendGroupFriendRepository(
    mysqlConfigurationProperties.databaseName
  )

  lazy val groupMemberRepository: GroupMemberRepository[RStream] = TangibleGroupMemberRepository(
    mysqlConfigurationProperties.databaseName
  )

  lazy val addMessageRepository: AddMessageRepository[RStream] = TangibleAddMessageRepository(
    mysqlConfigurationProperties.databaseName
  )
}

/** infrastructure dependencies
 */
object InfrastructureConfiguration {

  lazy val live: ULayer[InfrastructureConfiguration] =
    ZLayer.succeed[InfrastructureConfiguration](new InfrastructureConfiguration())

}
