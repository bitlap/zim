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

import java.time.ZonedDateTime

import io.circe.syntax.EncoderOps
import org.bitlap.zim.api.service.UserService
import org.bitlap.zim.domain._
import org.bitlap.zim.domain.model.Receive
import org.bitlap.zim.domain.ws.RefuseOrAgreeMessage
import org.bitlap.zim.domain.ws.protocol.Protocol
import org.bitlap.zim.infrastructure.repository.RStream
import zio._
import zio.stream.ZStream

/** @author
 *    梦境迷离
 *  @since 2022/1/12
 *  @version 1.0
 */
package object ws {

  /** 封装返回消息格式
   */
  private[ws] def getReceive(message: Message): Receive = {
    val mine = message.mine
    val to   = message.to
    Receive(
      mid = mine.id,
      username = mine.username,
      avatar = mine.avatar,
      `type` = to.`type`,
      content = mine.content,
      cid = 0,
      mine = false,
      fromid = mine.id,
      timestamp = ZonedDateTime.now().toInstant.toEpochMilli,
      status = 0,
      toid = to.id
    )
  }

  private[ws] def friendMessageHandler(userService: UserService[RStream])(message: Message): IO[Throwable, Unit] = {
    val uid     = message.to.id
    val receive = getReceive(message)
    userService
      .findUserById(uid)
      .runHead
      .flatMap { us =>
        {
          val msg = if (WsService.actorRefSessions.containsKey(uid)) {
            val actorRef          = WsService.actorRefSessions.get(uid)
            val tmpReceiveArchive = receive.copy(status = 1)
            WsService.sendMessage(tmpReceiveArchive.asJson.noSpaces, actorRef) *> ZIO.succeed(tmpReceiveArchive)
          } else ZIO.succeed(receive)
          // 由于都返回了stream，使用时都转成非stream
          msg.flatMap(m => userService.saveMessage(m).runHead.unit)
        }.unless(us.isEmpty)
      }
      .map(_.getOrElse(()))

  }

  private[ws] def groupMessageHandler(userService: UserService[RStream])(message: Message): IO[Throwable, Unit] = {
    val gid                     = message.to.id
    val receive                 = getReceive(message)
    val receiveArchive: Receive = receive.copy(mid = gid)
    userService
      .findGroupById(gid)
      .runHead
      .flatMap { group =>
        userService
          .findUserByGroupId(gid)
          .filter(_.id != message.mine.id)
          .foreach { user =>
            {
              // 是否在线
              val actorRef = WsService.actorRefSessions.get(user.id)
              WsService.sendMessage(receiveArchive.copy(status = 1).asJson.noSpaces, actorRef) *>
                userService
                  .saveMessage(if (receiveArchive != null) receiveArchive.copy(status = 1) else receiveArchive)
                  .runHead
                  .unit
            }.when(WsService.actorRefSessions.containsKey(user.id))
          }
          .unless(group.isEmpty)
      }
      .map(_.getOrElse(()))

  }

  private[ws] def agreeAddGroupHandler(
    userService: UserService[RStream]
  )(agree: RefuseOrAgreeMessage): IO[Throwable, Unit] =
    userService.addGroupMember(agree.groupId, agree.toUid, agree.messageBoxId).runHead.map { f =>
      userService
        .findGroupById(agree.groupId)
        .runHead
        .flatMap { groupList =>
          // 通知加群成功
          val actor = WsService.actorRefSessions.get(agree.toUid);
          {
            val message = Message(
              `type` = Protocol.agreeAddGroup.stringify,
              mine = agree.mine,
              to = null,
              msg = groupList.fold("")(g => g.asJson.noSpaces)
            )
            WsService.sendMessage(message.asJson.noSpaces, actor)
          }
            .when(groupList.isDefined && actor != null)
        }
        .unless(!f.getOrElse(false))
    }

  private[ws] def refuseAddFriendHandler(
    userService: UserService[RStream]
  )(messageBoxId: Int, username: String, to: Int): IO[Throwable, Boolean] =
    userService.updateAgree(messageBoxId, 2).runHead.flatMap { r =>
      r.fold(ZIO.attempt(false)) { ret =>
        val actor = WsService.actorRefSessions.get(to)
        if (actor != null) {
          val result = Map("type" -> "refuseAddFriend", "username" -> username)
          WsService.sendMessage(result.asJson.noSpaces, actor).as(ret)
        } else ZIO.attempt(ret)
      }
    }

  private[ws] def readOfflineMessageHandler(
    userService: UserService[RStream]
  )(message: Message): IO[Throwable, Unit] =
    userService
      .findOffLineMessage(message.mine.id, 0)
      .runCount
      .flatMap { c =>
        {
          if (message.to.`type` == SystemConstant.GROUP_TYPE) {
            // 我所有的群中有未读的消息吗
            userService.readGroupMessage(message.mine.id, message.mine.id).runHead
          } else {
            userService.readFriendMessage(message.mine.id, message.to.id).runHead
          }
        } when (c > 0)
      }
      .unit

  private[ws] def changeOnlineHandler(
    userService: UserService[RStream]
  )(uId: Int, status: String): IO[Throwable, Boolean] = {
    val isOnline = SystemConstant.status.ONLINE.equals(status)
    val beforeChange =
      if (isOnline) RedisCache.setSet(SystemConstant.ONLINE_USER, s"$uId")
      else RedisCache.removeSetValue(SystemConstant.ONLINE_USER, s"$uId")
    // 向我的所有在线好友发送广播消息，告知我的状态变更，否则只能再次打聊天开窗口时变更,todo 异步发送
    beforeChange *> {
      val ret = for {
        fs    <- userService.findFriendGroupsById(uId)
        users <- ZStream.fromZIO(RedisCache.getSets(SystemConstant.ONLINE_USER))
        u     <- ZStream.fromIterable(fs.list)
        notify <- {
          val fu       = users.contains(u.id.toString)
          val actorRef = WsService.actorRefSessions.get(u.id);
          {
            val msg = Map(
              "id"     -> s"$uId", // 对好友而言，好友的好友就是我
              "type"   -> Protocol.checkOnline.stringify,
              "status" -> (if (isOnline) SystemConstant.status.ONLINE_DESC else SystemConstant.status.HIDE_DESC)
            )
            ZStream.fromZIO(WsService.sendMessage(msg.asJson.noSpaces, actorRef))
          }.when(fu && actorRef != null)
        }
      } yield notify

      ret.runCollect
    } *> userService.updateUserStatus(status, uId).runHead.map(_.getOrElse(false))
  }
}
