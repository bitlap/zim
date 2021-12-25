package org.bitlap.zim.api.document

import akka.http.scaladsl.model
import org.bitlap.zim.domain.ZimError
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

  private lazy val defaultDescription = "Unknown Error"
  private[api] lazy val statusDefault: EndpointOutput.StatusMapping[ZimError] =
    statusDefaultMapping(anyJsonBody[ZimError].example(ZimError.BusinessException()).description(defaultDescription))

  private lazy val internalServerErrorDescription: String = model.StatusCodes.InternalServerError.defaultMessage
  private[api] lazy val statusInternalServerError: EndpointOutput.StatusMapping[ZimError.BusinessException] =
    statusMapping(
      StatusCode.InternalServerError,
      anyJsonBody[ZimError.BusinessException].example(ZimError.BusinessException()).description(internalServerErrorDescription)
    )

}