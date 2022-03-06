/*
 * Copyright 2021 bitlap
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

package org.bitlap.zim.tapir

import akka.stream.scaladsl.Source
import akka.util.ByteString
import io.circe.generic.extras.Configuration
import io.circe.parser.parse
import io.circe.syntax.EncoderOps
import io.circe.{ Decoder, Encoder, HCursor, Json }
import org.bitlap.zim.domain.ZimError.BusinessException
import org.bitlap.zim.domain._
import org.bitlap.zim.domain.model.{ GroupList, User }
import sttp.tapir.Codec.JsonCodec
import sttp.tapir.json.circe._
import sttp.tapir.{ Schema, SchemaType }
import zio._
import zio.interop.reactivestreams.streamToPublisher
import zio.stream.ZStream
import org.bitlap.zim.domain.model.Receive
import org.bitlap.zim.domain.input.UserSecurity.UserSecurityInfo

import scala.concurrent.Future

/**
 * API的circe解码器
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait ApiJsonCodec extends BootstrapRuntime {

  implicit val customConfig: Configuration = Configuration.default.withDefaults

  implicit val schemaForZimErrorInfo: Schema[ZimError] =
    Schema[ZimError](SchemaType.SProduct(Nil), Some(Schema.SName("ZimError")))

  // User不能使用`asJson`，会爆栈
  implicit def encodeGeneric[T <: Product]: Encoder[T] = (a: T) => {
    if (a == null) Json.Null
    else {
      a match {
        case u: FriendAndGroupInfo => FriendAndGroupInfo.encoder(u)
        case u: GroupList          => GroupList.encoder(u)
        case u: FriendList         => FriendList.encoder(u)
        case u: User               => User.encoder(u)
        case u: Message            => Message.encoder(u)
        case u: Mine               => Mine.encoder(u)
        case u: To                 => To.encoder(u)
        case u: ChatHistory        => ChatHistory.encoder(u)
        case u: AddInfo            => AddInfo.encoder(u)
        case u: UploadResult       => UploadResult.encoder(u)
        case u: Receive            => Receive.encoder(u)
        case u: UserSecurityInfo   => UserSecurityInfo.encoder(u)
        case _                     => Json.Null
      }
    }
  }

  implicit def encodeGenericResultSet[T <: Product]: Encoder[ResultSet[T]] =
    (a: ResultSet[T]) =>
      Json.obj(
        ("data", a.data.asJson),
        ("msg", Json.fromString(a.msg)),
        ("code", Json.fromInt(a.code))
      )

  implicit def encodeBooleanResultSet: Encoder[ResultSet[Boolean]] =
    (a: ResultSet[Boolean]) =>
      Json.obj(
        ("data", a.data.asJson),
        ("msg", Json.fromString(a.msg)),
        ("code", Json.fromInt(a.code))
      )

  implicit def encodeIntResultSet: Encoder[ResultSet[Int]] =
    (a: ResultSet[Int]) =>
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

  implicit def encodeGenericResultPageSets[T <: Product]: Encoder[ResultPageSet[T]] =
    (a: ResultPageSet[T]) =>
      Json.obj(
        ("data", a.data.asJson),
        ("msg", Json.fromString(a.msg)),
        ("code", Json.fromInt(a.code)),
        ("pages", Json.fromInt(a.pages))
      )

  implicit lazy val stringCodec: JsonCodec[String] =
    implicitly[JsonCodec[Json]].map(json => json.noSpaces)(string =>
      parse(string) match {
        case Left(_)      => throw new RuntimeException("ApiJsonCoded")
        case Right(value) => value
      }
    )

  implicit def zimErrorCodec[A <: ZimError]: JsonCodec[A] =
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

  /**
   * @tparam T 支持的多元素的类型
   * @return
   */
  def buildFlowResponse[T <: Product]
    : stream.Stream[Throwable, T] => Future[Either[ZimError, Source[ByteString, Any]]] = respStream => {
    val resp = for {
      list <- respStream.runCollect
      resp = ResultSet[List[T]](data = list.toList).asJson.noSpaces
      r <- ZStream(resp).map(body => ByteString(body)).toPublisher
    } yield r
    Future.successful(
      Right(Source.fromPublisher(unsafeRun(resp)))
    )
  }

  /**
   * 这些函数本来是没有必要的，因为都使用了ResultSet和Stream，被迫在这里转换
   * @param returnError 是否检验null，如果检验，出现null则返回错误信息
   * @tparam T 支持的单元素的类型
   * @return
   */
  def buildMonoResponse[T <: Product](
    returnError: PartialFunction[T, String] = {
      {
        case tt: T @unchecked => null
        case t if t == null   => null
      }: PartialFunction[T, String]
    }
  ): stream.Stream[Throwable, T] => Future[Either[ZimError, Source[ByteString, Any]]] = respStream => {
    val resp = for {
      ret <- respStream.runHead.map(_.getOrElse(null.asInstanceOf[T]))
      result = (
        if (returnError(ret) != null)
          ResultSet[T](data = null.asInstanceOf[T], code = SystemConstant.ERROR, msg = returnError(ret))
        else ResultSet[T](data = ret)
      ).asJson.noSpaces
      r <- ZStream.succeed(result).map(body => ByteString(body)).toPublisher
    } yield r
    Future.successful(
      Right(Source.fromPublisher(unsafeRun(resp)))
    )
  }

  def buildIntMonoResponse(
    returnError: Boolean = true,
    code: Int = SystemConstant.ERROR,
    msg: String = SystemConstant.ERROR_MESSAGE
  ): stream.Stream[Throwable, Int] => Future[Either[ZimError, Source[ByteString, Any]]] = respStream => {
    val resp = for {
      resp <- respStream.runHead.map(_.getOrElse(0))
      result = (if (resp < 1 && returnError) ResultSet(data = resp, code = code, msg = msg)
                else ResultSet(data = resp)).asJson.noSpaces
      r <- ZStream.succeed(result).map(body => ByteString(body)).toPublisher
    } yield r
    Future.successful(
      Right(Source.fromPublisher(unsafeRun(resp)))
    )
  }

  def buildBooleanMonoResponse(
    returnError: Boolean = true,
    code: Int = SystemConstant.ERROR,
    msg: String = SystemConstant.ERROR_MESSAGE
  ): stream.Stream[Throwable, Boolean] => Future[Either[ZimError, Source[ByteString, Any]]] = respStream => {
    val resp = for {
      resp <- respStream.runHead.map(_.getOrElse(false))
      result = (if (!resp && returnError) ResultSet(data = resp, code = code, msg = msg)
                else ResultSet(data = resp)).asJson.noSpaces
      r <- ZStream.succeed(result).map(body => ByteString(body)).toPublisher
    } yield r
    Future.successful(
      Right(Source.fromPublisher(unsafeRun(resp)))
    )
  }

  def buildPagesResponse[T <: Product]
    : IO[Throwable, ResultPageSet[T]] => Future[Either[ZimError, Source[ByteString, Any]]] =
    respIO => {
      val resp = for {
        resp <- respIO
        r <- ZStream.succeed(resp.asJson.noSpaces).map(body => ByteString(body)).toPublisher
      } yield r
      Future.successful(
        Right(Source.fromPublisher(unsafeRun(resp)))
      )
    }

}

object ApiJsonCodec extends ApiJsonCodec
