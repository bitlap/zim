package org.bitlap.zim.server.util

import org.bitlap.zim.domain.input.UserSecurity.UserSecurityInfo
import org.bitlap.zim.tapir.ApiJsonCodec
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.schema.codec.ProtobufCodec

/**
 * @author 梦境迷离
 * @since 2022/3/5
 * @version 1.0
 */
class SchemaSpec extends AnyFlatSpec with Matchers with ApiJsonCodec {

  "Schema" should "ok" in {
    // TODO 直接使用存Redis会有错误 209 变成 3104751
    val userSecurityInfo =
      UserSecurityInfo(209, "dreamylost@qq.com", "jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=", "顶顶顶顶")
    val chunkByte = ProtobufCodec.encode(UserSecurityInfo.userSecuritySchema)(userSecurityInfo)
    val str = ProtobufCodec.decode(UserSecurityInfo.userSecuritySchema)(chunkByte)
    val id = str.map(_.id)
    id.getOrElse(0) shouldBe userSecurityInfo.id
  }
}
