package org.bitlap.zim.domain

/**
 * 结果集
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
