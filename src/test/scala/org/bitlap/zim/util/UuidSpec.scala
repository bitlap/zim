package org.bitlap.zim.util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.BootstrapRuntime

/**
 * @author 梦境迷离
 * @since 2022/1/9
 * @version 1.0
 */
final class UuidSpec extends AnyFlatSpec with Matchers with BootstrapRuntime {

  "getUuid64" should "ok" in {
    val uuid = unsafeRun(UuidUtil.getUuid64)
    println(uuid)
    assert(uuid != null)
    assert(uuid.length == 64)
  }

  "getUuid32" should "ok" in {
    val uuid = unsafeRun(UuidUtil.getUuid32)
    println(uuid)
    assert(uuid != null)
    assert(uuid.length == 32)
  }

}
