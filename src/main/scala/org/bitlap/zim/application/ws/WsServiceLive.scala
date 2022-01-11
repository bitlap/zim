package org.bitlap.zim.application.ws

import akka.actor.ActorRef
import org.bitlap.zim.application.ws.WsService.Service
import org.bitlap.zim.configuration.ApplicationConfiguration.ZApplicationConfiguration
import org.bitlap.zim.domain.model.User
import org.bitlap.zim.domain.Message
import zio.{ IO, ZLayer }
import zio.stream.ZStream
import java.util.concurrent.ConcurrentHashMap
import org.bitlap.zim.configuration.SystemConstant
import zio.ZIO

/**
 * @author 梦境迷离
 * @version 1.0,2022/1/11
 */
object WsServiceLive {

  final lazy val actorRefSessions: ConcurrentHashMap[Integer, ActorRef] = new ConcurrentHashMap[Integer, ActorRef]

  lazy val live: ZLayer[ZApplicationConfiguration, Nothing, WsService] =
    ZLayer.fromService { env =>
      val userService = env.userApplication
      new Service {

        override def sendMessage(message: Message): ZIO[Nothing, Throwable, Unit] = {
          env.apiApplication.findById(1).flatMap(_ => ZStream.succeed(()))
          IO.unit
        }

        override def agreeAddGroup(msg: Message): ZIO[Nothing, Throwable, Unit] = ???

        override def refuseAddGroup(msg: Message): ZIO[Nothing, Throwable, Unit] = ???

        override def refuseAddFriend(messageBoxId: Int, user: User, to: Int): ZIO[Nothing, Throwable, Boolean] = ???

        override def deleteGroup(master: User, groupname: String, gid: Int, uid: Int): ZIO[Nothing, Throwable, Unit] =
          ???

        override def removeFriend(uId: Int, friendId: Int): ZIO[Nothing, Throwable, Unit] = ???

        override def addGroup(uId: Int, message: Message): ZIO[Nothing, Throwable, Unit] = ???

        override def addFriend(uId: Int, message: Message): ZIO[Nothing, Throwable, Unit] = ???

        override def countUnHandMessage(uId: Int): ZIO[Nothing, Throwable, Map[String, String]] = ???

        override def checkOnline(message: Message): ZIO[Nothing, Throwable, Map[String, String]] = ???

        override def sendMessage(message: String, actorRef: ActorRef): ZIO[Nothing, Throwable, Unit] = ???

        override def changeOnline(uId: Int, status: String): ZIO[Nothing, Throwable, Boolean] = ???

        override def readOfflineMessage(message: Message): ZIO[Nothing, Throwable, Unit] =
          message.mine.id.synchronized {
            userService
              .findOffLineMessage(message.mine.id, 0)
              .flatMap { _ =>
                if (message.to.`type` == SystemConstant.GROUP_TYPE) {
                  // 我所有的群中有未读的消息吗
                  userService.readGroupMessage(message.mine.id, message.mine.id)
                } else {
                  userService.readFriendMessage(message.mine.id, message.to.id)
                }
              }
              .foldM(())((_, _) => IO.unit)
          }

        override def getConnections: ZIO[Nothing, Throwable, Int] = ZIO.succeed(actorRefSessions.size())
      }
    }
}
