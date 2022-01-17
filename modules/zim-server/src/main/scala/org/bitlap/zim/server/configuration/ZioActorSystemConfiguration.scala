package org.bitlap.zim.server.configuration

import org.bitlap.zim.domain.ws.protocol
import org.bitlap.zim.domain.ws.protocol.Constants
import org.bitlap.zim.server.actor.{ ScheduleStateful, UserStatusStateful }
import org.bitlap.zim.server.configuration.InfrastructureConfiguration.ZInfrastructureConfiguration
import zio.actors._
import zio.clock.Clock
import zio.{ UIO, _ }

/**
 * zio actor configuration
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
object ZioActorSystemConfiguration {

  type ZZioActorSystemConfiguration = Has[ActorSystem]

  /**
   * create a zio actorSystem
   */
  private lazy val actorSystem: RIO[ZInfrastructureConfiguration, ActorSystem] = {
    for {
      actorSystem <- ActorSystem("zioActorSystem")
    } yield actorSystem
  }

  lazy val jobActor: ZIO[Any, Throwable, ActorRef[protocol.Command]] =
    actorSystem
      .flatMap(_.make(Constants.SCHEDULE_JOB_ACTOR, zio.actors.Supervisor.none, (), ScheduleStateful.stateful))
      .provideLayer(Clock.live ++ InfrastructureConfiguration.live)

  lazy val userStatusActor: ZIO[Any, Throwable, ActorRef[protocol.Command]] =
    actorSystem
      .flatMap(
        _.make(Constants.USER_STATUS_CHANGE_ACTOR, zio.actors.Supervisor.none, (), UserStatusStateful.stateful)
      )
      .provideLayer(Clock.live ++ InfrastructureConfiguration.live)

  val live: RLayer[ZInfrastructureConfiguration, ZZioActorSystemConfiguration] = ZLayer
    .fromAcquireRelease(actorSystem)(actorSystem => UIO.succeed(actorSystem.shutdown).ignore)

}
