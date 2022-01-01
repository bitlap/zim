package org.bitlap.zim.api.endpoint

import akka.NotUsed
import akka.stream.scaladsl.Source
import akka.util.ByteString
import io.circe.generic.extras.Configuration
import io.circe.parser.parse
import io.circe.syntax.EncoderOps
import io.circe.{ Decoder, Encoder, HCursor, Json }
import org.bitlap.zim.domain.ZimError.BusinessException
import org.bitlap.zim.domain.model.User
import org.bitlap.zim.domain.{ ResultSet, ZimError }
import sttp.tapir.Codec.JsonCodec
import sttp.tapir.json.circe._
import zio._
import zio.interop.reactivestreams.streamToPublisher
import zio.stream.ZStream

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import org.bitlap.zim.domain.ResultPageSet

/**
 * API的circe解码器
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait ApiJsonCodec extends BootstrapRuntime {

  implicit val customConfig: Configuration = Configuration.default.withDefaults

  // User不能使用`asJson`，会爆栈
  implicit def encodeGeneric[T <: Product]: Encoder[T] = (a: T) => {
    if (a == null) Json.Null
    else {
      a match {
        case u: User => User.encoder(u)
        case _       => Json.Null
      }
    }
  }

  implicit def encodeGenericResultPageSet[T <: Product]: Encoder[ResultPageSet[T]] =
    (a: ResultPageSet[T]) =>
      Json.obj(
        ("data", a.data.asJson),
        ("msg", Json.fromString(a.msg)),
        ("code", Json.fromInt(a.code)),
        ("pages", Json.fromInt(a.pages))
      )

  implicit def encodeGenericResultSet[T <: Product]: Encoder[ResultSet[T]] =
    (a: ResultSet[T]) =>
      Json.obj(
        ("data", a.data.asJson),
        ("msg", Json.fromString(a.msg)),
        ("code", Json.fromInt(a.code))
      )

  implicit def encodeGenericResultSets[T <: Product]: Encoder[ResultSet[List[T]]] =
    (a: ResultSet[List[T]]) =>
      Json.obj(
        ("data", a.data.asJson),
        ("msg", Json.fromString(a.msg)),
        ("code", Json.fromInt(a.code))
      )

  private[api] implicit lazy val stringCodec: JsonCodec[String] =
    implicitly[JsonCodec[Json]].map(json => json.noSpaces)(string =>
      parse(string) match {
        case Left(_)      => throw new RuntimeException("ApiJsonCoded")
        case Right(value) => value
      }
    )

  private[api] implicit def zimErrorCodec[A <: ZimError]: JsonCodec[A] =
    implicitly[JsonCodec[Json]].map(json =>
      json.as[A] match {
        case Left(_)      => throw new RuntimeException("MessageParsingError")
        case Right(value) => value
      }
    )(error => error.asJson)

  implicit def encodeZimError[A <: ZimError]: Encoder[A] = (_: A) => Json.Null

  implicit def decodeZimError[A <: ZimError]: Decoder[A] =
    (c: HCursor) =>
      for {
        code <- c.get[Int]("code")
        msg <- c.get[String]("msg")
      } yield BusinessException(code = code, msg = msg).asInstanceOf[A]

  private[api] def buildFlowResponse[T <: Product]
    : stream.Stream[Throwable, T] => Future[Either[ZimError, Source[ByteString, NotUsed]]] = respStream => {
    val list = ListBuffer[T]()
    val resp = for {
      _ <- respStream.foreach(u => ZIO.effect(list.append(u)))
      resp = ResultSet[List[T]](data = list.toList).asJson.noSpaces
      r <- ZStream(resp).map(body => ByteString(body)).toPublisher
    } yield r
    val value = unsafeRun(resp)
    Future.successful(
      Right(Source.fromPublisher(value))
    )
  }

  private[api] def buildMonoResponse[T <: Product]
    : stream.Stream[Throwable, T] => Future[Either[ZimError, Source[ByteString, NotUsed]]] = respStream => {
    val list = ListBuffer[T]()
    val resp = for {
      _ <- respStream.foreach(u => ZIO.effect(list.append(u)))
      resp = ResultSet[T](data = list.headOption.getOrElse[T](null.asInstanceOf[T])).asJson.noSpaces
      r <- ZStream(resp).map(body => ByteString(body)).toPublisher
    } yield r
    val value = unsafeRun(resp)
    Future.successful(
      Right(Source.fromPublisher(value))
    )
  }
}

object ApiJsonCodec extends ApiJsonCodec
