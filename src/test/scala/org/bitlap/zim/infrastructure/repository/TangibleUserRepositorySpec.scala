package org.bitlap.zim.infrastructure.repository

import org.bitlap.zim.BaseData
import org.bitlap.zim.domain.model.User
import org.bitlap.zim.infrastructure.repository.TangibleUserRepositorySpec.TangibleUserRepositoryConfigurationSpec
import org.bitlap.zim.repository.TangibleUserRepository
import org.bitlap.zim.repository.TangibleUserRepository.ZUserRepository
import scalikejdbc._
import zio.{ Chunk, ULayer, ZLayer }

/**
 * t_user表操作的单测
 *
 * @author 梦境迷离
 * @since 2022/1/2
 * @version 1.0
 */
final class TangibleUserRepositorySpec extends TangibleUserRepositoryConfigurationSpec {

  behavior of "Tangible User Repository"

  it should "find by id" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[User] = unsafeRun(
      (for {
        id <- TangibleUserRepository.saveUser(mockUser)
        dbUser <- TangibleUserRepository.findById(id.toInt)
      } yield dbUser).runHead
        .provideLayer(env)
    )

    // t h e n
    actual.map(u => u.id -> u.username) shouldBe Some(mockUser.id -> mockUser.username)

  }

  it should "matchUser by email" in {
    val actual: Option[User] = unsafeRun(
      (for {
        id <- TangibleUserRepository.saveUser(mockUser)
        dbUser <- TangibleUserRepository.matchUser(mockUser.email)
      } yield dbUser).runHead
        .provideLayer(env)
    )
    actual.map(u => u.id -> u.email) shouldBe Some(mockUser.id -> mockUser.email)
  }

  it should "activeUser by active" in {
    val actual: Option[Int] = unsafeRun(
      (for {
        id <- TangibleUserRepository.saveUser(mockUser)
        dbRet <- TangibleUserRepository.activeUser(mockUser.active)
      } yield dbRet).runHead
        .provideLayer(env)
    )
    actual shouldBe Some(1)
  }

  it should "findUser by username" in {
    val actual: Option[User] = unsafeRun(
      (for {
        id <- TangibleUserRepository.saveUser(mockUser)
        dbUser1 <- TangibleUserRepository.findUser(Some("zhangsan"), None)
      } yield dbUser1).runHead
        .provideLayer(env)
    )
    actual.map(u => u.id -> u.username) shouldBe Some(mockUser.id -> mockUser.username)
  }

  it should "updateAvatar by uid" in {
    val actual: Option[Int] = unsafeRun(
      (for {
        id <- TangibleUserRepository.saveUser(mockUser)
        dbUser1 <- TangibleUserRepository.updateAvatar("2", id.toInt)
      } yield dbUser1).runHead
        .provideLayer(env)
    )
    actual shouldBe Some(1)
  }

  it should "updateSign by uid" in {
    val actual: Option[Int] = unsafeRun(
      (for {
        id <- TangibleUserRepository.saveUser(mockUser)
        dbUser1 <- TangibleUserRepository.updateSign("1", id.toInt)
      } yield dbUser1).runHead
        .provideLayer(env)
    )
    actual shouldBe Some(1)
  }

  it should "updateUserStatus by uid" in {
    val actual: Option[User] = unsafeRun(
      (for {
        id <- TangibleUserRepository.saveUser(mockUser)
        _ <- TangibleUserRepository.updateUserStatus("hide", id.toInt)
        dbUser <- TangibleUserRepository.findById(id.toInt)
      } yield dbUser).runHead
        .provideLayer(env)
    )
    actual.map(u => u.id -> u.status) shouldBe Some(mockUser.id -> "hide")
  }

  it should "updateUserInfo by uid" in {
    val actual: Option[User] = unsafeRun(
      (for {
        id <- TangibleUserRepository.saveUser(mockUser)
        _ <- TangibleUserRepository.updateUserInfo(id.toInt, mockUser.copy(username = "lisi"))
        dbUser <- TangibleUserRepository.findById(id.toInt)
      } yield dbUser).runHead
        .provideLayer(env)
    )
    actual.map(u => u.id -> u.username) shouldBe Some(mockUser.id -> "lisi")
  }

  it should "countUser by uid" in {
    val actual: Option[Int] = unsafeRun(
      (for {
        id <- TangibleUserRepository.saveUser(mockUser)
        dbRet <- TangibleUserRepository.countUser(None, Some(mockUser.sex))
      } yield dbRet).runHead
        .provideLayer(env)
    )
    actual shouldBe Some(1)
  }

  it should "findUser by username and sex" in {
    val actual: Chunk[(User, User)] = unsafeRun(
      (for {
        id <- TangibleUserRepository.saveUser(mockUser)
        dbUser1 <- TangibleUserRepository.findUser(Some("zhangsan"), None)
        dbUser2 <- TangibleUserRepository.findUser(Some("zhangsan"), Some(1))
      } yield dbUser1 -> dbUser2).runCollect
        .provideLayer(env)
    )
    actual.map(u => u._1.id -> u._1.username).headOption shouldBe Some(mockUser.id -> mockUser.username)
    actual.map(u => u._2.id -> u._2.username).headOption shouldBe Some(mockUser.id -> mockUser.username)
  }

  // 需要表t_group和t_group_members的repository
  // TODO findUserByGroupId  findUsersByFriendGroupIds

}

object TangibleUserRepositorySpec {

  trait TangibleUserRepositoryConfigurationSpec extends BaseData {

    override val table: SQL[_, NoExtractor] =
      sql"""
           DROP TABLE IF EXISTS `t_user`;
            CREATE TABLE `t_user` (
              `id` int(10) NOT NULL AUTO_INCREMENT,
              `username` varchar(64) NOT NULL COMMENT '用户名',
              `password` varchar(128) NOT NULL COMMENT '密码',
              `sign` varchar(255) DEFAULT NULL COMMENT '签名',
              `email` varchar(64) NOT NULL COMMENT '邮箱地址',
              `avatar` varchar(255) DEFAULT '/static/image/avatar/avatar(3).jpg' COMMENT '头像地址',
              `sex` int(2) NOT NULL DEFAULT '1' COMMENT '性别',
              `active` varchar(64) NOT NULL COMMENT '激活码',
              `status` varchar(16) NOT NULL DEFAULT 'nonactivated' COMMENT '是否激活',
              `create_date` date NOT NULL COMMENT '创建时间',
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
            DROP TABLE IF EXISTS `t_group`;
            CREATE TABLE `t_group` (
              `id` int(20) NOT NULL AUTO_INCREMENT,
              `group_name` varchar(64) NOT NULL COMMENT '群组名称',
              `avatar` varchar(255) NOT NULL DEFAULT '' COMMENT '群组图标',
              `create_id` int(20) NOT NULL COMMENT '创建者id',
              `create_time` timestamp NOT NULL DEFAULT '1971-01-01 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
            DROP TABLE IF EXISTS `t_group`;
            CREATE TABLE `t_group` (
              `id` int(20) NOT NULL AUTO_INCREMENT,
              `group_name` varchar(64) NOT NULL COMMENT '群组名称',
              `avatar` varchar(255) NOT NULL DEFAULT '' COMMENT '群组图标',
              `create_id` int(20) NOT NULL COMMENT '创建者id',
              `create_time` timestamp NOT NULL DEFAULT '1971-01-01 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
         """

    val env: ULayer[ZUserRepository] = ZLayer.succeed(h2ConfigurationProperties.databaseName) >>>
      TangibleUserRepository.live

  }

}
