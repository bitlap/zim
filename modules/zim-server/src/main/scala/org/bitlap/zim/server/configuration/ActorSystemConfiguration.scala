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
object ActorSystemConfiguration {

  type ZActorSystemConfiguration = Has[ActorSystem]

  /**
   * 构造actorSystem对象，先使用classic actor
   */
  private lazy val actorSystem: RIO[ZInfrastructureConfiguration, ActorSystem] = {
    for {
      actorSystem <- Task(ActorSystem("ActorSystem"))
    } yield actorSystem
  }

  val live: RLayer[ZInfrastructureConfiguration, ZActorSystemConfiguration] = ZLayer
    .fromAcquireRelease(actorSystem)(actorSystem => UIO.succeed(actorSystem.terminate()).ignore)

}
