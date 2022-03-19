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

package org.bitlap.zim.domain.ws.protocol
import akka.actor.ActorRef
import io.circe.parser.decode
import org.bitlap.zim.domain.Message

/**
 * ws actor command
 */
sealed trait Command[+T]

/**
 * proxy
 * @param uId
 * @param  msg Now, default type is `String`, we use toJson to convert anything, should fix it in the future.
 * @param originActorRef The message comes from user, who send message to server flow by akka.
 */
case class TransmitMessageProxy(
  uId: Int,
  msg: String,
  originActorRef: Option[ActorRef]
) extends Command[String] {

  def getMessage: Message = decode[Message](msg).getOrElse(null)

}

/**
 * 在线用户
 */
case class OnlineUserMessage(description: Option[String]) extends Command[Unit]

/**
 * 用户状态变更
 */
case class UserStatusChangeMessage(uId: Int, typ: String, description: Option[String] = None) extends Command[Unit]
