package org.bitlap.zim

import org.bitlap.zim.domain.model.{ GroupList, Receive, User }
import scalikejdbc.{ NoExtractor, SQL, _ }
import scalikejdbc.streams._
import sqls.count
import zio.stream.ZStream
import zio.interop.reactivestreams._
import zio.{ stream, Task }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions

/**
 * 用户操作SQL
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
        Task.effect(NamedDB(Symbol(databaseName)).autoCommit(implicit session => sqlUpdateWithGeneratedKey.apply()))
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
        Task.effect(NamedDB(Symbol(databaseName)).autoCommit(implicit session => sqlUpdate.apply()))
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
      ZStream.fromIterable(NamedDB(Symbol(databaseName)).autoCommit(implicit session => sql.list().apply()))
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

  //==============================表别名定义========================================
  private[repository] lazy val u: QuerySQLSyntaxProvider[SQLSyntaxSupport[User], User] = User.syntax("u")
  private[repository] lazy val g: QuerySQLSyntaxProvider[SQLSyntaxSupport[GroupList], GroupList] = GroupList.syntax("g")
  private[repository] lazy val r: QuerySQLSyntaxProvider[SQLSyntaxSupport[Receive], Receive] = Receive.syntax("r")

  //==============================用户 SQL实现========================================
  private[repository] def queryFindById(table: TableDefSQLSyntax, id: Long): SQL[User, HasExtractor] =
    sql"SELECT * FROM ${table} WHERE id = ${id}".list().map(rs => User(rs))

  private[repository] def queryFindAll(table: TableDefSQLSyntax): StreamReadySQL[User] =
    sql"SELECT * FROM ${table}".list().map(r => User(r)).iterator()

  private[repository] def queryDeleteById(table: TableDefSQLSyntax, id: Long): SQL[Nothing, NoExtractor] =
    sql"DELETE FROM ${table} WHERE id = ${id};"

  /**
   * 根据用户名和性别统计用户
   *
   * @param username
   * @param sex
   * @return 这种stream只有一个元素
   */
  private[repository] def _countUser(username: Option[String], sex: Option[Int]): StreamReadySQL[Int] =
    withSQL {
      select(count(u.id))
        .from(User as u)
        .where(
          sqls.toAndConditionOpt(
            username.map(un => sqls.like(u.username, un)),
            sex.map(sex => sqls.eq(u.sex, sex))
          )
        )
    }.toList().map(rs => rs.get[Int]("id")).iterator()

  /**
   * 根据用户名和性别查询用户
   *
   * @param username
   * @param sex
   * @return
   */
  private[repository] def _findUser(username: Option[String], sex: Option[Int]): StreamReadySQL[User] =
    withSQL {
      select
        .from(User as u)
        .where(
          sqls.toAndConditionOpt(
            username.map(un => sqls.like(u.username, un)),
            sex.map(sex => sqls.eq(u.sex, sex))
          )
        )
    }.map(rs => User(rs)).list().iterator()

  /**
   * 更新用户头像
   *
   * @param table
   * @param avatar
   * @param uid
   * @return
   */
  private[repository] def _updateAvatar(table: TableDefSQLSyntax, avatar: String, uid: Int): SQLUpdate =
    sql"update $table set avatar=${avatar} where id=${uid};".update()

  /**
   * 更新签名
   *
   * @param table
   * @param sign
   * @param uid
   * @return
   */
  private[repository] def _updateSign(table: TableDefSQLSyntax, sign: String, uid: Int): SQLUpdate =
    sql"update $table set sign = ${sign} where id = ${uid};".update()

  /**
   * 更新用户信息
   *
   * @param table
   * @param id
   * @param user
   * @return
   */
  private[repository] def _updateUserInfo(table: TableDefSQLSyntax, id: Int, user: User): SQLUpdate =
    sql"update $table set username= ${user.username}, sex = ${user.sex}, sign = ${user.sign}, password = ${user.password} where id = ${id}; "
      .update()

  /**
   * 更新用户状态
   *
   * @param table
   * @param sign
   * @param uid
   * @return
   */
  private[repository] def _updateUserStatus(table: TableDefSQLSyntax, status: String, uid: Int): SQLUpdate =
    sql"update $table set status = ${status} where id = ${uid};".update()

  /**
   * 激活用户账号
   *
   * @param table
   * @param activeCode
   * @return
   */
  private[repository] def _activeUser(table: TableDefSQLSyntax, activeCode: String): SQLUpdate =
    sql"update $table set status = 'offline' where active = ${activeCode};".update()

  /**
   * 根据群组ID查询群里用户的信息
   *
   * @param table
   * @param memberTable
   * @param gid
   * @return
   */
  private[repository] def _findUserByGroupId(
    table: TableDefSQLSyntax,
    memberTable: TableDefSQLSyntax,
    gid: Int
  ): StreamReadySQL[User] =
    sql"select id,username,status,sign,avatar,email from $table where id in(select uid from $memberTable where gid = ${gid});"
      .map(rs => User(rs))
      .list()
      .iterator()

  /**
   * 根据好友列表ID查询用户信息列表
   *
   * @param table
   * @param friendGroupMemberTable
   * @param fgid
   * @return
   */
  private[repository] def _findUsersByFriendGroupIds(
    table: TableDefSQLSyntax,
    friendGroupMemberTable: TableDefSQLSyntax,
    fgid: Int
  ): StreamReadySQL[User] =
    sql"select id,username,avatar,sign,status,email,sex from $table where id in(select uid from $friendGroupMemberTable where fgid = ${fgid});"
      .map(rs => User(rs))
      .list()
      .iterator()

  /**
   * 保存用户信息
   *
   * @param table
   * @param user
   * @return
   */
  private[repository] def _saveUser(table: TableDefSQLSyntax, user: User): SQLUpdateWithGeneratedKey =
    sql"insert into t_user(username,password,email,create_date,active) values(${user.username},${user.password},${user.email},${user.createDate},${user.active});"
      .updateAndReturnGeneratedKey("id")

  /**
   * 根据邮箱匹配用户
   *
   * @param table
   * @param email
   * @return 这种stream只有一个元素
   */
  private[repository] def _matchUser(table: TableDefSQLSyntax, email: String): StreamReadySQL[User] =
    sql"select id,username,email,avatar,sex,sign,password,status,active,create_date from $table where email = ${email};"
      .map(rs => User(rs))
      .list()
      .iterator()

  //==============================群组 SQL实现========================================

  /**
   * 创建群组
   *
   * @param table
   * @param groupList
   * @return
   */
  private[repository] def _createGroupList(table: TableDefSQLSyntax, groupList: GroupList): SQLUpdateWithGeneratedKey =
    sql"insert into $table(group_name,avatar,create_id) values(${groupList.groupname},${groupList.avatar},${groupList.createId});"
      .updateAndReturnGeneratedKey("id")

  /**
   * 删除群组
   *
   * @param table
   * @param groupList
   * @return
   */
  private[repository] def _deleteGroup(table: TableDefSQLSyntax, id: Int): SQLUpdate =
    sql"delete from $table where id = ${id};".executeUpdate()

  /**
   * 根据群名模糊统计
   *
   * @param groupName
   * @return
   */
  private[repository] def _countGroup(groupName: Option[String]): StreamReadySQL[Int] =
    withSQL {
      select(count(u.id))
        .from(GroupList as g)
        .where(
          sqls.toAndConditionOpt(
            groupName.map(gn => sqls.like(g.groupname, gn))
          )
        )
    }.toList().map(rs => rs.get[Int]("id")).iterator()

  /**
   * 根据群名模糊查询群
   *
   * @param groupName
   * @return
   */
  private[repository] def _findGroup(groupName: Option[String]): StreamReadySQL[GroupList] =
    withSQL {
      select
        .from(GroupList as g)
        .where(
          sqls.toAndConditionOpt(
            groupName.map(gn => sqls.like(g.groupname, gn))
          )
        )
    }.toList().map(rs => GroupList(rs)).iterator()

  /**
   * 根据群id查询群信息
   *
   * @param table
   * @param gid
   * @return
   */
  private[repository] def _findGroupById(table: TableDefSQLSyntax, gid: Int): StreamReadySQL[GroupList] =
    sql"select id,group_name,avatar,create_id from $table where id = ${gid};"
      .map(rs => GroupList(rs))
      .list()
      .iterator()

  /**
   * 根据用户id查询用户所在的群组列表，不管是自己创建的还是别人创建的
   *
   * @param groupTable
   * @param groupMemberTable
   * @param uid
   * @return
   */
  private[repository] def _findGroupsById(
    groupTable: TableDefSQLSyntax,
    groupMemberTable: TableDefSQLSyntax,
    uid: Int
  ): StreamReadySQL[GroupList] =
    sql"select id,group_name,avatar,create_id from $groupTable where id in(select distinct gid from $groupMemberTable where uid = ${uid});"
      .map(rs => GroupList(rs))
      .list()
      .iterator()

  //==============================聊天消息 SQL实现========================================

  /**
   * 保存用户聊天记录
   *
   * @param table
   * @param receive
   * @return
   */
  private[repository] def _saveMessage(table: TableDefSQLSyntax, receive: Receive): SQLUpdate =
    sql"insert into $table(toid,mid,fromid,content,type,timestamp,status) values(${receive.toid},${receive.id},${receive.fromid},${receive.content},${receive.`type`},${receive.timestamp},${receive.status});"
      .update()

  /**
   * 查询消息
   *
   * @param table
   * @param uid 消息所属用户
   * @param status  历史消息还是离线消息 0代表离线 1表示已读
   * @return
   */
  private[repository] def _findOffLineMessage(
    table: TableDefSQLSyntax,
    uid: Int,
    status: Int
  ): StreamReadySQL[Receive] =
    sql"select toid,mid as id,fromid,content,type,timestamp,status from $table where uid = ${uid} and status = ${status};"
      .map(rs => Receive(rs))
      .list()
      .iterator()

  /**
   * 查询消息
   *
   * @param table
   * @param uid 消息所属用户
   * @param mid 来自哪个用户
   * @param `type` 消息类型，可能来自friend或者group
   * @return
   */
  private[repository] def _findHistoryMessage(
    table: TableDefSQLSyntax,
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
                uid.map(uid => sqls.eq(r.toid, uid)),
                mid.map(mid => sqls.eq(r.id, mid))
              ),
              sqls.toAndConditionOpt(
                uid.map(uid => sqls.eq(r.id, uid)),
                mid.map(mid => sqls.eq(r.toid, mid))
              )
            )
          )
        )
    }.toList().map(rs => Receive(rs)).iterator()

  /**
   * 统计查询消息
   *
   * @param table
   * @param uid 消息所属用户
   * @param mid 来自哪个用户
   * @param `type` 消息类型，可能来自friend或者group
   * @return
   */
  private[repository] def _countHistoryMessage(
    table: TableDefSQLSyntax,
    uid: Option[Int],
    mid: Option[Int],
    typ: Option[String]
  ): StreamReadySQL[Int] =
    withSQL {
      select(count(r.id))
        .from(Receive as r)
        .where(
          sqls.toAndConditionOpt(
            typ.map(ty => sqls.eq(r.`type`, ty)),
            sqls.toOrConditionOpt(
              sqls.toAndConditionOpt(
                uid.map(uid => sqls.eq(r.toid, uid)),
                mid.map(mid => sqls.eq(r.id, mid))
              ),
              sqls.toAndConditionOpt(
                uid.map(uid => sqls.eq(r.id, uid)),
                mid.map(mid => sqls.eq(r.toid, mid))
              )
            )
          )
        )
    }.toList().map(rs => rs.get[Int]("id")).iterator()

  /**
   * 置为已读
   *
   * @param table
   * @param mine
   * @param to
   * @param typ
   * @return
   */
  private[repository] def _readMessage(table: TableDefSQLSyntax, mine: Int, to: Int, typ: String): SQLUpdate =
    sql"update $table set status = 1 where status = 0 and mid = ${mine} and toid = ${to} and type = ${typ};".update()

}
