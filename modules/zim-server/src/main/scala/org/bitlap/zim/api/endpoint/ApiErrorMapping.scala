package org.bitlap.zim.api.endpoint

import akka.event.slf4j.Logger
import akka.http.scaladsl.model
import akka.http.scaladsl.model.StatusCodes.{ InternalServerError, NotFound }
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, HttpResponse }
import akka.http.scaladsl.server.Directives.{ complete, extractUri }
import akka.http.scaladsl.server.{ ExceptionHandler, RejectionHandler }
import io.circe.syntax.EncoderOps
import org.bitlap.zim.domain
import org.bitlap.zim.domain.ZimError.BusinessException
import org.bitlap.zim.domain.{ ResultSet, SystemConstant }
import sttp.model.StatusCode
import sttp.tapir.{ EndpointOutput, _ }

/**
 * 错误处理
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait ApiErrorMapping extends ApiJsonCodec {

  private[api] lazy val defaultDescription = "Unknown Error"
  private[api] lazy val statusDefault: EndpointOutput.StatusMapping[domain.ZimError] =
    statusDefaultMapping(
      anyJsonBody[domain.ZimError].example(domain.ZimError.BusinessException()).description(defaultDescription)
    )

  private lazy val internalServerErrorDescription: String = model.StatusCodes.InternalServerError.defaultMessage
  private[api] lazy val statusInternalServerError: EndpointOutput.StatusMapping[domain.ZimError.BusinessException] =
    statusMapping(
      StatusCode.InternalServerError,
      anyJsonBody[domain.ZimError.BusinessException]
        .example(domain.ZimError.BusinessException())
        .description(internalServerErrorDescription)
    )

  // 注意这里是PartialFunction，不能使用`_`匹配
  private[api] implicit def customExceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: BusinessException =>
      extractUri { uri =>
        Logger.root.error(s"Request to $uri could not be handled normally cause by ${e.getCause}")
        val result = ResultSet(code = e.code, msg = if (e.msg != null) e.msg else SystemConstant.ERROR_MESSAGE)
        val resp = HttpEntity(ContentTypes.`application/json`, result.asJson.noSpaces)
        complete(HttpResponse(InternalServerError, entity = resp))
      }
    case e: Exception =>
      extractUri { uri =>
        Logger.root.error(s"Request to $uri could not be handled normally cause by ${e.getCause}")
        val resp = HttpEntity(
          ContentTypes.`application/json`,
          ResultSet(code = SystemConstant.ERROR, msg = SystemConstant.ERROR_MESSAGE).asJson.noSpaces
        )
        complete(HttpResponse(InternalServerError, entity = resp))
      }
  }

  // 处理404
  implicit def customRejectionHandler: RejectionHandler =
    RejectionHandler
      .newBuilder()
      .handleNotFound {
        val result = ResultSet(code = 404)
        val resp = HttpEntity(ContentTypes.`application/json`, result.asJson.noSpaces)
        complete(HttpResponse(NotFound, entity = resp))
      }
      .result()
}
