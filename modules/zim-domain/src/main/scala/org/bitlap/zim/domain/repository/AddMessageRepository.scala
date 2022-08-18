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

import org.bitlap.zim.domain.model.AddMessage

/** @author
 *    梦境迷离
 *  @since 2022/1/15
 *  @version 1.0
 */
trait AddMessageRepository[F[_]] extends BaseRepository[F, AddMessage] {

  def countUnHandMessage(uid: Int, agree: Option[Int]): F[Int]

  def findAddInfo(uid: Int): F[AddMessage]

  def updateAgree(id: Int, agree: Int): F[Int]

  def saveAddMessage(addMessage: AddMessage): F[Int]
}
