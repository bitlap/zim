package org.bitlap.zim.configuration

import org.bitlap.zim.configuration.properties.{
  MailConfigurationProperties,
  MysqlConfigurationProperties,
  ZimConfigurationProperties
}
import org.bitlap.zim.domain.model.{ AddFriend, AddMessage, FriendGroup, GroupList, GroupMember, Receive, User }
import org.bitlap.zim.repository.{
  AddMessageRepository,
  FriendGroupFriendRepository,
  FriendGroupRepository,
  GroupMemberRepository,
  GroupRepository,
  ReceiveRepository,
  TangibleAddMessageRepository,
  TangibleFriendGroupFriendRepository,
  TangibleFriendGroupRepository,
  TangibleGroupMemberRepository,
  TangibleGroupRepository,
  TangibleReceiveRepository,
  TangibleUserRepository,
  UserRepository
}
import scalikejdbc.{ ConnectionPool, ConnectionPoolSettings }
import zio._

/**
 * 基础设施配置
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

  lazy val userRepository: UserRepository[User] = TangibleUserRepository(mysqlConfigurationProperties.databaseName)

  lazy val groupRepository: GroupRepository[GroupList] = TangibleGroupRepository(
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
  lazy val addMessageRepository: AddMessageRepository[AddMessage] = TangibleAddMessageRepository(
    mysqlConfigurationProperties.databaseName
  )
}

/**
 * 基础设施依赖管理
 */
object InfrastructureConfiguration {

  def apply(): InfrastructureConfiguration = new InfrastructureConfiguration()

  type ZInfrastructureConfiguration = Has[InfrastructureConfiguration]

  // ==================================系统配置============================================
  val mysqlConfigurationProperties: URIO[ZInfrastructureConfiguration, MysqlConfigurationProperties] =
    ZIO.access(_.get.mysqlConfigurationProperties)

  val zimConfigurationProperties: UIO[ZimConfigurationProperties] =
    ZimConfigurationProperties.make

  val mailConfigurationProperties: UIO[MailConfigurationProperties] =
    MailConfigurationProperties.make

  // ==================================数据库============================================
  val userRepository: URIO[ZInfrastructureConfiguration, UserRepository[User]] =
    ZIO.access(_.get.userRepository)

  val groupRepository: URIO[ZInfrastructureConfiguration, GroupRepository[GroupList]] =
    ZIO.access(_.get.groupRepository)

  val receiveRepository: URIO[ZInfrastructureConfiguration, ReceiveRepository[Receive]] =
    ZIO.access(_.get.receiveRepository)

  val friendGroupFriendRepository: URIO[ZInfrastructureConfiguration, FriendGroupFriendRepository[AddFriend]] =
    ZIO.access(_.get.friendGroupFriendRepository)

  val groupMemberRepository: URIO[ZInfrastructureConfiguration, GroupMemberRepository[GroupMember]] =
    ZIO.access(_.get.groupMemberRepository)

  val addMessageRepository: URIO[ZInfrastructureConfiguration, AddMessageRepository[AddMessage]] =
    ZIO.access(_.get.addMessageRepository)

  val live: ULayer[ZInfrastructureConfiguration] =
    ZLayer.succeed[InfrastructureConfiguration](InfrastructureConfiguration())

}
