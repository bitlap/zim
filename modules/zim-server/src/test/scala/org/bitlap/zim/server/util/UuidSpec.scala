/*
 * Copyright 2023 bitlap
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

package org.bitlap.zim.server.util

import org.bitlap.zim.infrastructure.util.UuidUtil
import org.bitlap.zim.server.CommonTestSupport
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** @author
 *    梦境迷离
 *  @since 2022/1/9
 *  @version 1.0
 */
final class UuidSpec extends AnyFlatSpec with Matchers with CommonTestSupport {

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
