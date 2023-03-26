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

import akka.event.slf4j._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import org.bitlap.zim.domain.ZimError._
import sttp.model._
import sttp.tapir._
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe.jsonBody

/** 错误处理
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 1.0
 */
trait ApiErrorMapping extends ApiJsonCodec {

  protected lazy val errorOut: EndpointIO.Body[String, BusinessException] =
    jsonBody[BusinessException].description("unknown")

  lazy val errorOutVar: Seq[EndpointOutput.OneOfVariant[BusinessException]] = Seq(
    oneOfVariant(StatusCode.Unauthorized, jsonBody[BusinessException].description("unauthorized")),
    oneOfVariant(StatusCode.NotFound, jsonBody[BusinessException].description("not found")),
    oneOfDefaultVariant(jsonBody[BusinessException].description("business exception"))
  )

  // 注意这里是PartialFunction，不能使用`_`匹配
  implicit val customExceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: Unauthorized =>
      extractUri { uri =>
        Logger.root.error(s"403: Request to $uri could not be handled normally cause by ${e.getLocalizedMessage}", e)
        getFromResource("static/html/403.html")
      }
    case e: Exception =>
      extractUri { uri =>
        Logger.root.error(s"500: Request to $uri could not be handled normally cause by ${e.getLocalizedMessage}", e)
        getFromResource("static/html/500.html")
      }
  }

  // 处理403 404 500
  implicit val customRejectionHandler: RejectionHandler =
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
}
