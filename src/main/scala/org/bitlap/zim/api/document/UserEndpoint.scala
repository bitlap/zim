package org.bitlap.zim.api.document

import akka.stream.scaladsl.Source
import akka.util.ByteString
import org.bitlap.zim.domain.ZimError
import org.bitlap.zim.domain.model.User
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir._

/**
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait UserEndpoint extends ApiErrorMapping {

  private[api] lazy val queryResource: EndpointInput[Unit] = "user"
  private[api] lazy val queryParameter: EndpointInput.Query[Long] = query[Long]("id")
    .example(10086L).description("query parameter")

  private[api] lazy val queryUserResource: EndpointInput[Long] = queryResource / "getOne" / queryParameter
  private[api] lazy val queriesDescriptionGetResource: String = "Queries Get Endpoint"
  private[api] lazy val queriesNameGetResource: String = "queries-get-resource"

  private[api] lazy val userGetOneEndpoint: Endpoint[Long, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint
      .in(queryUserResource)
      .name(queriesNameGetResource)
      .description(queriesDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[User].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))


  private[api] lazy val queriesResource: EndpointInput[Unit] = queryResource / "getAll"
  private[api] lazy val userGetAllEndpoint: Endpoint[Unit, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint
      .in(queriesResource)
      .name(queriesNameGetResource)
      .description(queriesDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[User].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))
}

object UserEndpoint extends UserEndpoint