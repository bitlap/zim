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

package org.bitlap.zim.api.service

import akka.actor.ActorRef
import org.bitlap.zim.domain
import org.bitlap.zim.domain.model.User

trait WsService[F[_]] {

  def sendMessage(message: domain.Message): F[Unit]

  def agreeAddGroup(msg: domain.Message): F[Unit]

  def refuseAddGroup(msg: domain.Message): F[Unit]

  def refuseAddFriend(messageBoxId: Int, username: String, to: Int): F[Boolean]

  def deleteGroup(master: User, groupname: String, gid: Int, uid: Int): F[Unit]

  def removeFriend(uId: Int, friendId: Int): F[Unit]

  def addGroup(uId: Int, message: domain.Message): F[Unit]

  def addFriend(uId: Int, message: domain.Message): F[Unit]

  def countUnHandMessage(uId: Int): F[Map[String, String]]

  def checkOnline(message: domain.Message): F[Map[String, String]]

  def sendMessage(message: String, actorRef: ActorRef): F[Unit]

  def changeOnline(uId: Int, status: String): F[Boolean]

  def readOfflineMessage(message: domain.Message): F[Unit]

  def getConnections: F[Int]
}
