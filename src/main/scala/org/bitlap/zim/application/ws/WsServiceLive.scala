package org.bitlap.zim.application.ws

import akka.actor.ActorRef
import org.bitlap.zim.application.ws.WsService.Service
import org.bitlap.zim.configuration.ApplicationConfiguration.ZApplicationConfiguration
import org.bitlap.zim.domain.model.User
import org.bitlap.zim.domain.Message
import zio.{ IO, ZLayer }

/**
 * @author 梦境迷离
 * @version 1.0,2022/1/11
 */
object WsServiceLive {

  lazy val live: ZLayer[ZApplicationConfiguration, Nothing, WsService] =
    ZLayer.fromFunction { env =>
      new Service {
        override def sendMessage(message: Message): IO[Nothing, Unit] = ???

        override def agreeAddGroup(msg: Message): IO[Nothing, Unit] = ???

        override def refuseAddGroup(msg: Message): IO[Nothing, Unit] = ???

        override def refuseAddFriend(messageBoxId: Int, user: User, to: Int): IO[Nothing, Boolean] = ???

        override def deleteGroup(master: User, groupname: String, gid: Int, uid: Int): IO[Nothing, Unit] = ???

        override def removeFriend(uId: Int, friendId: Int): IO[Nothing, Unit] = ???

        override def addGroup(uId: Int, message: Message): IO[Nothing, Unit] = ???

        override def addFriend(uId: Int, message: Message): IO[Nothing, Unit] = ???

        override def countUnHandMessage(uId: Int): IO[Nothing, Map[String, String]] = ???

        override def checkOnline(message: Message): IO[Nothing, Map[String, String]] = ???

        override def sendMessage(message: String, actorRef: ActorRef): IO[Nothing, Unit] = ???

        override def changeOnline(uId: Int, status: String): IO[Nothing, Boolean] = ???

        override def readOfflineMessage(message: Message): IO[Nothing, Unit] = ???

        override def getConnections: IO[Nothing, Int] = ???
      }
    }
}
