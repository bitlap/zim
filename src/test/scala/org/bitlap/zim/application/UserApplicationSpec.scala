package org.bitlap.zim.application

import zio.ZIO

/**
 * @author 梦境迷离
 * @since 2022/1/13
 * @version 1.0
 */
final class UserApplicationSpec extends TestApplication {

  "UserApplication" should "saveUser ok" in {
    val stream = (for {
      _ <- ZIO.serviceWith[UserApplication](_.saveUser(mockUser).runHead)
      user <- ZIO.serviceWith[UserApplication](_.findUserById(mockUser.id).runHead)
    } yield user).provideLayer(userApplicationLayer)
    val ret = unsafeRun(stream)
    ret.map(_.username) shouldBe Some(mockUser.username)
  }

  "UserApplication" should "matchUser ok" in {
    val stream = (for {
      _ <- ZIO.serviceWith[UserApplication](_.saveUser(mockUser).runHead)
      user <- ZIO.serviceWith[UserApplication](_.matchUser(mockUser).runHead)
    } yield user).provideLayer(userApplicationLayer)
    val ret = unsafeRun(stream)
    ret.map(_.password) shouldBe Some("jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=")
  }

  /**
   *    TODO 还需要测试的方法
   *    leaveOutGroup
   *    addGroupMember
   *    changeGroup
   *    addFriend
   *    findAddInfo
   *    countHistoryMessage
   *    findHistoryMessage
   *    findFriendGroupsById
   */
}
