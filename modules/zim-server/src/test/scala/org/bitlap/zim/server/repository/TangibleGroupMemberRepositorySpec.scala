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

def spec: ZSpec[Environment, Failure] =  suite("Tangible GroupMember Repository")(
  testM("find by id") {
    (for {
      _ <- TangibleGroupMemberRepository.addGroupMember(mockGroupMembers).runHead
      gm <- TangibleGroupMemberRepository.findById(1).runHead
    } yield assert(gm)(equalTo(Some(mockGroupMembers)))
      ).provideLayer(env)
  } @@ TestAspect.before(ZIO.succeed(before))
    @@ TestAspect.after(ZIO.succeed(after))
)

}

object TangibleGroupMemberRepositorySpec {
  trait TangibleGroupMemberRepositoryConfigurationSpec extends BaseSuit {
     val sqlAfter: SQL[_, NoExtractor] =
      sql"""
        drop table if exists t_group;
        drop table if exists t_group_members;
         """
     val sqlBefore: SQL[_, NoExtractor] =
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

    lazy val before: Boolean =
      NamedDB(Symbol(h2ConfigurationProperties.databaseName)).autoCommit { implicit session =>
        sqlBefore.execute().apply()
      }


    lazy val after: Boolean =
      NamedDB(Symbol(h2ConfigurationProperties.databaseName)).autoCommit { implicit session =>
        sqlAfter.execute().apply()
      }
  }



}
