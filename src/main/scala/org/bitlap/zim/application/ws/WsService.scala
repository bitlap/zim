package org.bitlap.zim.application.ws;

import akka.actor.ActorRef
import org.bitlap.zim.domain._
import org.bitlap.zim.domain.model._
import zio.Task

/**
 * @author 梦境迷离
 * @version 1.0, 2022/1/11
 */
object WsService {

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

}
