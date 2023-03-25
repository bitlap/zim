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

import java.time.format.DateTimeFormatter
import java.time.{ZoneId, ZonedDateTime}
import java.util.Locale

/** @author
 *    梦境迷离
 *  @since 2022/2/12
 *  @version 1.0
 */
object DateHelper {

  val fromPattern: DateTimeFormatter = DateTimeFormatter
    .ofPattern("yyyy-MM-dd HH:mm:ss")
    .withLocale(Locale.CHINA)
    .withZone(ZoneId.of("Asia/Shanghai"))

  def getConstantTime: ZonedDateTime =
    ZonedDateTime.parse("2022-02-11 08:00:00", fromPattern)

}
