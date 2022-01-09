package org.bitlap.zim

import org.bitlap.zim.domain.model.{ AddFriend, AddMessage, FriendGroup, GroupList, GroupMember, Receive, User }
import scalikejdbc.streams._
import scalikejdbc.{ SQL, _ }
import sqls.count
import zio.interop.reactivestreams._
import zio.stream.ZStream
import zio.{ stream, Task }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions

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

  //==============================表别名定义========================================
  implicit private[repository] lazy val u: QuerySQLSyntaxProvider[SQLSyntaxSupport[User], User] = User.syntax("u")
  private[repository] lazy val g: QuerySQLSyntaxProvider[SQLSyntaxSupport[GroupList], GroupList] = GroupList.syntax("g")
  private[repository] lazy val r: QuerySQLSyntaxProvider[SQLSyntaxSupport[Receive], Receive] = Receive.syntax("r")
  private[repository] lazy val fg: QuerySQLSyntaxProvider[SQLSyntaxSupport[FriendGroup], FriendGroup] =
    FriendGroup.syntax("fg")
  private[repository] lazy val af: QuerySQLSyntaxProvider[SQLSyntaxSupport[AddFriend], AddFriend] =
    AddFriend.syntax("af")
  private[repository] lazy val gm: QuerySQLSyntaxProvider[SQLSyntaxSupport[GroupMember], GroupMember] =
    GroupMember.syntax("gm")
  private[repository] lazy val am: QuerySQLSyntaxProvider[SQLSyntaxSupport[AddMessage], AddMessage] =
    AddMessage.syntax("am")

  //==============================测试SQL========================================
  private[repository] def queryFindUserById(id: Long): SQL[User, HasExtractor] =
    sql"SELECT ${u.result.*} FROM ${User as u} WHERE id = ${id}".list().map(User(_))

  private[repository] def queryFindGroupById(table: TableDefSQLSyntax, id: Long): SQL[GroupList, HasExtractor] =
    sql"SELECT * FROM ${table} WHERE id = ${id}".list().map(rs => GroupList(rs))
  private[repository] def queryFindReceiveById(table: TableDefSQLSyntax, id: Long): SQL[Receive, HasExtractor] =
    sql"SELECT * FROM ${table} WHERE id = ${id}".list().map(rs => Receive(rs))
  private[repository] def queryFindFriendGroupById(table: TableDefSQLSyntax, id: Long): SQL[FriendGroup, HasExtractor] =
    sql"SELECT * FROM ${table} WHERE id = ${id}".list().map(rs => FriendGroup(rs))
  private[repository] def queryFindGroupMemberById(table: TableDefSQLSyntax, id: Long): SQL[GroupMember, HasExtractor] =
    sql"SELECT * FROM ${table} WHERE id = ${id}".list().map(rs => GroupMember(rs))
  private[repository] def queryFindFriendGroupFriendById(
    table: TableDefSQLSyntax,
    id: Long
  ): SQL[AddFriend, HasExtractor] =
    sql"SELECT * FROM ${table} WHERE id = ${id}".list().map(rs => AddFriend(rs))
  private[repository] def queryFindAddMessageById(table: TableDefSQLSyntax, id: Long): SQL[AddMessage, HasExtractor] =
    sql"SELECT * FROM ${table} WHERE id = ${id}".list().map(rs => AddMessage(rs))
  //==============================用户 SQL实现========================================
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
            username.map(un => sqls.like(u.username, s"%$un%")),
            sex.map(sex => sqls.eq(u.sex, sex))
          )
        )
    }.toList().map(rs => rs.int(1)).iterator()

  /**
   * 根据用户名和性别查询用户
   *
   * @param username
   * @param sex
   * @return
   */
  private[repository] def _findUsers(username: Option[String], sex: Option[Int]): StreamReadySQL[User] =
    withSQL {
      select
        .from(User as u)
        .where(
          sqls.toAndConditionOpt(
            username.map(un => sqls.like(u.username, s"%$un%")),
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
   * @param email 邮件
   * @return 这种stream只有一个元素
   */
  private[repository] def _matchUser(email: String): StreamReadySQL[User] =
    sql"select ${u.result.*} from ${User as u} where email = ${email};"
      .map(User(_))
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
  private[repository] def _findGroup(groupName: Option[String]): StreamReadySQL[GroupList] =
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
    sql"select toid,mid as id,fromid,content,type,timestamp,status from $table where toid = ${uid} and status = ${status};"
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
                mid.map(mid => sqls.eq(r.column("mid"), mid))
              ),
              sqls.toAndConditionOpt(
                mid.map(mid => sqls.eq(r.toid, mid)),
                uid.map(uid => sqls.eq(r.column("mid"), uid))
              )
            )
          )
        )
    }.toList().map(rs => rs.int(1)).iterator()

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

  //==============================好友分组 SQL实现========================================

  /**
   * 创建好友分组记录
   *
   * @param table
   * @param friendGroup 好友分组
   * @return
   */
  private[repository] def _createFriendGroup(table: TableDefSQLSyntax, friendGroup: FriendGroup): SQLUpdate =
    sql"insert into $table(group_name,uid) values(${friendGroup.groupname},${friendGroup.uid});"
      .update()

  /**
   * 根据ID查询该用户的好友分组的列表
   *
   * @param table
   * @param uid 用户ID
   * @return
   */
  private[repository] def _findFriendGroupsById(table: TableDefSQLSyntax, uid: Int): StreamReadySQL[FriendGroup] =
    sql"select id, uid, group_name  from ${table} where uid = ${uid};"
      .map(rs => FriendGroup(rs))
      .list()
      .iterator()

  //==============================好友分组中人的操作 SQL实现========================================

  /**
   * 删除好友
   *
   * @param friendGroupFriendTable
   * @param friendGroupTable
   * @param friendId
   * @param uId
   * @return
   */
  private[repository] def _removeFriend(
    friendGroupFriendTable: TableDefSQLSyntax,
    friendGroupTable: TableDefSQLSyntax,
    friendId: Int,
    uId: Int
  ) =
    sql"delete from $friendGroupFriendTable where fgid in (select id from $friendGroupTable where uid in (${friendId}, ${uId})) and uid in(${friendId}, ${uId});"
      .executeUpdate()

  /**
   * 移动好友分组
   *
   * @param friendGroupFriendTable
   * @param groupId
   * @param originRecordId
   * @return
   */
  private[repository] def _changeGroup(friendGroupFriendTable: TableDefSQLSyntax, groupId: Int, originRecordId: Int) =
    sql"update $friendGroupFriendTable set fgid = ${groupId} where id = ${originRecordId};"
      .executeUpdate()

  /**
   * 查询我的好友的分组
   *
   * @param friendGroupFriendTable
   * @param uId 被移动的好友id
   * @param mId 我的id
   */
  private[repository] def _findUserGroup(
    friendGroupFriendTable: TableDefSQLSyntax,
    friendGroupTable: TableDefSQLSyntax,
    uId: Int,
    mId: Int
  ): StreamReadySQL[Int] =
    sql"select id from $friendGroupFriendTable where fgid in (select id from $friendGroupTable where uid = ${mId}) and uid = ${uId}"
      .list()
      .map(rs => rs.int(1))
      .iterator()

  /**
   * 添加好友操作
   *
   * @param table
   * @param from
   * @param to
   * @return
   */
  private[repository] def _addFriend(table: TableDefSQLSyntax, from: AddFriend, to: AddFriend): SQLUpdate =
    sql"insert into $table(fgid,uid) values(${from.fgid},${to.uid}),(${to.fgid}, ${from.uid});"
      .update()

  //==============================群组成员 SQL实现==============================================

  /**
   * 退出群
   *
   * @param table
   * @param groupMember 群成员对象
   * @return
   */
  private[repository] def _leaveOutGroup(table: TableDefSQLSyntax, groupMember: GroupMember): SQLUpdate =
    sql"delete from $table where gid = ${groupMember.gid} and uid = ${groupMember.uid};".update()

  /**
   * 查询用户编号
   *
   * @param table
   * @param gid
   * @return
   */
  private[repository] def _findGroupMembers(table: TableDefSQLSyntax, gid: Int): StreamReadySQL[Int] =
    sql" select uid from $table where gid = ${gid};"
      .list()
      .map(rs => rs.int(1))
      .iterator()

  /**
   * 添加群成员
   *
   * @param table
   * @param groupMember 群成员对象
   * @return
   */
  private[repository] def _addGroupMember(table: TableDefSQLSyntax, groupMember: GroupMember): SQLUpdate =
    sql"insert into $table(gid,uid) values(${groupMember.gid},${groupMember.uid});"
      .update()

  //==============================申请消息 SQL实现==============================================

  /**
   * 统计未处理的消息
   *
   * @param table
   * @param uid
   * @param agree
   * @return
   */
  private[repository] def _countUnHandMessage(uid: Int, agree: Int): StreamReadySQL[Int] =
    withSQL {
      select(count(am.id))
        .from(AddMessage as am)
        .where(
          sqls.eq(am.toUid, uid) and
            sqls.eq(am.agree, agree)
        )
    }.toList().map(rs => rs.int(1)).iterator()

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
   * @param table
   * @param addMessage 添加好友、群组信息对象
   * @return
   */
  private[repository] def _updateAddMessage(table: TableDefSQLSyntax, addMessage: AddMessage): SQLUpdate =
    sql"update ${table} set agree = ${addMessage.agree} where id = ${addMessage.id}".update()

  /**
   * 添加好友、群组信息请求
   * ON DUPLICATE KEY UPDATE 首先这个语法的目的是为了解决重复性，当数据库中存在某个记录时，执行这条语句会更新它，而不存在这条记录时，会插入它。
   *
   * @param table
   * @param addMessage 添加好友、群组信息对象
   * @return
   */
  private[repository] def _saveAddMessage(table: TableDefSQLSyntax, addMessage: AddMessage): SQLUpdate =
    sql"insert into ${table}(from_uid,to_uid,group_id,remark,agree,type,time) values(${addMessage.fromUid},${addMessage.toUid},${addMessage.groupId},${addMessage.remark},${addMessage.agree},${addMessage.`type`},${addMessage.time}) ON DUPLICATE KEY UPDATE remark=${addMessage.remark},time=${addMessage.time},agree=${addMessage.agree};"
      .update()
}
