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

package org.bitlap.zim.domain.ws

import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }
import io.circe.{ Decoder, Encoder }
import org.bitlap.zim.domain.Mine

/**
 * 同意或拒绝添加群的信息
 */
final case class RefuseOrAgreeMessage(toUid: Int, groupId: Int, messageBoxId: Int, mine: Mine)

object RefuseOrAgreeMessage {

  implicit val decoder: Decoder[RefuseOrAgreeMessage] = deriveDecoder[RefuseOrAgreeMessage]
  implicit val encoder: Encoder[RefuseOrAgreeMessage] = deriveEncoder[RefuseOrAgreeMessage]

}
