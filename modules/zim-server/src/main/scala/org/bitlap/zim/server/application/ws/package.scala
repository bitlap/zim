package org.bitlap.zim.server.application

import io.circe.syntax.EncoderOps
import org.bitlap.zim.domain.model.{ Receive, User }
import org.bitlap.zim.domain.ws.protocol.{ protocol, AddRefuseMessage }
import org.bitlap.zim.domain.{ Message, SystemConstant }
import org.bitlap.zim.server.application.ws.wsService.WsService.actorRefSessions
import org.bitlap.zim.server.util.DateUtil
import zio.{ IO, ZIO }
import zio.actors.{ ActorRef => _ }

/**
 * @author 梦境迷离
 * @since 2022/1/12
 * @version 1.0
 */
package object ws {

  private[ws] final val DEFAULT_VALUE: Unit = ()

  /**
   * 封装返回消息格式
   */
  private[ws] def getReceive(message: Message): Receive = {
    val mine = message.mine
    val to = message.to
    Receive(
      id = mine.id,
      username = mine.username,
      avatar = mine.avatar,
      `type` = to.`type`,
      content = mine.content,
      cid = 0,
      mine = false,
      fromid = mine.id,
      timestamp = DateUtil.getLongDateTime,
      status = 0,
      toid = to.id
    )
  }

  private[ws] def friendMessageHandler(userService: UserApplication)(message: Message): IO[Throwable, Unit] = {
    val gid = message.to.id
    val receive = getReceive(message)
    userService.findUserById(gid).runHead.flatMap { us =>
      {
        val msg = if (actorRefSessions.containsKey(gid)) {
          val actorRef = actorRefSessions.get(gid)
          val tmpReceiveArchive = receive.copy(status = 1)
          wsService.sendMessage(tmpReceiveArchive.asJson.noSpaces, actorRef)
          tmpReceiveArchive
        } else receive
        // 由于都返回了stream，使用时都转成非stream
        userService.saveMessage(msg).runHead.as(DEFAULT_VALUE)
      }.unless(us.isEmpty)
    }
  }

  private[ws] def groupMessageHandler(userService: UserApplication)(message: Message): IO[Throwable, Unit] = {
    val gid = message.to.id
    val receive = getReceive(message)
    var receiveArchive: Receive = receive.copy(id = gid)
    val sending = userService.findGroupById(gid).runHead.flatMap { group =>
      userService
        .findUserByGroupId(gid)
        .filter(_.id != message.mine.id)
        .foreach { user =>
          {
            //是否在线
            val actorRef = actorRefSessions.get(user.id)
            receiveArchive = receiveArchive.copy(status = 1)
            wsService.sendMessage(receiveArchive.asJson.noSpaces, actorRef)
          }.when(actorRefSessions.containsKey(user.id))
        }
        .unless(group.isEmpty)
    }

    sending *> userService.saveMessage(receiveArchive).runHead.as(DEFAULT_VALUE)
  }

  private[ws] def agreeAddGroupHandler(
    userService: UserApplication
  )(agree: AddRefuseMessage): IO[Throwable, Unit] =
    userService.addGroupMember(agree.groupId, agree.toUid, agree.messageBoxId).runHead.map { f =>
      if (!f.fold(false)(t => t)) {
        ZIO.effect(DEFAULT_VALUE)
      } else {
        userService.findGroupById(agree.groupId).runHead.flatMap { groupList =>
          // 通知加群成功
          val actor = actorRefSessions.get(agree.toUid);
          {
            val message = Message(
              `type` = protocol.agreeAddGroup.stringify,
              mine = agree.mine,
              to = null,
              msg = groupList.fold("")(g => g.asJson.noSpaces)
            )
            wsService.sendMessage(message.asJson.noSpaces, actor)
          }
            .when(groupList.isDefined && actor != null)
        }
      }
    }

  private[ws] def refuseAddFriendHandler(
    userService: UserApplication
  )(messageBoxId: Int, user: User, to: Int): IO[Throwable, Boolean] =
    userService.updateAddMessage(messageBoxId, 2).runHead.flatMap { r =>
      r.fold(ZIO.effect(false)) { ret =>
        val actor = actorRefSessions.get(to)
        if (actor != null) {
          val result = Map("type" -> "refuseAddFriend", "username" -> user.username)
          wsService.sendMessage(result.asJson.noSpaces, actor).as(ret)
        } else ZIO.effect(ret)
      }
    }

  private[ws] def readOfflineMessageHandler(
    userService: UserApplication
  )(message: Message): IO[Throwable, Unit] =
    userService
      .findOffLineMessage(message.mine.id, 0)
      .runCount
      .map { c =>
        {
          if (message.to.`type` == SystemConstant.GROUP_TYPE) {
            // 我所有的群中有未读的消息吗
            userService.readGroupMessage(message.mine.id, message.mine.id).runHead
          } else {
            userService.readFriendMessage(message.mine.id, message.to.id).runHead
          }
        } when (c > 0)
      } map (_ => DEFAULT_VALUE)
}
