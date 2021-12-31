package org.bitlap.zim.api.endpoint

import akka.stream.scaladsl.Source
import akka.util.ByteString
import org.bitlap.zim.domain.ZimError
import org.bitlap.zim.domain.model.User
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir._

/**
 * 用户接口的端点
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait UserEndpoint extends ApiErrorMapping {

  // API 最前缀path
  private[api] lazy val queryResource: EndpointInput[Unit] = "user"
  // API  资源描述
  private[api] lazy val userDescriptionGetResource: String = "User Endpoint"

  //================================================用户API定义===============================================================
  private[api] lazy val userGetOneEndpoint: Endpoint[Long, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.get
      .in(queryResource / "getOne" / query[Long]("id").example(10086L).description("query parameter"))
      .name("查询一个用户")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[User].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val userGetAllEndpoint: Endpoint[Unit, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.get
      .in(queryResource / "getAll")
      .name("查询所有用户")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[User].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))
}

object UserEndpoint extends UserEndpoint
