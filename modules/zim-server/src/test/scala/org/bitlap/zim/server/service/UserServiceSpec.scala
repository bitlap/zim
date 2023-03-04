/*
 * Copyright 2022 bitlap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bitlap.zim.server.service

import org.bitlap.zim.api.service.UserService
import org.bitlap.zim.domain.model
import org.bitlap.zim.domain.model._
import org.bitlap.zim.infrastructure.repository.RStream
import zio.ZIO

/** @author
 *    梦境迷离
 *  @since 2022/1/13
 *  @version 1.0
 */
final class UserServiceSpec extends TestService {

  "UserService" should "saveUser ok" in {
    val stream = (for {
      _    <- ZIO.serviceWithZIO[UserService[RStream]](_.saveUser(mockUser).runHead)
      user <- ZIO.serviceWithZIO[UserService[RStream]](_.findUserById(mockUser.id).runHead)
    } yield user).provideLayer(userServiceLayer)
    val ret = unsafeRun(stream)
    ret.map(_.username) shouldBe Some(mockUser.username)
  }

  "UserService" should "matchUser ok" in {
    val stream = (for {
      _    <- ZIO.serviceWithZIO[UserService[RStream]](_.saveUser(mockUser).runHead)
      user <- ZIO.serviceWithZIO[UserService[RStream]](_.matchUser(mockUser).runHead)
    } yield user).provideLayer(userServiceLayer)
    val ret = unsafeRun(stream)
    ret.map(_.password) shouldBe Some("jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=")
  }

  "UserService" should "creator leaveOutGroup ok" in {
    val stream = (for {
      _     <- ZIO.serviceWithZIO[UserService[RStream]](_.saveUser(mockUser).runHead)
      _     <- ZIO.serviceWithZIO[UserService[RStream]](_.createGroup(GroupList(1, "g", "", 1)).runHead)
      _     <- ZIO.serviceWithZIO[UserService[RStream]](_.addGroupMember(1, 1).runHead)
      leave <- ZIO.serviceWithZIO[UserService[RStream]](_.leaveOutGroup(1, 1).runHead)
      group <- ZIO.serviceWithZIO[UserService[RStream]](_.findGroupById(1).runCount)
    } yield leave.contains(true) && group == 0).provideLayer(userServiceLayer)
    val ret = unsafeRun(stream)

    ret shouldBe true
  }

  "UserService" should "leaveOutGroup ok" in {
    val stream = (for {
      _     <- ZIO.serviceWithZIO[UserService[RStream]](_.saveUser(mockUser).runHead)
      _     <- ZIO.serviceWithZIO[UserService[RStream]](_.createGroup(GroupList(1, "g", "", 1)).runHead)
      _     <- ZIO.serviceWithZIO[UserService[RStream]](_.addGroupMember(1, 1).runHead)
      _     <- ZIO.serviceWithZIO[UserService[RStream]](_.addGroupMember(1, 2).runHead)
      leave <- ZIO.serviceWithZIO[UserService[RStream]](_.leaveOutGroup(1, 2).runHead)
      group <- ZIO.serviceWithZIO[UserService[RStream]](_.findGroupById(1).runCount)
    } yield leave.contains(true) && group == 1).provideLayer(userServiceLayer)
    val ret = unsafeRun(stream)
    ret shouldBe true
  }

  "UserService" should "changeGroup ok" in {
    val stream = (for {
      // save时创建了默认的好友分组 gid=1 uid=1
      saveU1 <- ZIO.serviceWithZIO[UserService[RStream]](_.saveUser(mockUser).runHead)
      // 创建第二个分组 gid=2
      saveG1 <- ZIO.serviceWithZIO[UserService[RStream]](_.createFriendGroup("myfgroup1", 1).runHead)
      // gid=3 uid=2
      saveU2 <- ZIO.serviceWithZIO[UserService[RStream]](_.saveUser(mockUser.copy(id = 2, username = "lisi")).runHead)
      addMsg <- ZIO.serviceWithZIO[UserService[RStream]](_.saveAddMessage(model.AddMessage(1, 2, 1, "", 0)).runHead)
      addF   <- ZIO.serviceWithZIO[UserService[RStream]](_.addFriend(1, 1, 2, 3, 1).runHead)
      change <- ZIO.serviceWithZIO[UserService[RStream]](_.changeGroup(2, 2, 1).runHead)
      ret    <- ZIO.serviceWithZIO[UserService[RStream]](_.findFriendGroupsById(1).runCollect)
    } yield (saveU1, saveG1, saveU2, addMsg, addF, change, ret.map(_.list.map(_.username)).toList.flatten))
      .provideLayer(userServiceLayer)
    val ret = unsafeRun(stream)
    ret shouldBe (Some(true), Some(1), Some(true), Some(1), Some(true), Some(true), List("lisi"))
  }

  "UserService" should "friend findAddInfo ok" in {
    val stream = (for {
      saveU1  <- ZIO.serviceWithZIO[UserService[RStream]](_.saveUser(mockUser).runHead)
      _       <- ZIO.serviceWithZIO[UserService[RStream]](_.saveUser(mockUser.copy(id = 2, username = "lisi")).runHead)
      addMsg  <- ZIO.serviceWithZIO[UserService[RStream]](_.saveAddMessage(model.AddMessage(1, 2, 1, "", 0)).runHead)
      addInfo <- ZIO.serviceWithZIO[UserService[RStream]](_.findAddInfo(2).runHead)
    } yield (saveU1, addMsg, addInfo.map(a => a.uid -> a.content)))
      .provideLayer(userServiceLayer)
    val ret = unsafeRun(stream)
    ret shouldBe (Some(true), Some(1), Some((2, "申请添加你为好友")))
  }

  "UserService" should "friend findHistoryMessage ok" in {
    val stream = (for {
      // save时创建了默认的好友分组 gid=1 uid=1
      saveU1 <- ZIO.serviceWithZIO[UserService[RStream]](_.saveUser(mockUser).runHead)
      _      <- ZIO.serviceWithZIO[UserService[RStream]](_.saveUser(mockUser.copy(id = 2, username = "lisi")).runHead)
      addMsg <- ZIO.serviceWithZIO[UserService[RStream]](_.saveMessage(mockReceive).runHead)
      msgHistory <- ZIO.serviceWithZIO[UserService[RStream]](_.findHistoryMessage(mockUser, 2, "friend").runHead)
    } yield (saveU1, addMsg, msgHistory.map(a => a.username -> a.content)))
      .provideLayer(userServiceLayer)
    val ret = unsafeRun(stream)
    ret shouldBe (Some(true), Some(1), Some(("lisi", "receive")))
  }

  "UserService" should "group findHistoryMessage ok" in {
    val stream = (for {
      // save时创建了默认的好友分组 gid=1 uid=1
      saveU1 <- ZIO.serviceWithZIO[UserService[RStream]](_.saveUser(mockUser).runHead)
      _      <- ZIO.serviceWithZIO[UserService[RStream]](_.saveUser(mockUser.copy(id = 2, username = "lisi")).runHead)
      addMsg <- ZIO.serviceWithZIO[UserService[RStream]](_.saveMessage(mockReceive.copy(`type` = "group")).runHead)
      msgHistory <- ZIO.serviceWithZIO[UserService[RStream]](_.findHistoryMessage(mockUser, 2, "group").runHead)
    } yield (saveU1, addMsg, msgHistory.map(a => a.username -> a.content)))
      .provideLayer(userServiceLayer)
    val ret = unsafeRun(stream)
    ret shouldBe (Some(true), Some(1), Some(("lisi", "receive")))
  }

  "UserService" should "group countHistoryMessage ok" in {
    val stream = (for {
      // save时创建了默认的好友分组 gid=1 uid=1
      saveU1 <- ZIO.serviceWithZIO[UserService[RStream]](_.saveUser(mockUser).runHead)
      _      <- ZIO.serviceWithZIO[UserService[RStream]](_.saveUser(mockUser.copy(id = 2, username = "lisi")).runHead)
      addMsg <- ZIO.serviceWithZIO[UserService[RStream]](_.saveMessage(mockReceive.copy(`type` = "group")).runHead)
      ret    <- ZIO.serviceWithZIO[UserService[RStream]](_.countHistoryMessage(1, 2, "group").runHead)
    } yield (saveU1, addMsg, ret))
      .provideLayer(userServiceLayer)
    val ret = unsafeRun(stream)
    ret shouldBe (Some(true), Some(1), Some(1))
  }

  "UserService" should "findById ok" in {
    val stream = (for {
      _   <- ZIO.serviceWithZIO[UserService[RStream]](_.saveUser(mockUser).runHead)
      ret <- ZIO.serviceWithZIO[UserService[RStream]](_.findUserById(1).runHead)
    } yield ret)
      .provideLayer(userServiceLayer)
    val ret = unsafeRun(stream)
    ret.map(_.username) shouldBe Some(mockUser.username)
  }

  "UserService" should "removeFriend and findAddInfo ok" in {
    val stream = (for {
      _    <- ZIO.serviceWithZIO[UserService[RStream]](_.saveUser(mockUser).runHead)
      _    <- ZIO.serviceWithZIO[UserService[RStream]](_.saveUser(mockUser.copy(username = "lisi")).runHead)
      _    <- ZIO.serviceWithZIO[UserService[RStream]](_.createGroup(GroupList(0, "gn1", "", 1)).runHead)
      _    <- ZIO.serviceWithZIO[UserService[RStream]](_.createGroup(GroupList(0, "gn2", "", 2)).runHead)
      _    <- ZIO.serviceWithZIO[UserService[RStream]](_.saveAddMessage(mockAddMessage).runHead)
      _    <- ZIO.serviceWithZIO[UserService[RStream]](_.addFriend(1, 1, 2, 2, 1).runHead)
      ret1 <- ZIO.serviceWithZIO[UserService[RStream]](_.findAddInfo(2).runHead)
      ret2 <- ZIO.serviceWithZIO[UserService[RStream]](_.removeFriend(2, 1).runHead)
    } yield ret1.map(_.uid) -> ret2)
      .provideLayer(userServiceLayer)
    val ret = unsafeRun(stream)
    ret shouldBe Some(2) -> Some(true)
  }

  "UserService" should "updateUserInfo, sign, avatar, should success" in {
    val saveUser = ZIO
      .serviceWithZIO[UserService[RStream]](_.saveUser(mockUser.copy(username = "world")).runHead)
      .provideLayer(userServiceLayer)
    val user = unsafeRun(saveUser)

    println("saveUser:" + user)

    val stream1 = (for {
      findU <- ZIO.serviceWithZIO[UserService[RStream]](_.findUserById(1).runHead)
      uid = findU.map(_.id).getOrElse(1)
      _    <- ZIO.serviceWithZIO[UserService[RStream]](_.updateUserInfo(mockUser.copy(id = uid, sign = "梦境迷离")).runHead)
      user <- ZIO.serviceWithZIO[UserService[RStream]](_.findUserById(uid).runHead)
    } yield user.map(_.sign)).provideLayer(userServiceLayer)

    val stream2 = (for {
      findU <- ZIO.serviceWithZIO[UserService[RStream]](_.findUserById(1).runHead)
      uid = findU.map(_.id).getOrElse(1)
      _    <- ZIO.serviceWithZIO[UserService[RStream]](_.updateUserStatus("hide", uid).runHead)
      user <- ZIO.serviceWithZIO[UserService[RStream]](_.findUserById(uid).runHead)
    } yield user.map(_.status)).provideLayer(userServiceLayer)

    val stream3 = (for {
      findU <- ZIO.serviceWithZIO[UserService[RStream]](_.findUserById(1).runHead)
      uid = findU.map(_.id).getOrElse(1)
      _    <- ZIO.serviceWithZIO[UserService[RStream]](_.updateAvatar(uid, "sss").runHead)
      user <- ZIO.serviceWithZIO[UserService[RStream]](_.findUserById(uid).runHead)
    } yield user.map(_.avatar)).provideLayer(userServiceLayer)

    val stream = for {
      ret1 <- stream1
      ret2 <- stream2
      ret3 <- stream3
    } yield Tuple3(ret1, ret2, ret3)

    val Tuple3(ret1, ret2, ret3) = unsafeRun(stream)
    ret1 shouldBe Some("梦境迷离")
    ret2 shouldBe Some("hide")
    ret3 shouldBe Some("sss")

  }

}
