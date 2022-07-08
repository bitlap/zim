package org.bitlap.zim.server.repository
import org.bitlap.zim.infrastructure.repository.TangibleGroupMemberRepository
import org.bitlap.zim.infrastructure.repository.TangibleGroupMemberRepository.ZGroupMemberRepository
import org.bitlap.zim.server.BaseSuit
import org.bitlap.zim.server.repository.TangibleGroupMemberRepositorySpec.TangibleGroupMemberRepositoryConfigurationSpec
import scalikejdbc._
import zio.{ULayer, ZIO}
import zio.test._
import zio.test.Assertion._


object TangibleGroupMemberRepositoryMainSpec extends  TangibleGroupMemberRepositoryConfigurationSpec{
  override def spec: ZSpec[Environment, Failure] =  suite("Tangible GroupMember Repository")(
    testM("find by id") {
      for {
        _  <- TangibleGroupMemberRepository.addGroupMember(mockGroupMembers).runHead
        gm <- TangibleGroupMemberRepository.findById(mockGroupMembers.id).runHead
      } yield assert(gm)(equalTo(Some(mockGroupMembers)))
    } @@ TestAspect.before(ZIO.succeed(before)),

    testM("find group members") {
      for {
        uid <- TangibleGroupMemberRepository.findGroupMembers(mockGroupMembers.gid).runHead
      } yield assert(uid)(equalTo(Some(1)))
    },

    testM("leave out group") {
      for {
        _  <- TangibleGroupMemberRepository.leaveOutGroup(mockGroupMembers).runHead
        gm <-  TangibleGroupMemberRepository.findById(mockGroupMembers.id).runHead
      } yield assert(gm)(equalTo(None))
    } @@ TestAspect.after(ZIO.succeed(after))

  ).provideLayer(env) @@ TestAspect.sequential

}

object TangibleGroupMemberRepositorySpec {
  trait TangibleGroupMemberRepositoryConfigurationSpec extends BaseSuit {
     override val sqlAfter: SQL[_, NoExtractor] =
      sql"""
        drop table if exists t_group;
        drop table if exists t_group_members;
         """
    override val sqlBefore: SQL[_, NoExtractor] =
      sql"""
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
         """
    val env: ULayer[ZGroupMemberRepository] = TangibleGroupMemberRepository.make(h2ConfigurationProperties.databaseName)
  }



}
