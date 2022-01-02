package org.bitlap.zim.infrastructure.repository

import org.bitlap.zim.BaseData
import org.bitlap.zim.domain.model.User
import org.bitlap.zim.infrastructure.repository.TangibleUserRepositorySpec.TangibleUserRepositoryConfigurationSpec
import org.bitlap.zim.repository.TangibleUserRepository
import org.bitlap.zim.repository.TangibleUserRepository.ZUserRepository
import scalikejdbc._
import zio.{ ULayer, ZLayer }

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
         """

    val env: ULayer[ZUserRepository] = ZLayer.succeed(h2ConfigurationProperties.databaseName) >>>
      TangibleUserRepository.live

  }

}
