/*
 * Copyright 2021 bitlap
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

/**
 * IM Protocol
 *
 * @author 梦境迷离
 * @version 1.0,2022/1/11
 */
sealed trait protocol {
  self =>
  @inline final def stringify: String = self match {
    case protocol.readOfflineMessage => "readOfflineMessage"
    case protocol.message            => "message"
    case protocol.checkOnline        => "checkOnline"
    case protocol.addGroup           => "addGroup"
    case protocol.changOnline        => "changOnline"
    case protocol.addFriend          => "addFriend"
    case protocol.agreeAddFriend     => "agreeAddFriend"
    case protocol.agreeAddGroup      => "agreeAddGroup"
    case protocol.refuseAddGroup     => "refuseAddGroup"
    case protocol.unHandMessage      => "unHandMessage"
    case protocol.delFriend          => "delFriend"
  }
}

object protocol {

  final case object readOfflineMessage extends protocol

  final case object message extends protocol

  final case object checkOnline extends protocol

  final case object addGroup extends protocol

  final case object changOnline extends protocol

  final case object addFriend extends protocol

  final case object agreeAddFriend extends protocol

  final case object agreeAddGroup extends protocol

  final case object refuseAddGroup extends protocol

  final case object unHandMessage extends protocol

  final case object delFriend extends protocol

  @inline final def unStringify(`type`: String): protocol = {
    val mapping = Map(
      protocol.readOfflineMessage.stringify -> readOfflineMessage,
      protocol.message.stringify -> message,
      protocol.checkOnline.stringify -> checkOnline,
      protocol.addGroup.stringify -> addGroup,
      protocol.changOnline.stringify -> changOnline,
      protocol.addFriend.stringify -> addFriend,
      protocol.agreeAddFriend.stringify -> agreeAddFriend,
      protocol.agreeAddGroup.stringify -> agreeAddGroup,
      protocol.refuseAddGroup.stringify -> refuseAddGroup,
      protocol.unHandMessage.stringify -> unHandMessage,
      protocol.delFriend.stringify -> delFriend
    )

    mapping(`type`)

  }
}
