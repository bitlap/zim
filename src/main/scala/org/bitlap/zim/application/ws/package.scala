package org.bitlap.zim.application
import io.circe.syntax.EncoderOps
import org.bitlap.zim.application.ws.wsService.WsService.actorRefSessions
import org.bitlap.zim.domain.Message
import org.bitlap.zim.domain.model.Receive
import org.bitlap.zim.util.DateUtil
import zio.ZIO

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
  private[ws] def buildGroupMessage(
    userService: UserApplication
  )(message: Message, receive: Receive, gid: Int): ZIO[Any, Throwable, Unit] = {
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
}
