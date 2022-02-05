package org.bitlap.zim.server.repository

import org.bitlap.zim.domain.model.User
import org.bitlap.zim.server.BaseData
import org.bitlap.zim.server.repository.TangibleFriendGroupRepository.ZFriendGroupRepository
import org.bitlap.zim.server.repository.TangibleUserRepositorySpec.TangibleUserRepositoryConfigurationSpec
import scalikejdbc._
import zio.{ Chunk, ULayer, ZLayer }
import org.bitlap.zim.server.repository.TangibleFriendGroupFriendRepository.ZFriendGroupFriendRepository
import org.bitlap.zim.server.repository.TangibleGroupMemberRepository.ZGroupMemberRepository
import org.bitlap.zim.server.repository.TangibleGroupRepository.ZGroupRepository
import org.bitlap.zim.server.repository.TangibleUserRepository.ZUserRepository
import org.bitlap.zim.domain.model._

import scala.language.postfixOps

/**
 * t_user、t_group_members、t_friend_group_friends、t_friend_group 表操作的单测
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

  it should "find users" in {
    val dbUsers = unsafeRun(
      (for {
        _ <- TangibleUserRepository.saveUser(mockUser)
        dbUsers <- TangibleUserRepository.findUsers(None, None)
      } yield dbUsers).runCollect
        .provideLayer(env)
    )
    dbUsers.size shouldBe 1
    dbUsers.headOption.map(u => u.id -> u.username) shouldBe Some(mockUser.id -> mockUser.username)
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
        dbUser1 <- TangibleUserRepository.findUsers(Some("zhangsan"), None)
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
        _ <- TangibleUserRepository.saveUser(mockUser)
        dbUser1 <- TangibleUserRepository.findUsers(Some("zhangsan"), None)
        dbUser2 <- TangibleUserRepository.findUsers(Some("zhangsan"), Some(1))
      } yield dbUser1 -> dbUser2).runCollect
        .provideLayer(env)
    )
    actual.map(u => u._1.id -> u._1.username).headOption shouldBe Some(mockUser.id -> mockUser.username)
    actual.map(u => u._2.id -> u._2.username).headOption shouldBe Some(mockUser.id -> mockUser.username)
  }

  it should "findUserByGroupId by gid" in {
    val actual: Chunk[User] = unsafeRun(
      (for {
        id <- TangibleUserRepository.saveUser(mockUser)
        gid <- TangibleGroupRepository.createGroupList(mockGroupList)
        _ <- TangibleGroupMemberRepository.addGroupMember(GroupMember(gid.toInt, id.toInt))
        dbUser2 <- TangibleUserRepository.findUserByGroupId(gid.toInt)
      } yield dbUser2).runCollect
        .provideLayer(env)
    )

    actual.map(u => u.id -> u.username).headOption shouldBe Some(mockUser.id -> mockUser.username)
  }

  it should "findUsersByFriendGroupIds by fgid" in {
    val actual: Chunk[User] = unsafeRun(
      (for {
        id1 <- TangibleUserRepository.saveUser(mockUser)
        id2 <- TangibleUserRepository.saveUser(mockUser.copy(username = "myname"))
        _ <- TangibleFriendGroupFriendRepository.addFriend(AddFriend(id1.toInt, 11), AddFriend(id2.toInt, 22))
        _ <- TangibleFriendGroupFriendRepository.addFriend(AddFriend(id2.toInt, 11), AddFriend(id1.toInt, 22))
        dbUser2 <- TangibleUserRepository.findUsersByFriendGroupIds(22)
      } yield dbUser2).runCollect
        .provideLayer(env)
    )

    actual.map(u => u.id -> u.username).headOption shouldBe Some(mockUser.id -> mockUser.username)
  }

  it should "find AddFriend by id" in {
    val actual: Option[AddFriend] = unsafeRun(
      (for {
        id1 <- TangibleUserRepository.saveUser(mockUser)
        id2 <- TangibleUserRepository.saveUser(mockUser.copy(username = "myname"))
        _ <- TangibleFriendGroupFriendRepository.addFriend(AddFriend(id1.toInt, 11), AddFriend(id2.toInt, 22))
        ret <- TangibleFriendGroupFriendRepository.findById(1)
      } yield ret).runHead
        .provideLayer(env)
    )

    actual.isDefined
  }

  it should "findUserGroup by uid and mid" in {
    val actual: Option[Int] = unsafeRun(
      (for {
        id1 <- TangibleUserRepository.saveUser(mockUser)
        id2 <- TangibleUserRepository.saveUser(mockUser.copy(username = "myname"))
        _ <- TangibleFriendGroupRepository.createFriendGroup(FriendGroup(1, id1.toInt, "groupname"))
        _ <- TangibleFriendGroupRepository.createFriendGroup(FriendGroup(2, id2.toInt, "groupname"))
        _ <- TangibleFriendGroupFriendRepository.addFriend(AddFriend(id1.toInt, 1), AddFriend(id2.toInt, 2))
        ret <- TangibleFriendGroupFriendRepository.findUserGroup(id1.toInt, id2.toInt)
      } yield ret).runHead
        .provideLayer(env)
    )

    actual shouldBe Some(2)
  }

  it should "removeFriend by friendId and uId" in {
    val actual: Option[Int] = unsafeRun(
      (for {
        id1 <- TangibleUserRepository.saveUser(mockUser)
        id2 <- TangibleUserRepository.saveUser(mockUser.copy(username = "myname"))
        _ <- TangibleFriendGroupRepository.createFriendGroup(FriendGroup(1, id1.toInt, "groupname"))
        _ <- TangibleFriendGroupRepository.createFriendGroup(FriendGroup(2, id2.toInt, "groupname"))
        _ <- TangibleFriendGroupFriendRepository.addFriend(AddFriend(id1.toInt, 1), AddFriend(id2.toInt, 2))
        ret <- TangibleFriendGroupFriendRepository.removeFriend(id1.toInt, id2.toInt)
      } yield ret).runHead
        .provideLayer(env)
    )

    actual shouldBe Some(2)
  }

  it should "changeGroup by groupId and table id" in {
    val actual: Option[Int] = unsafeRun(
      (for {
        id1 <- TangibleUserRepository.saveUser(mockUser)
        id2 <- TangibleUserRepository.saveUser(mockUser.copy(username = "myname"))
        _ <- TangibleFriendGroupRepository.createFriendGroup(FriendGroup(0, id1.toInt, "1-groupname"))
        _ <- TangibleFriendGroupRepository.createFriendGroup(FriendGroup(0, id1.toInt, "1-groupname"))
        _ <- TangibleFriendGroupRepository.createFriendGroup(FriendGroup(0, id2.toInt, "2-groupname"))
        _ <- TangibleFriendGroupFriendRepository.addFriend(AddFriend(1, 1), AddFriend(2, 3))
        id <- TangibleFriendGroupFriendRepository.findUserGroup(1, 2)
        ret <- TangibleFriendGroupFriendRepository.changeGroup(2, id)
      } yield ret).runHead
        .provideLayer(env)
    )

    actual shouldBe Some(1)
  }

  it should "find FriendGroup by id" in {
    val actual: Option[FriendGroup] = unsafeRun(
      (for {
        _ <- TangibleFriendGroupRepository.createFriendGroup(FriendGroup(0, 1, "1-groupname"))
        group <- TangibleFriendGroupRepository.findById(1)
      } yield group).runHead
        .provideLayer(env)
    )

    actual.map(_.groupName) shouldBe Some("1-groupname")
  }

  it should "findFriendGroupsById by uid" in {
    val actual: Chunk[FriendGroup] = unsafeRun(
      (for {
        _ <- TangibleFriendGroupRepository.createFriendGroup(FriendGroup(0, 1, "1-groupname"))
        _ <- TangibleFriendGroupRepository.createFriendGroup(FriendGroup(0, 1, "2-groupname"))
        group <- TangibleFriendGroupRepository.findFriendGroupsById(1)
      } yield group).runCollect
        .provideLayer(env)
    )

    actual.map(_.groupName) shouldBe Chunk("1-groupname", "2-groupname")
  }
}

object TangibleUserRepositorySpec {

  trait TangibleUserRepositoryConfigurationSpec extends BaseData {

    override val sqlAfter: SQL[_, NoExtractor] =
      sql"""
        drop table if exists t_user;
        drop table if exists t_group;
        drop table if exists t_group_members;
        drop table if exists t_friend_group_friends;
        drop table if exists t_friend_group;
         """

    override val sqlBefore: SQL[_, NoExtractor] =
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

            DROP TABLE IF EXISTS `t_group_members`;
            CREATE TABLE `t_group_members` (
              `id` int(20) NOT NULL AUTO_INCREMENT,
              `gid` int(20) NOT NULL COMMENT '群组ID',
              `uid` int(20) NOT NULL,
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

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

    val friendGroupLayer: ULayer[ZFriendGroupRepository] =
      TangibleFriendGroupRepository.make(h2ConfigurationProperties.databaseName)

    val groupLayer: ULayer[ZGroupRepository] =
      TangibleGroupRepository.make(h2ConfigurationProperties.databaseName)

    val groupMemberLayer: ULayer[ZGroupMemberRepository] =
      TangibleGroupMemberRepository.make(h2ConfigurationProperties.databaseName)

    val friendGroupMemberLayer: ULayer[ZFriendGroupFriendRepository] =
      TangibleFriendGroupFriendRepository.make(h2ConfigurationProperties.databaseName)

    // show a layer specified as Throwable
    val userLayer: ZLayer[Any, Throwable, ZUserRepository] =
      TangibleUserRepository.make(h2ConfigurationProperties.databaseName)

    val env: ZLayer[
      Any,
      Throwable,
      ZFriendGroupRepository with ZFriendGroupFriendRepository with ZUserRepository with ZGroupMemberRepository with ZGroupRepository
    ] =
      friendGroupLayer ++ friendGroupMemberLayer ++ userLayer ++ groupMemberLayer ++ groupLayer

  }

}
