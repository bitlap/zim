package org.bitlap.zim.tapir

import io.circe.Json
import io.circe.parser._
import org.bitlap.zim.ZimBuildInfo
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.Codec.JsonCodec
import sttp.tapir.json.circe._

/**
 * Actuator的端点
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait ActuatorEndpoint {

  type HealthInfo = Map[String, Any]
  private[tapir] lazy val healthResource: String = "health"
  private[tapir] lazy val healthNameResource: String = "health-resource"
  private[tapir] lazy val healthDescriptionResource: String = "Zim Service Health Check Endpoint"

  lazy val healthEndpoint: PublicEndpoint[Unit, StatusCode, HealthInfo, Any] =
    ApiEndpoint.baseEndpoint.get
      .in(healthResource)
      .name(healthNameResource)
      .description(healthDescriptionResource)
      .out(customJsonBody[HealthInfo].example(ZimBuildInfo.toMap))
      .errorOut(statusCode)

  private[tapir] implicit lazy val buildInfoCodec: JsonCodec[HealthInfo] =
    implicitly[JsonCodec[Json]].map(_ => ZimBuildInfo.toMap)(_ =>
      parse(ZimBuildInfo.toJson) match {
        case Left(_)      => throw new RuntimeException("health doesn't work")
        case Right(value) => value
      }
    )

}

object ActuatorEndpoint extends ActuatorEndpoint
