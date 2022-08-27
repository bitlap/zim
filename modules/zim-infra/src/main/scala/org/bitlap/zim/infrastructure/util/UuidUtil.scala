/*
 * Copyright 2022 bitlap
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

package org.bitlap.zim.infrastructure.util

import zio._

import java.util.UUID

/** UUID工具
 *
 *  @since 2021年12月31日
 *  @author
 *    梦境迷离
 */
object UuidUtil {

  /** 64位随机UUID
   */
  def getUuid64: UIO[String] =
    ZIO.succeed((UUID.randomUUID.toString + UUID.randomUUID.toString).replace("-", ""))

  /** 32位随机UUID
   */
  def getUuid32: UIO[String] =
    ZIO.succeed(UUID.randomUUID.toString.replace("-", ""))

}
