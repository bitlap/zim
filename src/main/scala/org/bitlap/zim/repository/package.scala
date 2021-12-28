package org.bitlap.zim

import org.bitlap.zim.domain.model.User
import scalikejdbc.streams._
import scalikejdbc.{ NoExtractor, SQL, _ }
import sqls.count

/**
 * 用户操作SQL
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
package object repository {

  private[repository] lazy val u: QuerySQLSyntaxProvider[SQLSyntaxSupport[User], User] = User.syntax("u")

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
}
