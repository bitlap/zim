package org.bitlap.zim.application

import org.bitlap.zim.domain.model.{ AddMessage, GroupList }
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

  "UserApplication" should "creator leaveOutGroup ok" in {
    val stream = (for {
      _ <- ZIO.serviceWith[UserApplication](_.saveUser(mockUser).runHead)
      _ <- ZIO.serviceWith[UserApplication](_.createGroup(GroupList(1, "g", "", 1)).runHead)
      _ <- ZIO.serviceWith[UserApplication](_.addGroupMember(1, 1).runHead)
      leave <- ZIO.serviceWith[UserApplication](_.leaveOutGroup(1, 1).runHead)
      group <- ZIO.serviceWith[UserApplication](_.findGroupById(1).runCount)
    } yield leave.contains(true) && group == 0).provideLayer(userApplicationLayer)
    val ret = unsafeRun(stream)

    ret shouldBe true
  }

  "UserApplication" should "leaveOutGroup ok" in {
    val stream = (for {
      _ <- ZIO.serviceWith[UserApplication](_.saveUser(mockUser).runHead)
      _ <- ZIO.serviceWith[UserApplication](_.createGroup(GroupList(1, "g", "", 1)).runHead)
      _ <- ZIO.serviceWith[UserApplication](_.addGroupMember(1, 1).runHead)
      _ <- ZIO.serviceWith[UserApplication](_.addGroupMember(1, 2).runHead)
      leave <- ZIO.serviceWith[UserApplication](_.leaveOutGroup(1, 2).runHead)
      group <- ZIO.serviceWith[UserApplication](_.findGroupById(1).runCount)
    } yield leave.contains(true) && group == 1).provideLayer(userApplicationLayer)
    val ret = unsafeRun(stream)
    ret shouldBe true
  }

  "UserApplication" should "changeGroup ok" in {
    val stream = (for {
      // save时创建了默认的好友分组 gid=1 uid=1
      saveU1 <- ZIO.serviceWith[UserApplication](_.saveUser(mockUser).runHead)
      // 创建第二个分组 gid=2
      saveG1 <- ZIO.serviceWith[UserApplication](_.createFriendGroup("myfgroup1", 1).runHead)
      // gid=3 uid=2
      saveU2 <- ZIO.serviceWith[UserApplication](_.saveUser(mockUser.copy(id = 2, username = "lisi")).runHead)
      addMsg <- ZIO.serviceWith[UserApplication](_.saveAddMessage(AddMessage(1, 2, 1, "", 0)).runHead)
      addF <- ZIO.serviceWith[UserApplication](_.addFriend(1, 1, 2, 3, 1).runHead)
      change <- ZIO.serviceWith[UserApplication](_.changeGroup(2, 2, 1).runHead)
      ret <- ZIO.serviceWith[UserApplication](_.findFriendGroupsById(1).runCollect)
    } yield (saveU1, saveG1, saveU2, addMsg, addF, change, ret.map(_.list.map(_.username)).toList.flatten))
      .provideLayer(userApplicationLayer)
    val ret = unsafeRun(stream)
    ret shouldBe (Some(true), Some(1), Some(true), Some(1), Some(true), Some(true), List("lisi"))
  }

  "UserApplication" should "friend findAddInfo ok" in {
    val stream = (for {
      saveU1 <- ZIO.serviceWith[UserApplication](_.saveUser(mockUser).runHead)
      _ <- ZIO.serviceWith[UserApplication](_.saveUser(mockUser.copy(id = 2, username = "lisi")).runHead)
      addMsg <- ZIO.serviceWith[UserApplication](_.saveAddMessage(AddMessage(1, 2, 1, "", 0)).runHead)
      addInfo <- ZIO.serviceWith[UserApplication](_.findAddInfo(2).runHead)
    } yield (saveU1, addMsg, addInfo.map(a => a.uid -> a.content)))
      .provideLayer(userApplicationLayer)
    val ret = unsafeRun(stream)
    ret shouldBe (Some(true), Some(1), Some((2, "申请添加你为好友")))
  }

  "UserApplication" should "friend findHistoryMessage ok" in {
    val stream = (for {
      // save时创建了默认的好友分组 gid=1 uid=1
      saveU1 <- ZIO.serviceWith[UserApplication](_.saveUser(mockUser).runHead)
      _ <- ZIO.serviceWith[UserApplication](_.saveUser(mockUser.copy(id = 2, username = "lisi")).runHead)
      addMsg <- ZIO.serviceWith[UserApplication](_.saveMessage(mockReceive).runHead)
      msgHistory <- ZIO.serviceWith[UserApplication](_.findHistoryMessage(mockUser, 2, "friend").runHead)
    } yield (saveU1, addMsg, msgHistory.map(a => a.username -> a.content)))
      .provideLayer(userApplicationLayer)
    val ret = unsafeRun(stream)
    ret shouldBe (Some(true), Some(1), Some(("lisi", "receive")))
  }

  "UserApplication" should "group findHistoryMessage ok" in {
    val stream = (for {
      // save时创建了默认的好友分组 gid=1 uid=1
      saveU1 <- ZIO.serviceWith[UserApplication](_.saveUser(mockUser).runHead)
      _ <- ZIO.serviceWith[UserApplication](_.saveUser(mockUser.copy(id = 2, username = "lisi")).runHead)
      addMsg <- ZIO.serviceWith[UserApplication](_.saveMessage(mockReceive.copy(`type` = "group")).runHead)
      msgHistory <- ZIO.serviceWith[UserApplication](_.findHistoryMessage(mockUser, 2, "group").runHead)
    } yield (saveU1, addMsg, msgHistory.map(a => a.username -> a.content)))
      .provideLayer(userApplicationLayer)
    val ret = unsafeRun(stream)
    ret shouldBe (Some(true), Some(1), Some(("lisi", "receive")))
  }

  "UserApplication" should "group countHistoryMessage ok" in {
    val stream = (for {
      // save时创建了默认的好友分组 gid=1 uid=1
      saveU1 <- ZIO.serviceWith[UserApplication](_.saveUser(mockUser).runHead)
      _ <- ZIO.serviceWith[UserApplication](_.saveUser(mockUser.copy(id = 2, username = "lisi")).runHead)
      addMsg <- ZIO.serviceWith[UserApplication](_.saveMessage(mockReceive.copy(`type` = "group")).runHead)
      ret <- ZIO.serviceWith[UserApplication](_.countHistoryMessage(1, 2, "group").runHead)
    } yield (saveU1, addMsg, ret))
      .provideLayer(userApplicationLayer)
    val ret = unsafeRun(stream)
    ret shouldBe (Some(true), Some(1), Some(1))
  }
}
