package org.bitlap.zim.application.ws;

import akka.actor.ActorRef
import org.bitlap.zim.domain._
import org.bitlap.zim.domain.model._
import zio.IO
import zio.ZIO

/**
 * @author 梦境迷离
 * @version 1.0, 2022/1/11
 */
object WsService {

  trait Service {

    def sendMessage(message: Message): ZIO[Nothing, Throwable, Unit]

    def agreeAddGroup(msg: Message): ZIO[Nothing, Throwable, Unit]

    def refuseAddGroup(msg: Message): ZIO[Nothing, Throwable, Unit]

    def refuseAddFriend(messageBoxId: Int, user: User, to: Int): ZIO[Nothing, Throwable, Boolean]

    def deleteGroup(master: User, groupname: String, gid: Int, uid: Int): ZIO[Nothing, Throwable, Unit]

    def removeFriend(uId: Int, friendId: Int): ZIO[Nothing, Throwable, Unit]

    def addGroup(uId: Int, message: Message): ZIO[Nothing, Throwable, Unit]

    def addFriend(uId: Int, message: Message): ZIO[Nothing, Throwable, Unit]

    def countUnHandMessage(uId: Int): ZIO[Nothing, Throwable, Map[String, String]]

    def checkOnline(message: Message): ZIO[Nothing, Throwable, Map[String, String]]

    def sendMessage(message: String, actorRef: ActorRef): ZIO[Nothing, Throwable, Unit]

    def changeOnline(uId: Int, status: String): ZIO[Nothing, Throwable, Boolean]

    def readOfflineMessage(message: Message): ZIO[Nothing, Throwable, Unit]

    def getConnections: ZIO[Nothing, Throwable, Int]
  }

}
