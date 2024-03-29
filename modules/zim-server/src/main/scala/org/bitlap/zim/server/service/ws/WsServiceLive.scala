/*
 * Copyright 2023 bitlap
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

package org.bitlap.zim.server.service.ws

import java.time.ZonedDateTime

import scala.collection.mutable

import org.bitlap.zim.api.service._
import org.bitlap.zim.domain
import org.bitlap.zim.domain.{Add, SystemConstant}
import org.bitlap.zim.domain.model.{AddMessage, User}
import org.bitlap.zim.domain.ws._
import org.bitlap.zim.domain.ws.protocol.Protocol
import org.bitlap.zim.infrastructure.repository.RStream
import org.bitlap.zim.infrastructure.util.LogUtil
import org.bitlap.zim.server.service.RedisCache

import akka.actor.ActorRef

import io.circe.parser.decode
import io.circe.syntax.EncoderOps

import zio.{Task, ZIO, ZLayer}

/** @author
 *    梦境迷离
 *  @since 2022/3/5
 *  @version 2.0
 */
final case class WsServiceLive(private val userService: UserService[RStream]) {

  def sendMessage(message: domain.Message): Task[Unit] =
    message.synchronized {
      // 看起来有点怪 是否有必要存在？
      // 聊天类型，可能来自朋友或群组
      if (SystemConstant.FRIEND_TYPE == message.to.`type`) {
        friendMessageHandler(userService)(message)
      } else {
        groupMessageHandler(userService)(message)
      }
    }

  def agreeAddGroup(msg: domain.Message): Task[Unit] = {
    val agree = decode[RefuseOrAgreeMessage](msg.msg).getOrElse(null)
    agree.messageBoxId.synchronized {
      agreeAddGroupHandler(userService)(agree)
    }.unless(agree == null).map(_.getOrElse(()))
  }

  def refuseAddGroup(msg: domain.Message): Task[Unit] = {
    val refuse = decode[RefuseOrAgreeMessage](msg.msg).getOrElse(null)
    refuse.messageBoxId.synchronized {
      val actor = WsService.actorRefSessions.get(refuse.toUid)
      for {
        _ <- userService.updateAgree(refuse.messageBoxId, 2).runHead
        r <- {
          val result = Map("type" -> "refuseAddGroup", "username" -> refuse.mine.username)
          sendMessage(result.asJson.noSpaces, actor)
        }.unless(actor == null)
      } yield r
    }.unless(refuse == null).map(_.getOrElse(()))
  }

  def refuseAddFriend(messageBoxId: Int, username: String, to: Int): Task[Boolean] =
    messageBoxId.synchronized {
      refuseAddFriendHandler(userService)(messageBoxId, username, to)
    }

  def deleteGroup(master: User, groupname: String, gid: Int, uid: Int): Task[Unit] =
    gid.synchronized {
      val actor: ActorRef = WsService.actorRefSessions.get(uid);
      {
        val result = Map(
          "type"      -> "deleteGroup",
          "username"  -> master.username,
          "uid"       -> s"${master.id}",
          "groupname" -> groupname,
          "gid"       -> s"$gid"
        )
        sendMessage(result.asJson.noSpaces, actor)
      }.when(actor != null && uid != master.id).map(_.getOrElse(()))
    }

  def removeFriend(uId: Int, friendId: Int): Task[Unit] =
    uId.synchronized {
      // 对方是否在线，在线则处理，不在线则不处理
      val actor = WsService.actorRefSessions.get(friendId)
      userService
        .findUserById(uId)
        .runHead
        .flatMap { u =>
          {
            val result = Map(
              "type"     -> Protocol.delFriend.stringify,
              "uId"      -> s"$uId",
              "username" -> u.map(_.username).getOrElse("undefined")
            )
            sendMessage(result.asJson.noSpaces, actor)

          }.when(actor != null && u.isDefined)
        }
        .map(_.getOrElse(()))
    }

  def addGroup(uId: Int, message: domain.Message): Task[Unit] =
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
            "type" -> Protocol.addGroup.stringify
          )
          sendMessage(result.asJson.noSpaces, actorRef)
        }.when(actorRef != null)
      }.map(_.getOrElse(()))
    }

  def addFriend(uId: Int, message: domain.Message): Task[Unit] =
    uId.synchronized {
      val mine     = message.mine
      val actorRef = WsService.actorRefSessions.get(message.to.id)
      val add      = decode[Add](message.msg).getOrElse(null);
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
          Map("type" -> Protocol.addFriend.stringify).asJson.noSpaces,
          actorRef = actorRef
        ).when(actorRef != null).map(_.getOrElse(()))
    }

  def countUnHandMessage(uId: Int): Task[Map[String, String]] =
    userService.countUnHandMessage(uId, Some(0)).runHead.map { count =>
      Map(
        "type"  -> Protocol.unHandMessage.stringify,
        "count" -> s"${count.getOrElse(0)}"
      )
    }

  def checkOnline(message: domain.Message): Task[Map[String, String]] = {
    val result = mutable.HashMap[String, String]()
    result.put("type", Protocol.checkOnline.stringify)
    RedisCache.getSets(SystemConstant.ONLINE_USER).map { uids =>
      if (uids.contains(message.to.id.toString))
        result.put("status", SystemConstant.status.ONLINE_DESC)
      else result.put("status", SystemConstant.status.HIDE_DESC)
      result.toMap
    }
  }

  def sendMessage(message: String, actorRef: ActorRef): Task[Unit] =
    LogUtil
      .info(s"sendMessage message=>$message actorRef=>${actorRef.path}")
      .as(actorRef ! message)
      .when(actorRef != null)
      .map(_.getOrElse(()))

  def changeOnline(uId: Int, status: String): Task[Boolean] =
    changeOnlineHandler(userService)(uId, status)

  def readOfflineMessage(message: domain.Message): Task[Unit] =
    readOfflineMessageHandler(userService)(message)

  def getConnections: Task[Int] = ZIO.attempt(WsService.actorRefSessions.size())

}

object WsServiceLive {

  lazy val live: ZLayer[UserService[RStream], Nothing, WsServiceLive] =
    ZLayer.fromFunction(WsServiceLive.apply(_))
}
