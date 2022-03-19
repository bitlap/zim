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

package org.bitlap.zim.domain

/**
 * 具有分页功能的结果集
 *
 * @param data 每页数据
 * @param pages  页数
 * @tparam T 分页内容
 * @since 2022年1月1日
 * @author 梦境迷离
 */
final case class ResultPageSet[T](override val data: List[T] = Nil, pages: Int) extends ResultSet(data)
