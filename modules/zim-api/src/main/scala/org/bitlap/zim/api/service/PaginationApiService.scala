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

package org.bitlap.zim.api.service

import org.bitlap.zim.domain._
import org.bitlap.zim.domain.model._

/** 直接提供给endpoint使用 对userService做一定的包装
 *
 *  @author
 *    梦境迷离
 *  @since 2022/8/18
 *  @version 2.0
 */
trait PaginationApiService[F[_]] {

  /** 分页接口 内存分页
   *
   *  TODO 没有使用数据的offset
   *  @param id
   *  @param `type`
   *  @param page
   *  @param mid
   *  @return
   */
  def chatLog(id: Int, `type`: String, page: Int, mid: Int): F[ResultPageSet[ChatHistory]]

  def findAddInfo(uid: Int, page: Int): F[ResultPageSet[AddInfo]]

  def findUsers(name: Option[String], sex: Option[Int], page: Int): F[ResultPageSet[User]]

  def findGroups(name: Option[String], page: Int): F[ResultPageSet[GroupList]]

  def findMyGroups(createId: Int, page: Int): F[ResultPageSet[GroupList]]

}
