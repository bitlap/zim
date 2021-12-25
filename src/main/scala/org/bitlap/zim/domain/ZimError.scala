package org.bitlap.zim.domain

import org.bitlap.zim.configuration.SystemConstant

/**
 * 系统异常
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
sealed trait ZimError extends Throwable with Product {
  val msg: String
  val code: Int
}

object ZimError {

  case class BusinessException(override val code: Int = SystemConstant.ERROR, override val msg: String = SystemConstant.ERROR_MESSAGE) extends ZimError

}