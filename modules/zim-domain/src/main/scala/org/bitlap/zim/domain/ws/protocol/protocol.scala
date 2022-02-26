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
