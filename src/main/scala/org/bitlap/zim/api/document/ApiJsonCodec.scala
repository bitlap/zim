package org.bitlap.zim.api.document

import io.circe.generic.extras.Configuration
import io.circe.parser.parse
import io.circe.syntax._
import io.circe.{ Decoder, Encoder, HCursor, Json }
import org.bitlap.zim.domain.ZimError.BusinessException
import org.bitlap.zim.domain.model.User
import org.bitlap.zim.domain.{ ResultSet, ZimError }
import sttp.tapir.Codec.JsonCodec
import sttp.tapir.json.circe._

trait ApiJsonCodec {

  implicit val customConfig: Configuration = Configuration.default.withDefaults

  implicit val encodeResultSet: Encoder[ResultSet[User]] = (a: ResultSet[User]) => if (a == null) Json.Null else Json.obj(
    ("data", a.data.asJson),
    ("msg", Json.fromString(a.msg)),
    ("code", Json.fromInt(a.code))
  )

  implicit val encodeResultSets: Encoder[ResultSet[List[User]]] = (a: ResultSet[List[User]]) => Json.obj(
    ("data", a.data.asJson),
    ("msg", Json.fromString(a.msg)),
    ("code", Json.fromInt(a.code))
  )

  implicit val encodeUser: Encoder[User] = (a: User) => if (a == null) Json.Null else Json.obj(
    ("id", Json.fromLong(a.id.getOrElse(-1))),
    ("username", Json.fromString(a.name))
  )

  private[api] implicit lazy val stringCodec: JsonCodec[String] =
    implicitly[JsonCodec[Json]].map(json => json.noSpaces)(string =>
      parse(string) match {
        case Left(_) => throw new RuntimeException("ApiJsonCoded")
        case Right(value) => value
      }
    )

  private[api] implicit def zimErrorCodec[A <: ZimError]: JsonCodec[A] =
    implicitly[JsonCodec[Json]].map(json =>
      json.as[A] match {
        case Left(_) => throw new RuntimeException("MessageParsingError")
        case Right(value) => value
      }
    )(error => error.asJson)

  implicit def encodeZimError[A <: ZimError]: Encoder[A] = (_: A) => Json.Null

  implicit def decodeZimError[A <: ZimError]: Decoder[A] =
    (c: HCursor) =>
      for {
        error <- c.get[Int]("code")
      } yield BusinessException(error).asInstanceOf[A]
}

object ApiJsonCodec extends ApiJsonCodec