package org.bitlap.zim.server.api.endpoint

import akka.stream.scaladsl.Source
import akka.util.ByteString
import io.circe.generic.extras.Configuration
import io.circe.parser.parse
import io.circe.syntax.EncoderOps
import io.circe.{ Decoder, Encoder, HCursor, Json }
import org.bitlap.zim.domain.model.{ GroupList, User }
import org.bitlap.zim.domain._
import org.bitlap.zim.server.api.exception.ZimError
import org.bitlap.zim.server.api.exception.ZimError.BusinessException
import org.bitlap.zim.server.util.LogUtil
import sttp.tapir.Codec.JsonCodec
import sttp.tapir.json.circe._
import zio._
import zio.interop.reactivestreams.streamToPublisher
import zio.stream.ZStream

import scala.collection.mutable.ListBuffer
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
        case _                     => Json.Null
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

  /**
   * @param code 默认错误码
   * @param msg 默认错误提示
   * @tparam T 支持的多元素的类型
   * @return
   */
  private[api] def buildFlowResponse[T <: Product](
    code: Int = SystemConstant.ERROR,
    msg: String = SystemConstant.ERROR_MESSAGE
  ): stream.Stream[Throwable, T] => Future[Either[ZimError, Source[ByteString, Any]]] = respStream => {
    val list = ListBuffer[T]()
    val resp = for {
      _ <- respStream.foreach(u => ZIO.effect(list.append(u)))
      resp = ResultSet[List[T]](data = list.toList, code = code, msg = msg).asJson.noSpaces
      r <- ZStream(resp).map(body => ByteString(body)).toPublisher
    } yield r
    val value = unsafeRun(resp)
    Future.successful(
      Right(Source.fromPublisher(value))
    )
  }

  /**
   * 这些函数本来是没有必要的，因为都使用了ResultSet和Stream，被迫在这里转换
   * @param returnError 是否检验null，如果检验，出现null则返回错误信息
   * @param code 默认错误码
   * @param msg 默认错误提示
   * @tparam T 支持的单元素的类型
   * @return
   */
  private[api] def buildMonoResponse[T <: Product](
    returnError: PartialFunction[T, String] = {
      { case tt: T @unchecked =>
        null
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
      _ <- LogUtil.info(s"buildMonoResponse ret=>$ret result=>$result")
      r <- ZStream.succeed(result).map(body => ByteString(body)).toPublisher
    } yield r
    val value = unsafeRun(resp)
    Future.successful(
      Right(Source.fromPublisher(value))
    )
  }

  private[api] def buildIntMonoResponse(
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
    val value = unsafeRun(resp)
    Future.successful(
      Right(Source.fromPublisher(value))
    )
  }

  private[api] def buildBooleanMonoResponse(
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
    val value = unsafeRun(resp)
    Future.successful(
      Right(Source.fromPublisher(value))
    )
  }

}

object ApiJsonCodec extends ApiJsonCodec
