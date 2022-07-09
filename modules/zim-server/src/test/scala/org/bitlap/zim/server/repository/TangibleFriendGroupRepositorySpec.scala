package org.bitlap.zim.server.repository

import org.bitlap.zim.infrastructure.repository.TangibleFriendGroupRepository
import org.bitlap.zim.infrastructure.repository.TangibleFriendGroupRepository.ZFriendGroupRepository
import org.bitlap.zim.server.BaseSuit
import org.bitlap.zim.server.repository.TangibleFriendGroupRepositorySpec.TangibleFriendGroupRepositorySpec
import scalikejdbc._
import zio._
import zio.test.Assertion._
import zio.test._

object TangibleFriendGroupRepositoryMainSpec extends TangibleFriendGroupRepositorySpec {
  override def spec: ZSpec[Environment, Failure]  = suite("Tangible friendGroup repository") (
    testM("find by id") {
      for {
            _  <- TangibleFriendGroupRepository.createFriendGroup(mockFriendGroup).runHead
            fg <- TangibleFriendGroupRepository.findById(1).runHead
      } yield assert(fg)(equalTo(Some(mockFriendGroup)))
    } @@ TestAspect.before(ZIO.succeed(before)),

    testM("find friendGroups by id") {
      for {
            fg <- TangibleFriendGroupRepository.findFriendGroupsById(mockFriendGroup.uid).runHead
      } yield assert(fg)(equalTo(Some(mockFriendGroup)))
    } @@ TestAspect.after(ZIO.succeed(after))

  ).provideLayer(env) @@ TestAspect.sequential
}


object TangibleFriendGroupRepositorySpec {
  trait TangibleFriendGroupRepositorySpec extends BaseSuit {
    override val sqlAfter: SQL[_, NoExtractor] =
      sql"""
           |        drop table if exists t_friend_group_friends;
           |        drop table if exists t_friend_group;
           |""".stripMargin

    override val sqlBefore: SQL[_, NoExtractor] =
      sql"""
       DROP TABLE IF EXISTS `t_friend_group_friends`;
            CREATE TABLE `t_friend_group_friends` (
              `id` int(10) NOT NULL AUTO_INCREMENT,
              `fgid` int(10) NOT NULL COMMENT '分组id',
              `uid` int(10) NOT NULL COMMENT '用户id',
              PRIMARY KEY (`id`),
              UNIQUE KEY `g_uid_unique` (`fgid`,`uid`)
            ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

            DROP TABLE IF EXISTS `t_friend_group`;
            CREATE TABLE `t_friend_group` (
              `id` int(10) NOT NULL AUTO_INCREMENT,
              `uid` int(10) NOT NULL COMMENT '该分组所属的用户ID',
              `group_name` varchar(64) NOT NULL COMMENT '分组名称',
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
       """


    val env: ULayer[ZFriendGroupRepository] = TangibleFriendGroupRepository.make(h2ConfigurationProperties.databaseName)
  }

}