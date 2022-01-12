package org.bitlap.zim.actor.protocol

/**
 * 用户状态变更
 *
 * @param uId
 * @param typ
 */
case class UserStatusChange(uId: Int, typ: String)
