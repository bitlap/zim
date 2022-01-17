package org.bitlap.zim.server.application.ws
import akka.NotUsed
import akka.actor.ActorRef
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.model.ws.{ Message, TextMessage }
import akka.stream.{ CompletionStrategy, Materializer, OverflowStrategy }
import akka.stream.scaladsl.{ Flow, Keep, Sink, Source }
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import org.bitlap.zim.domain
import org.bitlap.zim.domain.{ SystemConstant, Message => IMMessage }
import org.bitlap.zim.domain.model.User
import org.bitlap.zim.domain.ws.protocol._
import org.bitlap.zim.server.actor.akka.WsMessageForwardBehavior
import org.bitlap.zim.server.configuration.{ AkkaActorSystemConfiguration, ZimServiceConfiguration }
import org.bitlap.zim.server.configuration.ApplicationConfiguration.ZApplicationConfiguration
import org.reactivestreams.Publisher
import zio.{ Has, Task, ZIO, ZLayer }
import zio.actors.akka.AkkaTypedActor
import org.bitlap.zim.cache.zioRedisService
import akka.actor.Status
import akka.Done
import java.util.concurrent.ConcurrentHashMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.jdk.CollectionConverters.ConcurrentMapHasAsScala

/**
 * @author 梦境迷离
 * @version 1.0, 2022/1/11
 */
object wsService extends ZimServiceConfiguration {

  type ZWsService = Has[WsService.Service]

  object WsService extends ZimServiceConfiguration {

    trait Service {

      def sendMessage(message: domain.Message): Task[Unit]

      def agreeAddGroup(msg: domain.Message): Task[Unit]

      def refuseAddGroup(msg: domain.Message): Task[Unit]

      def refuseAddFriend(messageBoxId: Int, user: User, to: Int): Task[Boolean]

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

    // akka actor -> zio actor
    final lazy val actorRefSessions: ConcurrentHashMap[Integer, ActorRef] =
      new ConcurrentHashMap[Integer, ActorRef]

    lazy val live: ZLayer[ZApplicationConfiguration, Nothing, ZWsService] =
      ZLayer.fromService { env =>
        val userService = env.userApplication

        new Service {

          override def sendMessage(message: domain.Message): Task[Unit] =
            message.synchronized {
              //看起来有点怪 是否有必要存在？
              //聊天类型，可能来自朋友或群组
              if (SystemConstant.FRIEND_TYPE == message.to.`type`) {
                friendMessageHandler(userService)(message)
              } else {
                groupMessageHandler(userService)(message)
              }
            }

          override def agreeAddGroup(msg: domain.Message): Task[Unit] = {
            val agree = decode[AddRefuseMessage](msg.msg).getOrElse(null)
            if (agree == null) ZIO.effect(DEFAULT_VALUE)
            else
              agree.messageBoxId.synchronized {
                agreeAddGroupHandler(userService)(agree)
              }
          }

          override def refuseAddGroup(msg: domain.Message): Task[Unit] = ???

          override def refuseAddFriend(messageBoxId: Int, user: User, to: Int): Task[Boolean] =
            messageBoxId.synchronized {
              refuseAddFriendHandler(userService)(messageBoxId, user, to)
            }

          override def deleteGroup(master: User, groupname: String, gid: Int, uid: Int): Task[Unit] =
            Task.succeed(())

          override def removeFriend(uId: Int, friendId: Int): Task[Unit] = ???

          override def addGroup(uId: Int, message: domain.Message): Task[Unit] = ???

          override def addFriend(uId: Int, message: domain.Message): Task[Unit] = ???

          override def countUnHandMessage(uId: Int): Task[Map[String, String]] = ???

          override def checkOnline(message: domain.Message): Task[Map[String, String]] = ???

          override def sendMessage(message: String, actorRef: ActorRef): Task[Unit] = ???

          override def changeOnline(uId: Int, status: String): Task[Boolean] = Task.succeed(true)

          override def readOfflineMessage(message: domain.Message): Task[Unit] =
            message.mine.id.synchronized {
              readOfflineMessageHandler(userService)(message)
            }

          override def getConnections: Task[Int] = ZIO.succeed(actorRefSessions.size())
        }
      }

  }

  // 非最佳实践，为了使用unsafeRun，不能把environment传递到最外层，这里直接provideLayer
  def sendMessage(message: domain.Message): Task[Unit] =
    ZIO.serviceWith[WsService.Service](_.sendMessage(message)).provideLayer(wsLayer)

  def agreeAddGroup(msg: domain.Message): Task[Unit] =
    ZIO.serviceWith[WsService.Service](_.agreeAddGroup(msg)).provideLayer(wsLayer)

  def refuseAddGroup(msg: domain.Message): Task[Unit] =
    ZIO.serviceWith[WsService.Service](_.refuseAddGroup(msg)).provideLayer(wsLayer)

  def refuseAddFriend(messageBoxId: Int, user: domain.model.User, to: Int): Task[Boolean] =
    ZIO.serviceWith[WsService.Service](_.refuseAddFriend(messageBoxId, user, to)).provideLayer(wsLayer)

  def deleteGroup(master: domain.model.User, groupname: String, gid: Int, uid: Int): Task[Unit] =
    ZIO.serviceWith[WsService.Service](_.deleteGroup(master, groupname, gid, uid)).provideLayer(wsLayer)

  def removeFriend(uId: Int, friendId: Int): Task[Unit] =
    ZIO.serviceWith[WsService.Service](_.removeFriend(uId, friendId)).provideLayer(wsLayer)

  def addGroup(uId: Int, message: domain.Message): Task[Unit] =
    ZIO.serviceWith[WsService.Service](_.addGroup(uId, message)).provideLayer(wsLayer)

  def addFriend(uId: Int, message: domain.Message): Task[Unit] =
    ZIO.serviceWith[WsService.Service](_.addFriend(uId, message)).provideLayer(wsLayer)

  def countUnHandMessage(uId: Int): Task[Map[String, String]] =
    ZIO.serviceWith[WsService.Service](_.countUnHandMessage(uId)).provideLayer(wsLayer)

  def checkOnline(message: domain.Message): Task[Map[String, String]] =
    ZIO.serviceWith[WsService.Service](_.checkOnline(message)).provideLayer(wsLayer)

  def sendMessage(message: String, actorRef: ActorRef): Task[Unit] =
    ZIO.serviceWith[WsService.Service](_.sendMessage(message, actorRef)).provideLayer(wsLayer)

  def changeOnline(uId: Int, status: String): Task[Boolean] =
    ZIO.serviceWith[WsService.Service](_.changeOnline(uId, status)).provideLayer(wsLayer)

  def readOfflineMessage(message: domain.Message): Task[Unit] =
    ZIO.serviceWith[WsService.Service](_.readOfflineMessage(message)).provideLayer(wsLayer)

  def getConnections: Task[Int] =
    ZIO.serviceWith[WsService.Service](_.getConnections).provideLayer(wsLayer)

  private final lazy val wsConnections: ConcurrentHashMap[Integer, ActorRef] =
    wsService.WsService.actorRefSessions

  private def changeStatus(uId: Int, status: String): Task[Unit] =
    for {
      _ <-
        if (status == SystemConstant.status.ONLINE) {
          zioRedisService.setSet(SystemConstant.ONLINE_USER, s"$uId")
        } else {
          zioRedisService.removeSetValue(SystemConstant.ONLINE_USER, s"$uId")
        }
      _ <- zioRedisService.setSet(SystemConstant.ONLINE_USER, s"$uId")
      msg = IMMessage(
        `type` = protocol.changOnline.stringify,
        mine = null,
        to = null,
        msg =
          if (status == SystemConstant.status.ONLINE) SystemConstant.status.ONLINE
          else SystemConstant.status.HIDE
      ).asJson.noSpaces
      akkaSystem <- AkkaActorSystemConfiguration.make
      akkaTypedActor = akkaSystem.spawn(WsMessageForwardBehavior.apply(), Constants.WS_MESSAGE_FORWARD_ACTOR)
      akkaActor <- AkkaTypedActor.make(akkaTypedActor)
      _ <- akkaActor ! TransmitMessageProxy(uId, msg, None)
    } yield ()

  /**
   * Connection processing and message processing
   *
   * @param uId
   * @return
   */
  def openConnection(
    uId: Int
  )(implicit m: Materializer): ZIO[Any, Throwable, Flow[Message, TextMessage.Strict, NotUsed]] = {
    //closeConnection(uId)
    val (actorRef: akka.actor.ActorRef, publisher: Publisher[TextMessage.Strict]) =
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
        .toMat(Sink.asPublisher(true))(Keep.both)
        .run()
    for {
      // we use akka-http, it must have akka-actor
      // if we use zio-actors in ws, because zio only support typed actor,
      // we need forward to akka typed actor for sending message to akka classic actor which return by akka http.
      // so we only use akka here
      akkaSystem <- AkkaActorSystemConfiguration.make
      akkaTypedActor = akkaSystem.spawn(WsMessageForwardBehavior(), Constants.WS_MESSAGE_FORWARD_ACTOR)
      _ <- changeStatus(uId, SystemConstant.status.ONLINE)
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

  lazy val zioRuntime: zio.Runtime[zio.ZEnv] = zio.Runtime.default

  /**
   * close websocket
   *
   * @param id
   */
  def closeConnection(id: Int): Unit =
    wsConnections.asScala.get(id).foreach { ar =>
      wsConnections.remove(id)
      zioRuntime.unsafeRunAsync(changeStatus(id, SystemConstant.status.HIDE))(ex => ex.unit)
      // Status(Done)
      ar ! Status.Success(Done)
    }

}
