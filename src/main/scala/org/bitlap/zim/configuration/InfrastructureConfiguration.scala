package org.bitlap.zim.configuration

import org.bitlap.zim.configuration.properties.{ MysqlConfigurationProperties, ZimConfigurationProperties }
import org.bitlap.zim.domain.model.User
import org.bitlap.zim.repository.UserRepository
import scalikejdbc.{ ConnectionPool, ConnectionPoolSettings }
import zio._
import org.bitlap.zim.repository.TangibleUserRepository

import org.bitlap.zim.domain.model.GroupList
import org.bitlap.zim.repository.{ GroupRepository, TangibleGroupRepository }

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

  lazy val zimConfigurationProperties: ZimConfigurationProperties = ZimConfigurationProperties()

  lazy val userRepository: UserRepository[User] = TangibleUserRepository(mysqlConfigurationProperties.databaseName)

  lazy val groupRepository: GroupRepository[GroupList] = TangibleGroupRepository(
    mysqlConfigurationProperties.databaseName
  )

}

/**
 * 基础设施依赖管理
 * 目前只有MySQL
 */
object InfrastructureConfiguration {

  def apply(): InfrastructureConfiguration = new InfrastructureConfiguration()

  type ZInfrastructureConfiguration = Has[InfrastructureConfiguration]

  val mysqlConfigurationProperties: URIO[ZInfrastructureConfiguration, MysqlConfigurationProperties] =
    ZIO.access(_.get.mysqlConfigurationProperties)

  val userRepository: URIO[ZInfrastructureConfiguration, UserRepository[User]] =
    ZIO.access(_.get.userRepository)

  val groupRepository: URIO[ZInfrastructureConfiguration, GroupRepository[GroupList]] =
    ZIO.access(_.get.groupRepository)

  val live: ULayer[ZInfrastructureConfiguration] =
    ZLayer.succeed[InfrastructureConfiguration](InfrastructureConfiguration())

}
