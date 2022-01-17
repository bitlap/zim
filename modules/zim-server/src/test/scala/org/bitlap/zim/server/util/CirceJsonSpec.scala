package org.bitlap.zim.server.util

import io.circe.jawn
import io.circe.syntax.EncoderOps
import org.bitlap.zim.domain.{ Message, Mine }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
 * @author 梦境迷离
 * @since 2022/1/16
 * @version 1.0
 */
class CirceJsonSpec extends AnyFlatSpec with Matchers {

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

}
