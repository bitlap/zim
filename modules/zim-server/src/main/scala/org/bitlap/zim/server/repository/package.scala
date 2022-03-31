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

package org.bitlap.zim.server

import org.bitlap.zim.domain.model._
import org.bitlap.zim.domain.repository.Condition
import scalikejdbc.streams._
import scalikejdbc.{ SQL, _ }
import sqls.count
import zio.interop.reactivestreams._
import zio.stream.ZStream
import zio.{ stream, Task }

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * 用户操作SQL
 *
 *  TODO 返回单个元素的被弄成了stream，这里有待考虑
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
package object repository {

  //==============================隐式转换========================================

  /**
   * scalikejdbc更新并返回主键的转换
   *
   * @param sqlUpdateWithGeneratedKey
   */
  implicit class sqlUpdateWithGeneratedKey(sqlUpdateWithGeneratedKey: SQLUpdateWithGeneratedKey) {
    def toUpdateReturnKey(implicit databaseName: String): stream.Stream[Throwable, Long] =
      ZStream.fromEffect(
        Task.effect(NamedDB(Symbol(databaseName)).localTx(implicit session => sqlUpdateWithGeneratedKey.apply()))
      )
  }

  /**
   * scalikejdbc更新操作转换
   *
   * @param sqlUpdate
   */
  implicit class executeUpdateOperation(sqlUpdate: SQLUpdate) {
    def toUpdateOperation(implicit databaseName: String): stream.Stream[Throwable, Int] =
      ZStream.fromEffect(
        Task.effect(NamedDB(Symbol(databaseName)).localTx(implicit session => sqlUpdate.apply()))
      )
  }

  /**
   * scalikejdbc单对象转换
   *
   * @param sql
   * @tparam T
   */
  implicit class executeSQLOperation[T](sql: SQL[T, HasExtractor]) {
    def toSQLOperation(implicit databaseName: String): stream.Stream[Throwable, T] =
      ZStream.fromIterable(NamedDB(Symbol(databaseName)).localTx(implicit session => sql.list().apply()))
  }

  /**
   * scalikejdbc流转换
   *
   * @param streamReadySQL
   * @tparam T
   */
  implicit class executeStreamOperation[T](streamReadySQL: StreamReadySQL[T]) {
    def toStreamOperation(implicit databaseName: String): stream.Stream[Throwable, T] =
      (NamedDB(Symbol(databaseName)) readOnlyStream streamReadySQL).toStream()
  }

  implicit final class SQLSyntaxStringArrow[T](private val self: String)(implicit
    val sp: QuerySQLSyntaxProvider[SQLSyntaxSupport[T], T]
  ) {

    import eu.timepit.refined.refineV
    import org.bitlap.zim.domain.repository.Condition.ConditionValidator._
    import org.bitlap.zim.domain.repository.Condition._

    @inline def like(y: Option[String]): Option[ZCondition] =
      refineV(Condition(self, y.map(sqls.like(sp.column(self), _)).orNull)).toOption

    @inline def like(y: String): Option[ZCondition] = like(Option(y))

    @inline def ===[B: ParameterBinderFactory](y: Option[B]): Option[ZCondition] =
      refineV(Condition(self, y.map(sqls.eq(sp.column(self), _)).orNull)).toOption

    @inline def ===[B: ParameterBinderFactory](y: B): Option[ZCondition] = ===(Option(y))
  }

  //==============================表别名定义========================================
  implicit private lazy val u: QuerySQLSyntaxProvider[SQLSyntaxSupport[User], User] = User.syntax("u")

  implicit private lazy val g: QuerySQLSyntaxProvider[SQLSyntaxSupport[GroupList], GroupList] = GroupList.syntax("g")

  implicit private lazy val r: QuerySQLSyntaxProvider[SQLSyntaxSupport[Receive], Receive] = Receive.syntax("r")

  implicit private lazy val fg: QuerySQLSyntaxProvider[SQLSyntaxSupport[FriendGroup], FriendGroup] =
    FriendGroup.syntax("fg")

  implicit private lazy val af: QuerySQLSyntaxProvider[SQLSyntaxSupport[AddFriend], AddFriend] = AddFriend.syntax("af")

  implicit private lazy val gm: QuerySQLSyntaxProvider[SQLSyntaxSupport[GroupMember], GroupMember] =
    GroupMember.syntax("gm")

  implicit private lazy val am: QuerySQLSyntaxProvider[SQLSyntaxSupport[AddMessage], AddMessage] =
    AddMessage.syntax("am")

  //==============================测试SQL========================================
  private[repository] def queryFindReceiveById(id: Long): SQL[Receive, HasExtractor] =
    sql"SELECT ${r.result.*} FROM ${Receive as r} WHERE id = ${id}".list().map(rs => Receive(rs))
  //==============================用户 SQL实现========================================

  /**
   * 更新用户头像
   *
   * @param avatar
   * @param uid
   * @return
   */
  private[repository] def _updateAvatar(avatar: String, uid: Int): SQLUpdate =
    sql"update ${User.table} set avatar=${avatar} where id=${uid};".update()

  /**
   * 更新签名
   *
   * @param sign
   * @param uid
   * @return
   */
  private[repository] def _updateSign(sign: String, uid: Int): SQLUpdate =
    sql"update ${User.table} set sign = ${sign} where id = ${uid};".update()

  /**
   * 更新用户信息
   *
   * @param id
   * @param user
   * @return
   */
  private[repository] def _updateUserInfo(id: Int, user: User): SQLUpdate =
    sql"update ${User.table} set username= ${user.username}, sex = ${user.sex}, sign = ${user.sign}, password = ${user.password} where id = ${id}; "
      .update()

  /**
   * 更新用户状态
   *
   * @param status
   * @param uid
   * @return
   */
  private[repository] def _updateUserStatus(status: String, uid: Int): SQLUpdate =
    sql"update ${User.table} set status = ${status} where id = ${uid};".update()

  /**
   * 激活用户账号
   *
   * @param activeCode
   * @return
   */
  private[repository] def _activeUser(activeCode: String): SQLUpdate =
    sql"update ${User.table} set status = 'offline' where active = ${activeCode};".update()

  /**
   * 根据群组ID查询群里用户的信息
   *
   * @param gid group id
   * @return
   */
  private[repository] def _findUserByGroupId(gid: Int): StreamReadySQL[User] =
    sql"select ${u.result.*} from ${User as u} where id in(select ${gm.uid} from ${GroupMember as gm} where gid = ${gid});"
      .map(User(_))
      .list()
      .iterator()

  /**
   * 根据好友列表ID查询用户信息列表
   *
   * @param fgid
   * @return
   */
  private[repository] def _findUsersByFriendGroupIds(fgid: Int): StreamReadySQL[User] =
    sql"select ${u.result.*} from ${User as u} where id in (select ${af.uid} from ${AddFriend as af} where fgid = ${fgid});"
      .map(User(_))
      .list()
      .iterator()

  /**
   * 保存用户信息
   *
   * @param user
   * @return
   */
  private[repository] def _saveUser(user: User): SQLUpdateWithGeneratedKey =
    sql"insert into ${User.table}(username,password,sign,email,create_date,active) values(${user.username},${user.password},${user.sign},${user.email},${user.createDate},${user.active});"
      .updateAndReturnGeneratedKey("id")

  //==============================群组 SQL实现========================================

  /**
   * 创建群组
   *
   * @param groupList
   * @return
   */
  private[repository] def _createGroupList(groupList: GroupList): SQLUpdateWithGeneratedKey =
    sql"insert into ${GroupList.table}(group_name,avatar,create_id) values(${groupList.groupName},${groupList.avatar},${groupList.createId});"
      .updateAndReturnGeneratedKey("id")

  /**
   * 删除群组
   *
   * @param table
   * @param groupList
   * @return
   */
  private[repository] def _deleteGroup(id: Int): SQLUpdate =
    sql"delete from ${GroupList.table} where id = ${id};".executeUpdate()

  /**
   * 根据群名模糊统计
   *
   * @param groupName
   * @return
   */
  private[repository] def _countGroup(groupName: Option[String]): StreamReadySQL[Int] =
    withSQL {
      select(count(g.id))
        .from(GroupList as g)
        .where(
          sqls.toAndConditionOpt(
            groupName.map(gn => sqls.like(g.column("group_name"), s"%$gn%"))
          )
        )
    }.toList().map(rs => rs.int(1)).iterator()

  /**
   * 根据群名模糊查询群
   *
   * @param groupName
   * @return
   */
  private[repository] def _findGroups(groupName: Option[String]): StreamReadySQL[GroupList] =
    withSQL {
      select
        .from(GroupList as g)
        .where(
          sqls.toAndConditionOpt(
            // value 需要 % % ？
            groupName.map(gn => sqls.like(g.column("group_name"), s"%$gn%"))
          )
        )
    }.toList().map(rs => GroupList(rs)).iterator()

  /**
   * 根据群id查询群信息
   *
   * @param gid
   * @return
   */
  private[repository] def _findGroupById(gid: Int): StreamReadySQL[GroupList] =
    sql"select ${g.result.*} from ${GroupList as g} where id = ${gid};"
      .map(rs => GroupList(rs))
      .list()
      .iterator()

  /**
   * 根据用户id查询用户所在的群组列表，不管是自己创建的还是别人创建的
   *
   * @param uid
   * @return
   */
  private[repository] def _findGroupsById(
    uid: Int
  ): StreamReadySQL[GroupList] =
    sql"select ${g.result.*} from ${GroupList as g} where id in(select distinct ${gm.gid} from ${GroupMember as gm} where uid = ${uid});"
      .map(rs => GroupList(rs))
      .list()
      .iterator()

  //==============================聊天消息 SQL实现========================================

  /**
   * 保存用户聊天记录
   *
   * @param receive
   * @return
   */
  private[repository] def _saveMessage(receive: Receive): SQLUpdate =
    sql"insert into ${Receive.table}(toid,mid,fromid,content,type,timestamp,status) values(${receive.toid},${receive.mid},${receive.fromid},${receive.content},${receive.`type`},${receive.timestamp},${receive.status});"
      .update()

  /**
   * 查询消息
   *
   * @param uid 消息所属用户
   * @param status  历史消息还是离线消息 0代表离线 1表示已读
   * @return
   */
  private[repository] def _findOffLineMessage(
    uid: Int,
    status: Int
  ): StreamReadySQL[Receive] =
    sql"select ${r.result.*} from ${Receive as r} where toid = ${uid} and status = ${status};"
      .map(rs => Receive(rs))
      .list()
      .iterator()

  /**
   * 查询消息
   *
   * @param uid 消息所属用户
   * @param mid 来自哪个用户
   * @param typ 消息类型，可能来自friend或者group
   * @return
   */
  private[repository] def _findHistoryMessage(
    uid: Option[Int],
    mid: Option[Int],
    typ: Option[String]
  ): StreamReadySQL[Receive] =
    withSQL {
      select
        .from(Receive as r)
        .where(
          sqls.toAndConditionOpt(
            typ.map(ty => sqls.eq(r.`type`, ty)),
            sqls.toOrConditionOpt(
              sqls.toAndConditionOpt(
                uid.map(uid => sqls.eq(r.column("mid"), uid)),
                mid.map(mid => sqls.eq(r.toid, mid))
              ),
              sqls.toAndConditionOpt(
                mid.map(mid => sqls.eq(r.column("mid"), mid)),
                uid.map(uid => sqls.eq(r.toid, uid))
              )
            )
          )
        )
    }.toList().map(rs => Receive(rs)).iterator()

  /**
   * 统计查询消息
   *
   * @param uid 消息所属用户
   * @param mid 来自哪个用户
   * @param typ 消息类型，可能来自friend或者group
   * @return
   */
  private[repository] def _countHistoryMessage(
    uid: Option[Int],
    mid: Option[Int],
    typ: Option[String]
  ): StreamReadySQL[Int] =
    withSQL {
      select(count(r.mid))
        .from(Receive as r)
        .where(
          sqls.toAndConditionOpt(
            typ.map(ty => sqls.eq(r.`type`, ty)),
            sqls.toOrConditionOpt(
              sqls.toAndConditionOpt(
                uid.map(uid => sqls.eq(r.toid, uid)),
                mid.map(mid => sqls.eq(r.mid, mid))
              ),
              sqls.toAndConditionOpt(
                mid.map(mid => sqls.eq(r.toid, mid)),
                uid.map(uid => sqls.eq(r.mid, uid))
              )
            )
          )
        )
    }.toList().map(rs => rs.int(1)).iterator()

  /**
   * 置为已读
   *
   * @param mine
   * @param to
   * @param typ
   * @return
   */
  private[repository] def _readMessage(mine: Int, to: Int, typ: String): SQLUpdate =
    sql"update ${Receive.table} set status = 1 where status = 0 and mid = ${mine} and toid = ${to} and type = ${typ};"
      .update()

  //==============================好友分组 SQL实现========================================

  /**
   * 创建好友分组记录
   *
   * @param friendGroup 好友分组
   * @return
   */
  private[repository] def _createFriendGroup(friendGroup: FriendGroup): SQLUpdate =
    sql"insert into ${FriendGroup.table}(group_name,uid) values(${friendGroup.groupName},${friendGroup.uid});"
      .update()

  /**
   * 根据ID查询该用户的好友分组的列表
   *
   * @param uid 用户ID
   * @return
   */
  private[repository] def _findFriendGroupsById(uid: Int): StreamReadySQL[FriendGroup] =
    sql"select ${fg.result.*} from ${FriendGroup as fg} where uid = ${uid};"
      .map(rs => FriendGroup(rs))
      .list()
      .iterator()

  //==============================好友分组中人的操作 SQL实现========================================

  /**
   * 删除好友
   *
   * @param friendId
   * @param uId
   * @return
   */
  private[repository] def _removeFriend(friendId: Int, uId: Int) =
    sql"delete from ${AddFriend.table} where fgid in (select id from ${FriendGroup.table} where uid in (${friendId}, ${uId})) and uid in(${friendId}, ${uId});"
      .executeUpdate()

  /**
   * 移动好友分组
   *
   * @param groupId
   * @param originRecordId
   * @return
   */
  private[repository] def _changeGroup(groupId: Int, originRecordId: Int) =
    sql"update ${AddFriend.table} set fgid = ${groupId} where id = ${originRecordId};"
      .executeUpdate()

  /**
   * 查询我的好友的分组
   *
   * @param uId 被移动的好友id
   * @param mId 我的id
   */
  private[repository] def _findUserGroup(
    uId: Int,
    mId: Int
  ): StreamReadySQL[Int] =
    sql"select id from ${AddFriend.table} where fgid in (select id from ${FriendGroup.table} where uid = ${mId}) and uid = ${uId}"
      .list()
      .map(rs => rs.int(1))
      .iterator()

  /**
   * 添加好友操作
   *
   * @param from
   * @param to
   * @return
   */
  private[repository] def _addFriend(from: AddFriend, to: AddFriend): SQLUpdate =
    sql"insert into ${AddFriend.table}(fgid,uid) values(${from.fgid},${to.uid}),(${to.fgid}, ${from.uid});"
      .update()

  //==============================群组成员 SQL实现==============================================

  /**
   * 退出群
   *
   * @param groupMember 群成员对象
   * @return
   */
  private[repository] def _leaveOutGroup(groupMember: GroupMember): SQLUpdate =
    sql"delete from ${GroupMember.table} where gid = ${groupMember.gid} and uid = ${groupMember.uid};".update()

  /**
   * 查询用户编号
   *
   * @param gid
   * @return
   */
  private[repository] def _findGroupMembers(gid: Int): StreamReadySQL[Int] =
    sql" select uid from ${GroupMember.table} where gid = ${gid};"
      .list()
      .map(rs => rs.int(1))
      .iterator()

  /**
   * 添加群成员
   *
   * @param groupMember 群成员对象
   * @return
   */
  private[repository] def _addGroupMember(groupMember: GroupMember): SQLUpdate =
    sql"insert into ${GroupMember.table}(gid,uid) values(${groupMember.gid},${groupMember.uid});"
      .update()

  //==============================申请消息 SQL实现==============================================
  /**
   * 查询添加好友、群组信息
   *
   * @param table
   * @param uid
   * @return
   */
  private[repository] def _findAddInfo(uid: Int): StreamReadySQL[AddMessage] =
    withSQL {
      select
        .from(AddMessage as am)
        .where
        .eq(am.toUid, uid)
        .orderBy(am.time)
        .desc
    }.map(rs => AddMessage(rs)).list().iterator()

  /**
   * 更新好友、群组信息请求
   *
   * @param id 消息盒子id
   * @param agree 0未处理，1同意，2拒绝
   * @return
   */
  private[repository] def _updateAgree(id: Int, agree: Int): SQLUpdate =
    sql"update ${AddMessage.table} set agree = ${agree} where id = ${id}".update()

  /**
   * 添加好友、群组信息请求
   * ON DUPLICATE KEY UPDATE 首先这个语法的目的是为了解决重复性，当数据库中存在某个记录时，执行这条语句会更新它，而不存在这条记录时，会插入它。
   *
   * @param addMessage 添加好友、群组信息对象
   * @return
   */
  private[repository] def _saveAddMessage(addMessage: AddMessage): SQLUpdate =
    sql"insert into ${AddMessage.table}(from_uid,to_uid,group_id,remark,agree,type,time) values(${addMessage.fromUid},${addMessage.toUid},${addMessage.groupId},${addMessage.remark},${addMessage.agree},${addMessage.`type`},${addMessage.time}) ON DUPLICATE KEY UPDATE remark=${addMessage.remark},time=${addMessage.time},agree=${addMessage.agree};"
      .update()
}
