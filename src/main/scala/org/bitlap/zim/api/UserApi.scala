package org.bitlap.zim.api


import akka.NotUsed
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, HttpResponse }
import akka.http.scaladsl.server.Directives.{ complete, _ }
import akka.http.scaladsl.server.{ ExceptionHandler, Route }
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import org.bitlap.zim.api.document.{ ApiErrorMapping, ApiJsonCodec, UserEndpoint }
import org.bitlap.zim.application.UserApplication
import org.bitlap.zim.application.UserService.ZUserApplication
import org.bitlap.zim.configuration.SystemConstant
import org.bitlap.zim.domain.ZimError.BusinessException
import org.bitlap.zim.domain.model.User
import org.bitlap.zim.domain.{ ResultSet, ZimError }
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import zio._
import zio.interop.reactivestreams.streamToPublisher
import zio.stream.ZStream

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

/**
 * 用户API
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
final class UserApi(userApplication: UserApplication)(implicit materializer: Materializer) extends ApiJsonCodec with ApiErrorMapping with BootstrapRuntime {

  private implicit def myExceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: BusinessException =>
      extractUri { uri =>
        logging.log.error(s"Request to $uri could not be handled normally cause by BusinessException")
        val result = ResultSet(code = e.code, msg = if (e.msg != null) e.msg else SystemConstant.ERROR_MESSAGE)
        val resp = HttpEntity(ContentTypes.`application/json`, result.asJson.noSpaces)
        complete(HttpResponse(InternalServerError, entity = resp))
      }
    case _: RuntimeException =>
      extractUri { uri =>
        logging.log.error(s"Request to $uri could not be handled normally cause by RuntimeException")
        val resp = HttpEntity(ContentTypes.`application/json`, ResultSet().asJson.noSpaces)
        complete(HttpResponse(InternalServerError, entity = resp))
      }
  }

  val route: Route = Route.seal(userGetAllRoute ~ userGetRoute)

  lazy val userGetRoute: Route = AkkaHttpServerInterpreter.toRoute(UserEndpoint.userGetOneEndpoint) {
    id =>

      val userStream = userApplication.findById(id)
      val resp = userStream.mapError({
        case e: BusinessException => BusinessException(SystemConstant.ERROR, e.msg)
        case e: Exception => e
      })

      buildOneResponse(resp)
  }

  lazy val userGetAllRoute: Route = AkkaHttpServerInterpreter.toRoute(UserEndpoint.userGetAllEndpoint) {
    _ =>
      val userStream = userApplication.findAll()
      val resp = userStream.mapError[ZimError]({
        case e: BusinessException => BusinessException(SystemConstant.ERROR, e.msg)
        case e: Exception => BusinessException.apply(SystemConstant.ERROR, e.getMessage)
      })
      buildResponse(resp)
  }

  private def buildResponse: stream.Stream[Throwable, User] => Future[Either[ZimError, Source[ByteString, NotUsed]]] = respStream => {
    val list = ListBuffer[User]()
    val resp = for {
      _ <- respStream.foreach(u => ZIO.effect(list.append(u)))
      resp = ResultSet[List[User]](data = list.toList).asJson.noSpaces
      r <- ZStream(resp).map(body => ByteString(body)).toPublisher
    } yield r
    val value = unsafeRun(resp)
    Future.successful(
      Right(Source.fromPublisher(value))
    )
  }

  private def buildOneResponse: stream.Stream[Throwable, User] => Future[Either[ZimError, Source[ByteString, NotUsed]]] = respStream => {
    val list = ListBuffer[User]()
    val resp = for {
      _ <- respStream.foreach(u => ZIO.effect(list.append(u)))
      resp = ResultSet[User](data = list.headOption.orNull).asJson.noSpaces
      r <- ZStream(resp).map(body => ByteString(body)).toPublisher
    } yield r
    val value = unsafeRun(resp)
    Future.successful(
      Right(Source.fromPublisher(value))
    )
  }
}

object UserApi {

  def apply(app: UserApplication)(implicit materializer: Materializer): UserApi = new UserApi(app)

  type ZUserApi = Has[UserApi]

  val route: ZIO[ZUserApi, Nothing, Route] =
    ZIO.access[ZUserApi](_.get.route)

  val live: ZLayer[ZUserApplication with Has[Materializer], Nothing, ZUserApi] =
    ZLayer.fromServices[UserApplication, Materializer, UserApi]((app, mat) => UserApi(app)(mat))

}