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

/** 结果集
 *
 *  @param code
 *    状态，0表示成功，其他表示失败
 *  @param msg
 *    额外信息
 *  @since 2021年12月25日
 *  @author
 *    梦境迷离
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
