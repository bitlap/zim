/*
 * Copyright 2022 bitlap
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

package org.bitlap.zim.server.service.ws
import akka.{ Done, NotUsed }
import akka.actor.{ ActorRef, Status }
import akka.actor.typed.DispatcherSelector
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.model.ws.{ Message, TextMessage }
import akka.stream.{ CompletionStrategy, Materializer, OverflowStrategy }
import akka.stream.scaladsl.{ Flow, Keep, Sink, Source }
import io.circe.syntax.EncoderOps
import org.bitlap.zim.domain
import org.bitlap.zim.domain.{ Message => IMMessage, SystemConstant }
import org.bitlap.zim.domain.model.User
import org.bitlap.zim.domain.ws._
import org.bitlap.zim.domain.ws.protocol._
import org.bitlap.zim.server.actor.akka.WsMessageForwardBehavior
import org.bitlap.zim.server.configuration.{
  AkkaActorSystemConfiguration,
  ZimServiceConfiguration,
  ZioActorSystemConfiguration
}
import org.bitlap.zim.server.configuration.ApplicationConfiguration.ZApplicationConfiguration
import org.bitlap.zim.server.service.RedisCache
import org.bitlap.zim.server.zioRuntime
import org.reactivestreams.Publisher
import zio._
import zio.actors.akka.AkkaTypedActor

import java.util.concurrent.ConcurrentHashMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.jdk.CollectionConverters.ConcurrentMapHasAsScala

/** @author
 *    梦境迷离
 *  @version 2.0, 2022/1/11
 */
trait WsService {

  def sendMessage(message: domain.Message): Task[Unit]

  def agreeAddGroup(msg: domain.Message): Task[Unit]

  def refuseAddGroup(msg: domain.Message): Task[Unit]

  def refuseAddFriend(messageBoxId: Int, username: String, to: Int): Task[Boolean]

  def deleteGroup(master: User, groupname: String, gid: Int, uid: Int): Task[Unit]

  def removeFriend(uId: Int, friendId: Int): Task[Unit]

  def addGroup(uId: Int, message: domain.Message): Task[Unit]

  def addFriend(uId: Int, message: domain.Message): Task[Unit]

  def countUnHandMessage(uId: Int): Task[Map[String, String]]

  def checkOnline(message: domain.Message): Task[Map[String, String]]

  def sendMessage(message: String, actorRef: ActorRef): Task[Unit]

  def changeOnline(uId: Int, status: String): Task[Boolean]

  def readOfflineMessage(message: domain.Message): Task[Unit]

  def getConnections: Task[Int]
}

object WsService extends ZimServiceConfiguration {

  private lazy val live: URLayer[ZApplicationConfiguration, Has[WsService]] = (r => WsServiceLive(r)).toLayer

  // 非最佳实践
  private lazy val wsLayer: ULayer[Has[WsService]] = applicationConfigurationLayer >>> WsService.live

  final lazy val actorRefSessions: ConcurrentHashMap[Integer, ActorRef] = new ConcurrentHashMap[Integer, ActorRef]

  private val customDispatcher = DispatcherSelector.fromConfig("custom-dispatcher")

  // 非最佳实践，为了使用unsafeRun，不能把environment传递到最外层，这里直接provideLayer
  def sendMessage(message: domain.Message): Task[Unit] =
    ZIO.serviceWith[WsService](_.sendMessage(message)).provideLayer(wsLayer)

  def agreeAddGroup(msg: domain.Message): Task[Unit] =
    ZIO.serviceWith[WsService](_.agreeAddGroup(msg)).provideLayer(wsLayer)

  def refuseAddGroup(msg: domain.Message): Task[Unit] =
    ZIO.serviceWith[WsService](_.refuseAddGroup(msg)).provideLayer(wsLayer)

  def refuseAddFriend(messageBoxId: Int, username: String, to: Int): Task[Boolean] =
    ZIO.serviceWith[WsService](_.refuseAddFriend(messageBoxId, username, to)).provideLayer(wsLayer)

  def deleteGroup(master: domain.model.User, groupname: String, gid: Int, uid: Int): Task[Unit] =
    ZIO.serviceWith[WsService](_.deleteGroup(master, groupname, gid, uid)).provideLayer(wsLayer)

  def removeFriend(uId: Int, friendId: Int): Task[Unit] =
    ZIO.serviceWith[WsService](_.removeFriend(uId, friendId)).provideLayer(wsLayer)

  def addGroup(uId: Int, message: domain.Message): Task[Unit] =
    ZIO.serviceWith[WsService](_.addGroup(uId, message)).provideLayer(wsLayer)

  def addFriend(uId: Int, message: domain.Message): Task[Unit] =
    ZIO.serviceWith[WsService](_.addFriend(uId, message)).provideLayer(wsLayer)

  def countUnHandMessage(uId: Int): Task[Map[String, String]] =
    ZIO.serviceWith[WsService](_.countUnHandMessage(uId)).provideLayer(wsLayer)

  def checkOnline(message: domain.Message): Task[Map[String, String]] =
    ZIO.serviceWith[WsService](_.checkOnline(message)).provideLayer(wsLayer)

  def sendMessage(message: String, actorRef: ActorRef): Task[Unit] =
    ZIO.serviceWith[WsService](_.sendMessage(message, actorRef)).provideLayer(wsLayer)

  def changeOnline(uId: Int, status: String): Task[Boolean] =
    ZIO.serviceWith[WsService](_.changeOnline(uId, status)).provideLayer(wsLayer)

  def readOfflineMessage(message: domain.Message): Task[Unit] =
    ZIO.serviceWith[WsService](_.readOfflineMessage(message)).provideLayer(wsLayer)

  def getConnections: Task[Int] =
    ZIO.serviceWith[WsService](_.getConnections).provideLayer(wsLayer)

  private final lazy val wsConnections: ConcurrentHashMap[Integer, ActorRef] =
    WsService.actorRefSessions

  private def changeStatus(uId: Int, status: String): Task[Unit] =
    for {
      _ <-
        if (status == SystemConstant.status.ONLINE) {
          RedisCache.setSet(SystemConstant.ONLINE_USER, s"$uId")
        } else {
          RedisCache.removeSetValue(SystemConstant.ONLINE_USER, s"$uId")
        }
      _ <- RedisCache.setSet(SystemConstant.ONLINE_USER, s"$uId")
      msg = IMMessage(
        `type` = Protocol.changOnline.stringify,
        mine = null,
        to = null,
        msg =
          if (status == SystemConstant.status.ONLINE) SystemConstant.status.ONLINE
          else SystemConstant.status.HIDE
      ).asJson.noSpaces
      akkaSystem <- AkkaActorSystemConfiguration.make
      akkaTypedActor = akkaSystem.spawn(
        WsMessageForwardBehavior.apply(),
        Constants.WS_MESSAGE_FORWARD_ACTOR,
        customDispatcher
      )
      akkaActor <- AkkaTypedActor.make(akkaTypedActor)
      _         <- akkaActor ! TransmitMessageProxy(uId, msg, None)
    } yield ()

  /** Connection processing and message processing
   *
   *  @param uId
   *  @return
   */
  def openConnection(
    uId: Int
  )(implicit m: Materializer): ZIO[Any, Throwable, Flow[Message, String, NotUsed]] = {
    // closeConnection(uId)
    val (actorRef: akka.actor.ActorRef, publisher: Publisher[String]) =
      Source
        .actorRef(
          { case akka.actor.Status.Success(s: CompletionStrategy) =>
            s
          },
          { case akka.actor.Status.Failure(cause: Throwable) =>
            cause
          },
          16,
          OverflowStrategy.fail
        )
        .map(TextMessage.Strict)
        .map(_.text)
        .toMat(Sink.asPublisher(true))(Keep.both)
        .run()
    for {
      // we use akka-http, it must have akka-actor
      // if we use zio-actors in ws, because zio only support typed actor,
      // we need forward to akka typed actor for sending message to akka classic actor which return by akka http.
      // so we only use akka here
      akkaSystem <- AkkaActorSystemConfiguration.make
      akkaTypedActor = akkaSystem.spawn(WsMessageForwardBehavior(), Constants.WS_MESSAGE_FORWARD_ACTOR)
      _         <- changeStatus(uId, SystemConstant.status.ONLINE)
      akkaActor <- AkkaTypedActor.make(akkaTypedActor)
      in = Flow[Message]
        .watchTermination()((_, ft) => ft.foreach(_ => closeConnection(uId)))
        .mapConcat {
          case TextMessage.Strict(message) =>
            zioRuntime.unsafeRun(akkaActor ! TransmitMessageProxy(uId, message, Some(actorRef)))
            Nil
          case _ => Nil
        }
        .to(Sink.ignore)
      _ = wsConnections.put(uId, actorRef)
    } yield Flow.fromSinkAndSource(in, Source.fromPublisher(publisher))
  }

  /** close websocket
   *
   *  @param id
   */
  def closeConnection(id: Int): Unit =
    wsConnections.asScala.get(id).foreach { ar =>
      wsConnections.remove(id)
      zioRuntime.unsafeRunAsync {
        changeStatus(id, SystemConstant.status.HIDE)
          .flatMap(_ => userStatusChangeByServer(id, SystemConstant.status.HIDE))
      }(ex => ex.unit)

      ar ! Status.Success(Done)
    }

  def userStatusChangeByServer(uId: Int, status: String): ZIO[Any, Throwable, Unit] =
    ZioActorSystemConfiguration.userStatusActor.flatMap(actor => actor ! UserStatusChangeMessage(uId, status))
}
