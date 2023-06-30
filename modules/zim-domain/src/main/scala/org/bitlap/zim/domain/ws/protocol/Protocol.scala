/*
 * Copyright 2023 bitlap
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

/** IM Protocol
 *
 *  @author
 *    梦境迷离
 *  @version 1.0,2022/1/11
 */
sealed trait Protocol {
  self =>

  @inline final def stringify: String = self match {
    case Protocol.readOfflineMessage => "readOfflineMessage"
    case Protocol.message            => "message"
    case Protocol.checkOnline        => "checkOnline"
    case Protocol.addGroup           => "addGroup"
    case Protocol.changOnline        => "changOnline"
    case Protocol.addFriend          => "addFriend"
    case Protocol.agreeAddFriend     => "agreeAddFriend"
    case Protocol.agreeAddGroup      => "agreeAddGroup"
    case Protocol.refuseAddGroup     => "refuseAddGroup"
    case Protocol.unHandMessage      => "unHandMessage"
    case Protocol.delFriend          => "delFriend"
  }
}

object Protocol {

  private lazy val mapping = Map(
    Protocol.readOfflineMessage.stringify -> readOfflineMessage,
    Protocol.message.stringify            -> message,
    Protocol.checkOnline.stringify        -> checkOnline,
    Protocol.addGroup.stringify           -> addGroup,
    Protocol.changOnline.stringify        -> changOnline,
    Protocol.addFriend.stringify          -> addFriend,
    Protocol.agreeAddFriend.stringify     -> agreeAddFriend,
    Protocol.agreeAddGroup.stringify      -> agreeAddGroup,
    Protocol.refuseAddGroup.stringify     -> refuseAddGroup,
    Protocol.unHandMessage.stringify      -> unHandMessage,
    Protocol.delFriend.stringify          -> delFriend
  )

  @inline final def unStringify(`type`: String): Protocol = mapping(`type`)

  final case object readOfflineMessage extends Protocol

  final case object message extends Protocol

  final case object checkOnline extends Protocol

  final case object addGroup extends Protocol

  final case object changOnline extends Protocol

  final case object addFriend extends Protocol

  final case object agreeAddFriend extends Protocol

  final case object agreeAddGroup extends Protocol

  final case object refuseAddGroup extends Protocol

  final case object unHandMessage extends Protocol

  final case object delFriend extends Protocol

}
