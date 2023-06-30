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

package org.bitlap.zim

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import scala.util.Try

import io.circe._

import zio.schema._

/** @author
 *    梦境迷离
 *  @since 2022/2/2
 *  @version 1.0
 */
package object domain {

  implicit val encodeDate: Encoder[ZonedDateTime] =
    Encoder.encodeString.contramap[ZonedDateTime](t =>
      if (t == null) "" else t.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    )

  implicit val decodeDate: Decoder[ZonedDateTime] = Decoder.decodeString.emapTry { str =>
    Try {
      if (str == null) ZonedDateTime.now() else ZonedDateTime.parse(str)
    }
  }

  // 定义自己的schema 用以描述日期序列化
  implicit val zonedDateTimeSchema: Schema[ZonedDateTime] = Schema[String].transformOrFail(
    f => Right(if (f == null) ZonedDateTime.now() else ZonedDateTime.parse(f)),
    g => Right(if (g == null) "" else g.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
  )

}
