package org.bitlap.zim.server.configuration

import org.bitlap.zim.cache.zioRedisService.ZRedisCacheService
import org.bitlap.zim.cache.ZioRedisLive
import org.bitlap.zim.server.application.ws.wsService.{ WsService, ZWsService }
import org.bitlap.zim.server.configuration.AkkaActorSystemConfiguration.ZAkkaActorSystemConfiguration
import org.bitlap.zim.server.configuration.AkkaHttpConfiguration.{ ZAkkaHttpConfiguration, ZMaterializer }
import org.bitlap.zim.server.configuration.ApiConfiguration.ZApiConfiguration
import org.bitlap.zim.server.configuration.ApplicationConfiguration.ZApplicationConfiguration
import org.bitlap.zim.server.configuration.ZioActorSystemConfiguration.ZZioActorSystemConfiguration
import zio.{ Layer, TaskLayer, ULayer }

/**
 * global configuration to collect all service or system layer
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait ZimServiceConfiguration {

  private lazy val akkaActorSystemLayer: TaskLayer[ZAkkaActorSystemConfiguration] =
    InfrastructureConfiguration.live >>>
      AkkaActorSystemConfiguration.live

  private lazy val akkaHttpConfigurationLayer: TaskLayer[ZAkkaHttpConfiguration] =
    akkaActorSystemLayer >>> AkkaHttpConfiguration.live

  private lazy val materializerLayer: TaskLayer[ZMaterializer] =
    akkaActorSystemLayer >>>
      AkkaHttpConfiguration.materializerLive

  protected lazy val applicationConfigurationLayer: ULayer[ZApplicationConfiguration] =
    InfrastructureConfiguration.live >>>
      ApplicationConfiguration.live

  private lazy val apiConfigurationLayer: TaskLayer[ZApiConfiguration] =
    (applicationConfigurationLayer ++
      akkaHttpConfigurationLayer ++
      materializerLayer) >>>
      ApiConfiguration.live

  val ZimEnv: TaskLayer[ZApiConfiguration with ZAkkaActorSystemConfiguration with ZAkkaHttpConfiguration] =
    apiConfigurationLayer ++ akkaActorSystemLayer ++ akkaHttpConfigurationLayer

  // 非最佳实践
  protected lazy val wsLayer: ULayer[ZWsService] =
    applicationConfigurationLayer >>> WsService.live

  protected lazy val zioActorSystemLayer: TaskLayer[ZZioActorSystemConfiguration] =
    InfrastructureConfiguration.live >>> ZioActorSystemConfiguration.live

}
