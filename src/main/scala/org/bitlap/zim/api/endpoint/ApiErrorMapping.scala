package org.bitlap.zim.api.endpoint

import akka.http.scaladsl.model
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, HttpResponse }
import akka.http.scaladsl.server.Directives.{ complete, extractUri }
import akka.http.scaladsl.server.ExceptionHandler
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import org.bitlap.zim.configuration.SystemConstant
import org.bitlap.zim.domain.ZimError.BusinessException
import org.bitlap.zim.domain.{ ResultSet, ZimError }
import sttp.model.StatusCode
import sttp.tapir.{ EndpointOutput, _ }
import zio.logging

/**
 * 错误处理
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait ApiErrorMapping extends ApiJsonCodec {

  private[api] lazy val defaultDescription = "Unknown Error"
  private[api] lazy val statusDefault: EndpointOutput.StatusMapping[ZimError] =
    statusDefaultMapping(anyJsonBody[ZimError].example(ZimError.BusinessException()).description(defaultDescription))

  private lazy val internalServerErrorDescription: String = model.StatusCodes.InternalServerError.defaultMessage
  private[api] lazy val statusInternalServerError: EndpointOutput.StatusMapping[ZimError.BusinessException] =
    statusMapping(
      StatusCode.InternalServerError,
      anyJsonBody[ZimError.BusinessException]
        .example(ZimError.BusinessException())
        .description(internalServerErrorDescription)
    )

  private[api] implicit def myExceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: BusinessException =>
      extractUri { uri =>
        logging.log.error(s"Request to $uri could not be handled normally cause by BusinessException")
        val result = ResultSet(code = e.code, msg = if (e.msg != null) e.msg else SystemConstant.ERROR_MESSAGE)
        val resp = HttpEntity(ContentTypes.`application/json`, result.asJson.noSpaces)
        complete(HttpResponse(InternalServerError, entity = resp))
      }
    case _: RuntimeException =>
      extractUri { uri =>
        logging.log.error(s"Request to $uri could not be handled normally cause by RuntimeException")
        val resp = HttpEntity(ContentTypes.`application/json`, ResultSet().asJson.noSpaces)
        complete(HttpResponse(InternalServerError, entity = resp))
      }
  }
}
