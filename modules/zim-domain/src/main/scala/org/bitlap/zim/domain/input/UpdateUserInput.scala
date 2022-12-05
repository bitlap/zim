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

package org.bitlap.zim.domain.input

import io.circe._
import io.circe.generic.semiauto._

/** 用户信息提交 输入
 *
 *  @param id
 *  @param username
 *    用户名
 *  @param password
 *    密码
 *  @param oldpwd
 *    旧密码
 *  @param sign
 *    签名
 *  @param sex
 *    性别
 */
final case class UpdateUserInput(
  id: Int,
  username: String,
  password: Option[String],
  oldpwd: Option[String],
  sign: String,
  sex: String
)
object UpdateUserInput {

  implicit val decoder: Decoder[UpdateUserInput] = deriveDecoder[UpdateUserInput]

  implicit val encoder: Encoder[UpdateUserInput] = deriveEncoder[UpdateUserInput]

}
