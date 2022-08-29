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

import zio.stream._
import zio._

/** @author
 *    梦境迷离
 *  @since 2022/1/20
 *  @version 1.0
 */
object LogUtil {
  def info(msg: => String): UIO[Unit] = ZIO.logInfo(msg)

  def debug(msg: => String): UIO[Unit] = ZIO.debug(msg)

  def error(msg: => String): UIO[Unit] = ZIO.logError(msg)

  // 后面要把非必要的stream去掉
  def infoS(msg: => String): UStream[Unit] =
    ZStream.fromZIO(info(msg))

  def debugS(msg: => String): UStream[Unit] =
    ZStream.fromZIO(debug(msg))

  def errorS(msg: => String): UStream[Unit] =
    ZStream.fromZIO(error(msg))
}
