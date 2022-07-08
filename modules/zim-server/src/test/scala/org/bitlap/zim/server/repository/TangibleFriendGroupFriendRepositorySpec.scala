package org.bitlap.zim.server.repository

import org.bitlap.zim.domain.model.{AddFriend, FriendGroup}
import org.bitlap.zim.infrastructure.repository.{TangibleFriendGroupFriendRepository, TangibleFriendGroupRepository}
import org.bitlap.zim.infrastructure.repository.TangibleFriendGroupFriendRepository.ZFriendGroupFriendRepository
import org.bitlap.zim.infrastructure.repository.TangibleFriendGroupRepository.ZFriendGroupRepository
import org.bitlap.zim.server.BaseSuit
import org.bitlap.zim.server.repository.TangibleFriendGroupFriendRepositorySpec.TangibleFriendGroupFriendRepositorySpec
import scalikejdbc._
import zio._
import zio.test._
import zio.test.Assertion._

object TangibleFriendGroupFriendRepositoryMainSpec extends TangibleFriendGroupFriendRepositorySpec{
  override def spec: ZSpec[Environment, Failure]  = suite("Tangible friendGroupFriend repository") (
    testM("find user group") {
      for {
        _  <- TangibleFriendGroupFriendRepository.addFriend(AddFriend(id = 1,uid = 1,fgid = 2),AddFriend(id = 2,uid =2,fgid = 1)).runHead
        _  <- TangibleFriendGroupRepository.createFriendGroup(FriendGroup(id = 2,uid = 1,groupName = "zim")).runHead
        id <- TangibleFriendGroupFriendRepository.findUserGroup(1,1).runHead
      } yield assert(id)(equalTo(Some(2)))
    } @@ TestAspect.before(ZIO.succeed(before)),

    testM("find by id ") {
      for {
        af  <- TangibleFriendGroupFriendRepository.findById(2).runHead
      } yield assert(af.map(_.fgid))(equalTo(Some(1)))
    },
    testM("remove friend") {
      for {
        _   <- TangibleFriendGroupFriendRepository.removeFriend(1,1).runHead
        af  <- TangibleFriendGroupFriendRepository.findById(2).runHead
      } yield assert(af)(equalTo(None))
    },
    testM("change group ") {
      for {
        _   <- TangibleFriendGroupFriendRepository.changeGroup(3,1).runHead
        af  <- TangibleFriendGroupFriendRepository.findById(1).runHead
      } yield assert(af.map(_.fgid))(equalTo(Some(3)))
    } @@ TestAspect.after(ZIO.succeed(after))

  ).provideLayer(env) @@ TestAspect.sequential
}

object TangibleFriendGroupFriendRepositorySpec {
 trait TangibleFriendGroupFriendRepositorySpec extends BaseSuit {
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

   val  friendGroupFriendLayer = TangibleFriendGroupFriendRepository.make(h2ConfigurationProperties.databaseName)
   val  friendGroupLayer       = TangibleFriendGroupRepository.make(h2ConfigurationProperties.databaseName)

   val env: ULayer[ZFriendGroupRepository with ZFriendGroupFriendRepository] = friendGroupFriendLayer ++ friendGroupLayer
 }
}
