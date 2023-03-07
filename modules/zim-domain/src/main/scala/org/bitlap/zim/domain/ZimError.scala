/*
 * Copyright 2023 bitlap
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

package org.bitlap.zim.domain

/** 系统异常
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 1.0
 */
sealed trait ZimError extends Throwable with Product {
  val msg: String
  val code: Int
}

object ZimError {

  case class BusinessException(
    override val code: Int = SystemConstant.ERROR,
    override val msg: String = SystemConstant.ERROR_MESSAGE
  ) extends ZimError

  case class Unauthorized(
    override val code: Int = SystemConstant.ERROR,
    override val msg: String = SystemConstant.NOT_LOGIN
  ) extends ZimError
}
