/*
 * Copyright 2021 bitlap
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

package org.bitlap.zim.domain.model

import io.circe._
import io.circe.generic.semiauto._
import scalikejdbc.{ WrappedResultSet, _ }

import java.time.ZonedDateTime

/**
 * 用户
 *
 * @see table:t_user
 * @param id
 * @param username   用户名
 * @param password   密码
 * @param sign       签名
 * @param avatar     头像
 * @param email      邮箱
 * @param createDate 创建时间
 * @param sex        性别
 * @param status     状态
 * @param active     激活码
 */
final case class User(
  id: Int,
  username: String,
  password: String,
  sign: String, //TODO use `Option[String]`
  avatar: String,
  email: String,
  createDate: ZonedDateTime,
  sex: Int,
  status: String,
  active: String
)

object User extends BaseModel[User] {

  // 日期格式化
  import org.bitlap.zim.domain._

  // for dsl query
  // see https://groups.google.com/g/scalikejdbc-users-group/c/h2bUE7xgS5o
  override lazy val columns: collection.Seq[String] = autoColumns[User]()

  override val tableName = "t_user"

  // 由于经过了中间处理，需要显示调用解码器
  implicit val decoder: Decoder[User] = deriveDecoder[User]
  implicit val encoder: Encoder[User] = deriveEncoder[User]

  def apply(rs: WrappedResultSet)(implicit sp: SyntaxProvider[User]): User = autoConstruct(rs, sp)

  // null序列化需要自己定义，不能使用deriveEncoder/deriveDecoder宏 这里偷懒先使用默认值
  def apply(id: Int, email: String, password: String) = new User(
    id = id,
    username = "",
    password = password,
    sign = "",
    avatar = "",
    email = email,
    createDate = ZonedDateTime.now(),
    sex = 0,
    status = "",
    active = ""
  )
}
