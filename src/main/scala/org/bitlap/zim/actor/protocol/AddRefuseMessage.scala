package org.bitlap.zim.actor.protocol

import org.bitlap.zim.domain.Mine

/**
 * 同意添加群
 */
case class AddRefuseMessage(toUid: Int, groupId: Int, messageBoxId: Int, mine: Mine)
