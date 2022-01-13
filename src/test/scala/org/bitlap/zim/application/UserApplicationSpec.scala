package org.bitlap.zim.application

import zio.ZIO

/**
 * @author 梦境迷离
 * @since 2022/1/13
 * @version 1.0
 */
final class UserApplicationSpec extends TestApplication {

  "UserApplication" should "findUserById ok" in {
    // 随便写
    val stream = (for {
      _ <- ZIO.serviceWith[UserApplication](_.saveUser(mockUser).runHead)
      user <- ZIO.serviceWith[UserApplication](_.findUserById(1).runHead)
    } yield user).provideLayer(userApplicationLayer)
    val userOpt = unsafeRun(stream)
    userOpt.map(_.username) shouldBe Some(mockUser.username)
  }

}
