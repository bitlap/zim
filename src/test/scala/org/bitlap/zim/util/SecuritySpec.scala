package org.bitlap.zim.util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.BootstrapRuntime
import zio.crypto.hash.Hash

/**
 * @author 梦境迷离
 * @since 2022/1/9
 * @version 1.0
 */
final class SecuritySpec extends AnyFlatSpec with Matchers with BootstrapRuntime {

  "encrypt" should "ok" in {
    val encrypt = unsafeRun(SecurityUtil.encrypt("123456").provideLayer(Hash.live))
    println(encrypt.value)
    assert(encrypt.value == "jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=")
  }

  "getUuid32" should "ok" in {
    val encrypt = unsafeRun(SecurityUtil.encrypt("123456").provideLayer(Hash.live))
    val matchPwd = unsafeRun(SecurityUtil.matched("123456", encrypt).provideLayer(Hash.live))
    println(matchPwd)
    assert(matchPwd)
  }

}