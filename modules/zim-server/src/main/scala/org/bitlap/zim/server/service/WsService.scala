/*
 * Copyright 2022 bitlap
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

package org.bitlap.zim.server.service

import akka.actor.ActorRef
import org.bitlap.zim.domain
import org.bitlap.zim.domain.model.User
import zio.Task

import java.util.concurrent.ConcurrentHashMap

/** @author
 *    梦境迷离
 *  @version 2.0, 2022/1/11
 */
trait WsService {

  val actorRefSessions: ConcurrentHashMap[Integer, ActorRef]

  def sendMessage(message: domain.Message): Task[Unit]

  def agreeAddGroup(msg: domain.Message): Task[Unit]

  def refuseAddGroup(msg: domain.Message): Task[Unit]

  def refuseAddFriend(messageBoxId: Int, username: String, to: Int): Task[Boolean]

  def deleteGroup(master: User, groupname: String, gid: Int, uid: Int): Task[Unit]

  def removeFriend(uId: Int, friendId: Int): Task[Unit]

  def addGroup(uId: Int, message: domain.Message): Task[Unit]

  def addFriend(uId: Int, message: domain.Message): Task[Unit]

  def countUnHandMessage(uId: Int): Task[Map[String, String]]

  def checkOnline(message: domain.Message): Task[Map[String, String]]

  def sendMessage(message: String, actorRef: ActorRef): Task[Unit]

  def changeOnline(uId: Int, status: String): Task[Boolean]

  def readOfflineMessage(message: domain.Message): Task[Unit]

  def getConnections: Task[Int]
}
