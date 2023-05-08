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

package org.bitlap.zim.domain

import io.circe._
import io.circe.generic.semiauto._

/** 群组
 *
 *  @param id
 *    群组id
 *  @param groupName
 *    群组名
 */
@SerialVersionUID(1L) class Group(val id: Int, val groupName: String)

object Group {

  implicit val decoder: Decoder[Group] = deriveDecoder[Group]

  implicit val encoder: Encoder[Group] = (a: Group) =>
    if (a == null) Json.Null
    else
      Json.obj(
        ("id", Json.fromInt(a.id)),
        ("groupname", Json.fromString(a.groupName)) // 表名是下划线，如果字段名不是标准驼峰不能自动转化。字段名改成驼峰后序列化时处理一下
      )

}
