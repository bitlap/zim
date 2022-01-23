package org.bitlap.zim.server.util

import io.circe.jawn
import io.circe.syntax.EncoderOps
import org.bitlap.zim.domain.model.User
import org.bitlap.zim.domain.{ FriendAndGroupInfo, FriendList, Message, Mine }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.bitlap.zim.domain.model.GroupList
import org.bitlap.zim.domain.ResultSet
import org.bitlap.zim.server.api.endpoint.ApiJsonCodec

/**
 * @author 梦境迷离
 * @since 2022/1/16
 * @version 1.0
 */
class CirceJsonSpec extends AnyFlatSpec with Matchers with ApiJsonCodec {

  "Message fromJson" should "ok" in {
    val msg = """{"type":"changOnline","mine":"null","to":"null","msg":"online"}"""
    val msgObj = jawn.decode[Message](msg).getOrElse(null)
    msgObj.`type` shouldBe "changOnline"

    val objStr = msgObj.asJson.noSpaces

    // obj default is null
    objStr shouldBe """{"type":"changOnline","mine":"null","to":"null","msg":"online"}"""

  }

  "Mine fromJson" should "ok" in {
    val msg =
      """{"id": 1,"username": null,"mine": false,"avatar": "aaa","content": ""}""".stripMargin
    val msgObj = jawn.decode[Mine](msg).getOrElse(null)
    msgObj.id shouldBe 1

    // String default is `""`
    val objStr = msgObj.asJson.noSpaces
    objStr shouldBe """{"id":1,"username":"","mine":false,"avatar":"aaa","content":""}"""

  }

  "FriendAndGroupInfo" should "ok" in {
    val fagi = FriendAndGroupInfo(
      mine = User(0, "", ""),
      friend = List(FriendList(0, "", Nil)),
      group = List(GroupList(0, "", "", 0))
    )
    val ret = ResultSet(data = fagi)
    ret.asJson != null shouldBe true
  }

}
