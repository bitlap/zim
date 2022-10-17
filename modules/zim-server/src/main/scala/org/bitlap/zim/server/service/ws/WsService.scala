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
import _root_.io.circe.syntax.EncoderOps
import akka._
import akka.actor.typed._
import akka.actor.typed.scaladsl.adapter._
import akka.actor.{ typed, ActorRef, Status }
import akka.http.scaladsl.model.ws._
import akka.stream._
import akka.stream.scaladsl.{ Flow, Keep, Sink, Source }
import org.bitlap.zim._
import org.bitlap.zim.api.service.WsService
import org.bitlap.zim.domain.ws.protocol._
import org.bitlap.zim.domain.{ Message => IMMessage, SystemConstant }
import org.bitlap.zim.server.actor.akka._
import org.bitlap.zim.server.configuration._
import org.bitlap.zim.server.service._
import org.reactivestreams._
import zio._
import zio.actors.akka.AkkaTypedActor

import java.util.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.jdk.CollectionConverters.ConcurrentMapHasAsScala

/** @author
 *    梦境迷离
 *  @version 2.0, 2022/1/11
 */
object WsService extends ZimServiceConfiguration {

  private lazy val wsLayer: ULayer[WsService[Task]] = ZLayer.make[WsService[Task]](
    applicationConfigurationLayer,
    ZLayer(ZIO.service[ApplicationConfiguration].map(WsServiceLive.apply))
  )

  final lazy val actorRefSessions: ConcurrentHashMap[Integer, ActorRef] = new ConcurrentHashMap[Integer, ActorRef]

  private val customDispatcher = DispatcherSelector.fromConfig("custom-dispatcher")

  // 非最佳实践，为了使用unsafeRun，不能把environment传递到最外层，这里直接provideLayer
  def sendMessage(message: domain.Message): Task[Unit] =
    ZIO.serviceWithZIO[WsService[Task]](_.sendMessage(message)).provideLayer(wsLayer)

  def agreeAddGroup(msg: domain.Message): Task[Unit] =
    ZIO.serviceWithZIO[WsService[Task]](_.agreeAddGroup(msg)).provideLayer(wsLayer)

  def refuseAddGroup(msg: domain.Message): Task[Unit] =
    ZIO.serviceWithZIO[WsService[Task]](_.refuseAddGroup(msg)).provideLayer(wsLayer)

  def refuseAddFriend(messageBoxId: Int, username: String, to: Int): Task[Boolean] =
    ZIO.serviceWithZIO[WsService[Task]](_.refuseAddFriend(messageBoxId, username, to)).provideLayer(wsLayer)

  def deleteGroup(master: domain.model.User, groupname: String, gid: Int, uid: Int): Task[Unit] =
    ZIO.serviceWithZIO[WsService[Task]](_.deleteGroup(master, groupname, gid, uid)).provideLayer(wsLayer)

  def removeFriend(uId: Int, friendId: Int): Task[Unit] =
    ZIO.serviceWithZIO[WsService[Task]](_.removeFriend(uId, friendId)).provideLayer(wsLayer)

  def addGroup(uId: Int, message: domain.Message): Task[Unit] =
    ZIO.serviceWithZIO[WsService[Task]](_.addGroup(uId, message)).provideLayer(wsLayer)

  def addFriend(uId: Int, message: domain.Message): Task[Unit] =
    ZIO.serviceWithZIO[WsService[Task]](_.addFriend(uId, message)).provideLayer(wsLayer)

  def countUnHandMessage(uId: Int): Task[Map[String, String]] =
    ZIO.serviceWithZIO[WsService[Task]](_.countUnHandMessage(uId)).provideLayer(wsLayer)

  def checkOnline(message: domain.Message): Task[Map[String, String]] =
    ZIO.serviceWithZIO[WsService[Task]](_.checkOnline(message)).provideLayer(wsLayer)

  def sendMessage(message: String, actorRef: ActorRef): Task[Unit] =
    ZIO.serviceWithZIO[WsService[Task]](_.sendMessage(message, actorRef)).provideLayer(wsLayer)

  def changeOnline(uId: Int, status: String): Task[Boolean] =
    ZIO.serviceWithZIO[WsService[Task]](_.changeOnline(uId, status)).provideLayer(wsLayer)

  def readOfflineMessage(message: domain.Message): Task[Unit] =
    ZIO.serviceWithZIO[WsService[Task]](_.readOfflineMessage(message)).provideLayer(wsLayer)

  def getConnections: Task[Int] =
    ZIO.serviceWithZIO[WsService[Task]](_.getConnections).provideLayer(wsLayer)

  private val actorRefs = new scala.collection.mutable.HashMap[String, akka.actor.typed.ActorRef[Command[_]]]

  private def getActorRef(akkaSystem: ActorSystem[_], uid: Long): typed.ActorRef[Command[_]] =
    actorRefs.getOrElseUpdate(
      s"$uid",
      akkaSystem.classicSystem.spawn(
        WsMessageForwardBehavior.apply(),
        s"wsMessageForwardActor_$uid",
        customDispatcher
      )
    )

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
      akkaTypedActor = getActorRef(akkaSystem, uId)
      // FIXME: until zio-actors support zio 2.0
      _ <- ZIO.attempt(akkaTypedActor ! TransmitMessageProxy(uId, msg, None))
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
      akkaTypedActor = getActorRef(akkaSystem, uId)
      _         <- changeStatus(uId, SystemConstant.status.ONLINE)
      akkaActor <- AkkaTypedActor.make(akkaTypedActor)
      in = Flow[Message]
        .watchTermination()((_, ft) => ft.foreach(_ => closeConnection(uId)))
        .mapConcat {
          case TextMessage.Strict(message) =>
            // Using it only for using zio-actors.
            Unsafe.unsafe { implicit runtime =>
              Runtime.default.unsafe
                .run(akkaActor ! TransmitMessageProxy(uId, message, Some(actorRef)))
                .getOrThrowFiberFailure()
            }
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
      Unsafe.unsafe { implicit runtime =>
        Runtime.default.unsafe
          .run(changeStatus(id, SystemConstant.status.HIDE) *> userStatusChangeByServer(id, SystemConstant.status.HIDE))
          .getOrThrowFiberFailure()
      }

      ar ! Status.Success(Done)
    }

  def userStatusChangeByServer(uId: Int, status: String): ZIO[Any, Throwable, Unit] = ZIO.scoped {
    ZioActorSystemConfiguration.userStatusActor.flatMap(actor => actor ! UserStatusChangeMessage(uId, status))
  }
}
