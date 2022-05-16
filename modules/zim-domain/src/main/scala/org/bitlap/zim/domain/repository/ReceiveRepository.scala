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

package org.bitlap.zim.domain.repository
import zio.stream

/** 消息的操作定义
 *
 *  @author
 *    LittleTear
 *  @since 2021/12/30
 *  @version 1.0
 */
trait ReceiveRepository[T] extends BaseRepository[T] {

  def saveMessage(receive: T): stream.Stream[Throwable, Int]

  def findOffLineMessage(uid: Int, status: Int): stream.Stream[Throwable, T]

  def findHistoryMessage(uid: Option[Int], mid: Option[Int], typ: Option[String]): stream.Stream[Throwable, T]

  def countHistoryMessage(uid: Option[Int], mid: Option[Int], typ: Option[String]): stream.Stream[Throwable, Int]

  def readMessage(mine: Int, to: Int, typ: String): stream.Stream[Throwable, Int]

}
