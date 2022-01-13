package org.bitlap.zim.application.ws;

import akka.actor.ActorRef
import org.bitlap.zim.actor.protocol.AddRefuseMessage
import org.bitlap.zim.configuration.ApplicationConfiguration.ZApplicationConfiguration
import org.bitlap.zim.configuration.{ SystemConstant, ZimServiceConfiguration }
import org.bitlap.zim.domain._
import org.bitlap.zim.domain.model._
import zio.{ Has, Task, ZIO, ZLayer }
import io.circe.parser.decode

import java.util.concurrent.ConcurrentHashMap

/**
 * @author 梦境迷离
 * @version 1.0, 2022/1/11
 */
object wsService extends ZimServiceConfiguration {

  type ZWsService = Has[WsService.Service]

  object WsService extends ZimServiceConfiguration {
    trait Service {

      def sendMessage(message: Message): Task[Unit]

      def agreeAddGroup(msg: Message): Task[Unit]

      def refuseAddGroup(msg: Message): Task[Unit]

      def refuseAddFriend(messageBoxId: Int, user: User, to: Int): Task[Boolean]

      def deleteGroup(master: User, groupname: String, gid: Int, uid: Int): Task[Unit]

      def removeFriend(uId: Int, friendId: Int): Task[Unit]

      def addGroup(uId: Int, message: Message): Task[Unit]

      def addFriend(uId: Int, message: Message): Task[Unit]

      def countUnHandMessage(uId: Int): Task[Map[String, String]]

      def checkOnline(message: Message): Task[Map[String, String]]

      def sendMessage(message: String, actorRef: ActorRef): Task[Unit]

      def changeOnline(uId: Int, status: String): Task[Boolean]

      def readOfflineMessage(message: Message): Task[Unit]

      def getConnections: Task[Int]
    }

    final lazy val actorRefSessions: ConcurrentHashMap[Integer, ActorRef] = new ConcurrentHashMap[Integer, ActorRef]

    lazy val live: ZLayer[ZApplicationConfiguration, Nothing, ZWsService] =
      ZLayer.fromService { env =>
        val userService = env.userApplication

        new Service {

          override def sendMessage(message: Message): Task[Unit] =
            message.synchronized { //看起来有点怪 是否有必要存在？
              //聊天类型，可能来自朋友或群组
              if (SystemConstant.FRIEND_TYPE == message.to.`type`) {
                friendMessageHandler(userService)(message)
              } else {
                groupMessageHandler(userService)(message)
              }
            }

          override def agreeAddGroup(msg: Message): Task[Unit] = {
            val agree = decode[AddRefuseMessage](msg.msg).getOrElse(null)
            if (agree == null) ZIO.effect(DEFAULT_VALUE)
            else
              agree.messageBoxId.synchronized {
                agreeAddGroupHandler(userService)(agree)
              }
          }

          override def refuseAddGroup(msg: Message): Task[Unit] = ???

          override def refuseAddFriend(messageBoxId: Int, user: User, to: Int): Task[Boolean] =
            messageBoxId.synchronized {
              refuseAddFriendHandler(userService)(messageBoxId, user, to)
            }

          override def deleteGroup(master: User, groupname: String, gid: Int, uid: Int): Task[Unit] =
            ???

          override def removeFriend(uId: Int, friendId: Int): Task[Unit] = ???

          override def addGroup(uId: Int, message: Message): Task[Unit] = ???

          override def addFriend(uId: Int, message: Message): Task[Unit] = ???

          override def countUnHandMessage(uId: Int): Task[Map[String, String]] = ???

          override def checkOnline(message: Message): Task[Map[String, String]] = ???

          override def sendMessage(message: String, actorRef: ActorRef): Task[Unit] = ???

          override def changeOnline(uId: Int, status: String): Task[Boolean] = ???

          override def readOfflineMessage(message: Message): Task[Unit] =
            message.mine.id.synchronized {
              readOfflineMessageHandler(userService)(message)
            }

          override def getConnections: Task[Int] = ZIO.succeed(actorRefSessions.size())
        }
      }

  }

  // 非最佳实践，为了使用unsafeRun，不能把environment传递到最外层，这里直接provideLayer
  def sendMessage(message: Message): ZIO[Any, Throwable, Unit] =
    ZIO.serviceWith[WsService.Service](_.sendMessage(message)).provideLayer(wsLayer)

  def agreeAddGroup(msg: Message): ZIO[Any, Throwable, Unit] =
    ZIO.serviceWith[WsService.Service](_.agreeAddGroup(msg)).provideLayer(wsLayer)

  def refuseAddGroup(msg: Message): ZIO[Any, Throwable, Unit] =
    ZIO.serviceWith[WsService.Service](_.refuseAddGroup(msg)).provideLayer(wsLayer)

  def refuseAddFriend(messageBoxId: Int, user: User, to: Int): ZIO[Any, Throwable, Boolean] =
    ZIO.serviceWith[WsService.Service](_.refuseAddFriend(messageBoxId, user, to)).provideLayer(wsLayer)

  def deleteGroup(master: User, groupname: String, gid: Int, uid: Int): ZIO[Any, Throwable, Unit] =
    ZIO.serviceWith[WsService.Service](_.deleteGroup(master, groupname, gid, uid)).provideLayer(wsLayer)

  def removeFriend(uId: Int, friendId: Int): ZIO[Any, Throwable, Unit] =
    ZIO.serviceWith[WsService.Service](_.removeFriend(uId, friendId)).provideLayer(wsLayer)

  def addGroup(uId: Int, message: Message): ZIO[Any, Throwable, Unit] =
    ZIO.serviceWith[WsService.Service](_.addGroup(uId, message)).provideLayer(wsLayer)

  def addFriend(uId: Int, message: Message): ZIO[Any, Throwable, Unit] =
    ZIO.serviceWith[WsService.Service](_.addFriend(uId, message)).provideLayer(wsLayer)

  def countUnHandMessage(uId: Int): ZIO[Any, Throwable, Map[String, String]] =
    ZIO.serviceWith[WsService.Service](_.countUnHandMessage(uId)).provideLayer(wsLayer)

  def checkOnline(message: Message): ZIO[Any, Throwable, Map[String, String]] =
    ZIO.serviceWith[WsService.Service](_.checkOnline(message)).provideLayer(wsLayer)

  def sendMessage(message: String, actorRef: ActorRef): ZIO[Any, Throwable, Unit] =
    ZIO.serviceWith[WsService.Service](_.sendMessage(message, actorRef)).provideLayer(wsLayer)

  def changeOnline(uId: Int, status: String): ZIO[Any, Throwable, Boolean] =
    ZIO.serviceWith[WsService.Service](_.changeOnline(uId, status)).provideLayer(wsLayer)

  def readOfflineMessage(message: Message): ZIO[Any, Throwable, Unit] =
    ZIO.serviceWith[WsService.Service](_.readOfflineMessage(message)).provideLayer(wsLayer)

  def getConnections: ZIO[Any, Throwable, Int] =
    ZIO.serviceWith[WsService.Service](_.getConnections).provideLayer(wsLayer)

}
