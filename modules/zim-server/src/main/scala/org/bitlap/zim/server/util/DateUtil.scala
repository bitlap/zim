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

package org.bitlap.zim.server.util

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/** 时间工具
 *
 *  @since 2021年12月31日
 *  @author
 *    梦境迷离
 */
object DateUtil {

  final lazy val pattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  /** 获取格式化后的当前时间yyyy-MM-dd
   *  @param now
   */
  def getDateString(now: ZonedDateTime = ZonedDateTime.now()): String =
    now.format(pattern)

}
