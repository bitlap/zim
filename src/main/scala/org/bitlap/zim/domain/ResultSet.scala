package org.bitlap.zim.domain

import org.bitlap.zim.configuration.SystemConstant

/** 结果集
 *
 * @param code 状态，0表示成功，其他表示失败
 * @param msg  额外信息
 * @since 2021年12月25日
 * @author 梦境迷离
 */
class ResultSet[T](
  val data: T,
  val code: Int = SystemConstant.SUCCESS,
  val msg: String = SystemConstant.SUCCESS_MESSAGE
)

object ResultSet {
  def apply[T](
    data: T = null,
    code: Int = SystemConstant.SUCCESS,
    msg: String = SystemConstant.SUCCESS_MESSAGE
  ): ResultSet[T] = new ResultSet(data, code, msg)
}

class ResultSets[T](
  val data: List[T] = Nil,
  val code: Int = SystemConstant.SUCCESS,
  val msg: String = SystemConstant.SUCCESS_MESSAGE
)

object ResultSets {
  def apply[T](
    data: List[T] = Nil,
    code: Int = SystemConstant.SUCCESS,
    msg: String = SystemConstant.SUCCESS_MESSAGE
  ): ResultSets[T] = new ResultSets(data, code, msg)
}

case class ResultPageSet[T](override val data: T, pages: Int) extends ResultSet(data)
