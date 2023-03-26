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

package org.bitlap.zim.api

import io.circe.Json
import io.circe.parser._
import org.bitlap.zim.ZimBuildInfo
import sttp.model.StatusCode
import sttp.tapir.Codec.JsonCodec
import sttp.tapir._
import sttp.tapir.json.circe._

/** Actuator的端点
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 1.0
 */
trait ActuatorEndpoint {

  private[api] type HealthInfo = Map[String, Any]
  private[api] lazy val healthResource: String            = "health"
  private[api] lazy val healthNameResource: String        = "health-resource"
  private[api] lazy val healthDescriptionResource: String = "Zim Service Health Check Endpoint"

  lazy val healthEndpoint: PublicEndpoint[Unit, StatusCode, HealthInfo, Any] =
    ApiEndpoint.baseEndpoint.get
      .in(healthResource)
      .name(healthNameResource)
      .description(healthDescriptionResource)
      .out(customCodecJsonBody[HealthInfo].example(ZimBuildInfo.toMap))
      .errorOut(statusCode)

  private[api] implicit lazy val buildInfoCodec: JsonCodec[HealthInfo] =
    implicitly[JsonCodec[Json]].map(_ => ZimBuildInfo.toMap)(_ =>
      parse(ZimBuildInfo.toJson) match {
        case Left(_)      => throw new RuntimeException("health doesn't work")
        case Right(value) => value
      }
    )

}

object ActuatorEndpoint extends ActuatorEndpoint
