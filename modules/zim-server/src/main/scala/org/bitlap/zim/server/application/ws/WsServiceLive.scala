/*
 * Copyright 2021 bitlap
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

package org.bitlap.zim.server.application.ws

import akka.actor.ActorRef
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import org.bitlap.zim.cache.ZioRedisService
import org.bitlap.zim.domain
import org.bitlap.zim.domain.model.{ AddMessage, User }
import org.bitlap.zim.domain.ws.protocol._
import org.bitlap.zim.domain.{ Add, SystemConstant }
import org.bitlap.zim.server.application.UserApplication
import org.bitlap.zim.server.configuration.ApplicationConfiguration
import org.bitlap.zim.server.util.LogUtil
import zio.{ Task, ZIO }

import java.time.ZonedDateTime
import scala.collection.mutable

/**
 * @author 梦境迷离
 * @since 2022/3/5
 * @version 2.0
 */
case class WsServiceLive(private val app: ApplicationConfiguration) extends WsService {

  private val userService: UserApplication = app.userApplication

  override def sendMessage(message: domain.Message): Task[Unit] =
    message.synchronized {
      //看起来有点怪 是否有必要存在？
      //聊天类型，可能来自朋友或群组
      if (SystemConstant.FRIEND_TYPE == message.to.`type`) {
        friendMessageHandler(userService)(message)
      } else {
        groupMessageHandler(userService)(message)
      }
    }

  override def agreeAddGroup(msg: domain.Message): Task[Unit] = {
    val agree = decode[AddRefuseMessage](msg.msg).getOrElse(null)
    agree.messageBoxId.synchronized {
      agreeAddGroupHandler(userService)(agree)
    }.unless(agree == null)
  }

  override def refuseAddGroup(msg: domain.Message): Task[Unit] = {
    val refuse = decode[AddRefuseMessage](msg.msg).getOrElse(null)
    refuse.messageBoxId.synchronized {
      val actor = WsService.actorRefSessions.get(refuse.toUid)
      for {
        _ <- userService.updateAgree(refuse.messageBoxId, 2).runHead
        r <- {
          val result = Map("type" -> "refuseAddGroup", "username" -> refuse.mine.username)
          sendMessage(result.asJson.noSpaces, actor)
        }.unless(actor == null)
      } yield r
    }.unless(refuse == null)
  }

  override def refuseAddFriend(messageBoxId: Int, username: String, to: Int): Task[Boolean] =
    messageBoxId.synchronized {
      refuseAddFriendHandler(userService)(messageBoxId, username, to)
    }

  override def deleteGroup(master: User, groupname: String, gid: Int, uid: Int): Task[Unit] =
    gid.synchronized {
      val actor: ActorRef = WsService.actorRefSessions.get(uid);
      {
        val result = Map(
          "type" -> "deleteGroup",
          "username" -> master.username,
          "uid" -> s"${master.id}",
          "groupname" -> groupname,
          "gid" -> s"$gid"
        )
        sendMessage(result.asJson.noSpaces, actor)
      }.when(actor != null && uid != master.id)
    }

  override def removeFriend(uId: Int, friendId: Int): Task[Unit] =
    uId.synchronized {
      //对方是否在线，在线则处理，不在线则不处理
      val actor = WsService.actorRefSessions.get(friendId)
      app.userApplication.findUserById(uId).runHead.flatMap { u =>
        {
          val result = Map(
            "type" -> protocol.delFriend.stringify,
            "uId" -> s"$uId",
            "username" -> u.map(_.username).getOrElse("undefined")
          )
          sendMessage(result.asJson.noSpaces, actor)

        }.when(actor != null && u.isDefined)
      }
    }

  override def addGroup(uId: Int, message: domain.Message): Task[Unit] =
    uId.synchronized {
      val t = decode[Group](message.msg).getOrElse(null);
      {
        userService
          .saveAddMessage(
            AddMessage(
              fromUid = message.mine.id,
              toUid = message.to.id,
              groupId = t.groupId,
              remark = t.remark,
              `type` = 1,
              time = ZonedDateTime.now()
            )
          )
          .runHead
          .when(t != null)
      } *> {
        val actorRef = WsService.actorRefSessions.get(message.to.id);
        {
          val result = Map(
            "type" -> protocol.addGroup.stringify
          )
          sendMessage(result.asJson.noSpaces, actorRef)
        }.when(actorRef != null)
      }
    }

  override def addFriend(uId: Int, message: domain.Message): Task[Unit] =
    uId.synchronized {
      val mine = message.mine
      val actorRef = WsService.actorRefSessions.get(message.to.id)
      val add = decode[Add](message.msg).getOrElse(null);
      {
        val addMessageCopy = AddMessage(
          fromUid = mine.id,
          toUid = message.to.id,
          groupId = add.groupId,
          remark = add.remark,
          `type` = add.`type`,
          time = ZonedDateTime.now()
        )
        userService.saveAddMessage(addMessageCopy).runHead
      }.when(add != null) *>
        sendMessage(
          Map("type" -> protocol.addFriend.stringify).asJson.noSpaces,
          actorRef = actorRef
        ).when(actorRef != null)
    }

  override def countUnHandMessage(uId: Int): Task[Map[String, String]] =
    uId.synchronized {
      userService.countUnHandMessage(uId, Some(0)).runHead.map { count =>
        Map(
          "type" -> protocol.unHandMessage.stringify,
          "count" -> s"${count.getOrElse(0)}"
        )
      }
    }

  override def checkOnline(message: domain.Message): Task[Map[String, String]] =
    message.to.id.synchronized {
      val result = mutable.HashMap[String, String]()
      result.put("type", protocol.checkOnline.stringify)
      ZioRedisService.getSets(SystemConstant.ONLINE_USER).map { uids =>
        if (uids.contains(message.to.id.toString))
          result.put("status", SystemConstant.status.ONLINE_DESC)
        else result.put("status", SystemConstant.status.HIDE_DESC)
        result.toMap
      }
    }

  override def sendMessage(message: String, actorRef: ActorRef): Task[Unit] =
    this.synchronized {

      LogUtil
        .info(s"sendMessage message=>$message actorRef=>${actorRef.path}")
        .as(actorRef ! message)
        .when(actorRef != null)
    }

  override def changeOnline(uId: Int, status: String): Task[Boolean] =
    uId.synchronized {
      changeOnlineHandler(userService)(uId, status)
    }

  override def readOfflineMessage(message: domain.Message): Task[Unit] =
    message.mine.id.synchronized {
      readOfflineMessageHandler(userService)(message)
    }

  override def getConnections: Task[Int] = ZIO.effect(WsService.actorRefSessions.size())

}
