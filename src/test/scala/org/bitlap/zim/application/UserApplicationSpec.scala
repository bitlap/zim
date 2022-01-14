package org.bitlap.zim.application

import zio.ZIO

/**
 * @author 梦境迷离
 * @since 2022/1/13
 * @version 1.0
 */
final class UserApplicationSpec extends TestApplication {

  "UserApplication" should "saveUser ok" in {
    // 随便写 这里同时测试2个方法会出现数据库重新被重置 导致出现无表的错误
    val stream = (for {
      user <- ZIO.serviceWith[UserApplication](_.saveUser(mockUser).runHead)
    } yield user).provideLayer(userApplicationLayer)
    val ret = unsafeRun(stream)
    ret shouldBe Some(true)
  }

  "UserApplication" should "findUserById ok" in {
    // 随便写
    val stream = (for {
      user <- ZIO.serviceWith[UserApplication](_.findUserById(mockUser.id).runHead)
    } yield user).provideLayer(userApplicationLayer)
    val ret = unsafeRun(stream)
    ret shouldBe None
  }
}
