package org.bitlap.zim.domain

import io.circe.generic.semiauto._
import io.circe.{ Decoder, Encoder, Json }

/**
 * 群组
 *
 * @param id        群组id
 * @param groupName 群组名
 */
@SerialVersionUID(1L) class Group(val id: Int, val groupName: String)

object Group {

  implicit val decoder: Decoder[Group] = deriveDecoder[Group]
  implicit val encoder: Encoder[Group] = (a: Group) => {
    if (a == null) Json.Null
    else
      Json.obj(
        ("id", Json.fromInt(a.id)),
        ("groupname", Json.fromString(a.groupName)) // 表名是下划线，如果字段名不是标准驼峰不能自动转化。字段名改成驼峰后序列化时处理一下
      )
  }

}
