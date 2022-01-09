package org.bitlap.zim.domain.model

import io.circe._
import io.circe.generic.semiauto._
import scalikejdbc.{ WrappedResultSet, _ }

import java.time.ZonedDateTime

/**
 * 添加消息
 *
 * @see table:t_add_message
 * @param id 暂时都使用`Int`
 * @param fromUid 谁发起的请求
 * @param toUid   发送给谁的申请,可能是群，那么就是创建该群组的用户
 * @param groupId 如果是添加好友则为from_id的分组id，如果为群组则为群组id
 * @param remark  附言
 * @param agree   0未处理，1同意，2拒绝
 * @param `type`  类型，可能是添加好友或群组
 * @param time    申请时间
 */
final case class AddMessage(
  id: Int,
  fromUid: Int,
  toUid: Int,
  groupId: Int,
  remark: String,
  agree: Int,
  `type`: Int,
  time: ZonedDateTime
)

object AddMessage extends SQLSyntaxSupport[AddMessage] {

  override lazy val columns: collection.Seq[String] = autoColumns[AddMessage]()

  override val tableName = "t_add_message"

  implicit val decoder: Decoder[AddMessage] = deriveDecoder[AddMessage]
  implicit val encoder: Encoder[AddMessage] = deriveEncoder[AddMessage]

  def apply(rs: WrappedResultSet): AddMessage = AddMessage(
    rs.int("id"),
    rs.int("from_uid"),
    rs.int("to_uid"),
    rs.int("group_id"),
    rs.string("remark"),
    rs.int("agree"),
    rs.int("type"),
    rs.zonedDateTime("create_date")
  )

  def apply(id: Int, agree: Int): AddMessage =
    AddMessage(
      id = id,
      fromUid = 0,
      toUid = 0,
      groupId = 0,
      remark = null,
      agree = agree,
      `type` = 0,
      time = null
    )

  def apply(
    fromUid: Int,
    toUid: Int,
    groupId: Int,
    remark: String,
    `type`: Int,
    time: ZonedDateTime
  ): AddMessage =
    AddMessage(0, fromUid, toUid, groupId, remark, 0, `type`, time)
}
