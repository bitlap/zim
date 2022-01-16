package org.bitlap.zim.server.configuration

import akka.actor.ActorSystem
import org.bitlap.zim.server.configuration.InfrastructureConfiguration.ZInfrastructureConfiguration
import zio._

/**
 * akka actor配置
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
object AkkaActorSystemConfiguration {

  type ZAkkaActorSystemConfiguration = Has[ActorSystem]

  /**
   * 构造actorSystem对象，先使用classic actor
   */
  private lazy val actorSystem: RIO[ZInfrastructureConfiguration, ActorSystem] = {
    for {
      actorSystem <- Task(ActorSystem("akkaActorSystem"))
    } yield actorSystem
  }

  val live: RLayer[ZInfrastructureConfiguration, ZAkkaActorSystemConfiguration] = ZLayer
    .fromAcquireRelease(actorSystem)(actorSystem => UIO.succeed(actorSystem.terminate()).ignore)

  def make: ZIO[Any, Throwable, ActorSystem] =
    AkkaActorSystemConfiguration.actorSystem.provideLayer(InfrastructureConfiguration.live)

}
