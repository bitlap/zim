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

  case object readOfflineMessage extends protocol

  case object message extends protocol

  case object checkOnline extends protocol

  case object addGroup extends protocol

  case object changOnline extends protocol

  case object addFriend extends protocol

  case object agreeAddFriend extends protocol

  case object agreeAddGroup extends protocol

  case object refuseAddGroup extends protocol

  case object unHandMessage extends protocol

  case object delFriend extends protocol

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
