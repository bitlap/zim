package org.bitlap.zim.application.ws;

import akka.actor.ActorRef
import org.bitlap.zim.domain._
import org.bitlap.zim.domain.model._
import zio.IO

/**
 * @author 梦境迷离
 * @version 1.0, 2022/1/11
 */
object WsService {

  trait Service {

    def sendMessage(message: Message): IO[Nothing, Unit]

    def agreeAddGroup(msg: Message): IO[Nothing, Unit]

    def refuseAddGroup(msg: Message): IO[Nothing, Unit]

    def refuseAddFriend(messageBoxId: Int, user: User, to: Int): IO[Nothing, Boolean]

    def deleteGroup(master: User, groupname: String, gid: Int, uid: Int): IO[Nothing, Unit]

    def removeFriend(uId: Int, friendId: Int): IO[Nothing, Unit]

    def addGroup(uId: Int, message: Message): IO[Nothing, Unit]

    def addFriend(uId: Int, message: Message): IO[Nothing, Unit]

    def countUnHandMessage(uId: Int): IO[Nothing, Map[String, String]]

    def checkOnline(message: Message): IO[Nothing, Map[String, String]]

    def sendMessage(message: String, actorRef: ActorRef): IO[Nothing, Unit]

    def changeOnline(uId: Int, status: String): IO[Nothing, Boolean]

    def readOfflineMessage(message: Message): IO[Nothing, Unit]

    def getConnections: IO[Nothing, Int]
  }

}
