package org.bitlap.zim.tapir

import akka.event.slf4j.Logger
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.server.Directives.{ extractUri, getFromResource }
import akka.http.scaladsl.server._
import org.bitlap.zim.domain.ZimError._
import org.bitlap.zim.domain.input.UserSecurity
import sttp.model.StatusCode
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.{ EndpointOutput, _ }

import java.util.Base64
import scala.util.Try

/**
 * 错误处理
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait ApiErrorMapping extends ApiJsonCodec {

  lazy val errorOut: EndpointIO.Body[String, BusinessException] = jsonBody[BusinessException].description("unknown")

  lazy val errorOutVar: Seq[EndpointOutput.OneOfVariant[BusinessException]] = Seq(
    oneOfVariant(StatusCode.Unauthorized, jsonBody[BusinessException].description("unauthorized")),
    oneOfVariant(StatusCode.NotFound, jsonBody[BusinessException].description("not found")),
    oneOfDefaultVariant(jsonBody[BusinessException].description("business exception"))
  )

  // 注意这里是PartialFunction，不能使用`_`匹配
  implicit def customExceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: Unauthorized =>
      extractUri { uri =>
        Logger.root.error(s"Request to $uri could not be handled normally cause by ${e.toString}")
        getFromResource("static/html/403.html")
      }
    case e: BusinessException =>
      extractUri { uri =>
        Logger.root.error(s"Request to $uri could not be handled normally cause by ${e.toString}")
        getFromResource("static/html/500.html")
      }
    case e: Exception =>
      extractUri { uri =>
        Logger.root.error(s"Request to $uri could not be handled normally cause by ${e.toString}")
        getFromResource("static/html/500.html")
      }
  }

  // 处理403 404 500
  implicit def customRejectionHandler: RejectionHandler =
    RejectionHandler
      .newBuilder()
      .handleNotFound {
        getFromResource("static/html/404.html")
      }
      .handle { case MissingCookieRejection(_) =>
        getFromResource("static/html/403.html")
      }
      .handle { case _ =>
        // 所有其他的先使用404，后续改成500
        getFromResource("static/html/404.html")
      }
      .result()

  def extractAuthorization: PartialFunction[HttpHeader, Option[UserSecurity]] = {
    case h: HttpHeader =>
      val secret: String = Try(new String(Base64.getDecoder.decode(h.value()))).getOrElse(null)
      Option(secret).map(f => UserSecurity(f))
    case _ => None
  }
}
