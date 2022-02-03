package org.bitlap.zim.server.configuration

import org.bitlap.zim.domain.model
import org.bitlap.zim.domain.model.{ AddFriend, FriendGroup, GroupList, GroupMember, Receive, User }
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

  lazy val friendGroupRepository: FriendGroupRepository[FriendGroup] = TangibleFriendGroupRepository(
    mysqlConfigurationProperties.databaseName
  )

  lazy val friendGroupFriendRepository: FriendGroupFriendRepository[AddFriend] = TangibleFriendGroupFriendRepository(
    mysqlConfigurationProperties.databaseName
  )

  lazy val groupMemberRepository: GroupMemberRepository[GroupMember] = TangibleGroupMemberRepository(
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

  val friendGroupFriendRepository: URIO[ZInfrastructureConfiguration, FriendGroupFriendRepository[AddFriend]] =
    ZIO.access(_.get.friendGroupFriendRepository)

  val groupMemberRepository: URIO[ZInfrastructureConfiguration, GroupMemberRepository[GroupMember]] =
    ZIO.access(_.get.groupMemberRepository)

  val addMessageRepository: URIO[ZInfrastructureConfiguration, AddMessageRepository] =
    ZIO.access(_.get.addMessageRepository)

  val live: ULayer[ZInfrastructureConfiguration] =
    ZLayer.succeed[InfrastructureConfiguration](InfrastructureConfiguration())

}
