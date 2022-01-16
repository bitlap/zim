package org.bitlap.zim.server.configuration

import org.bitlap.zim.server.application.ws.wsService.{ WsService, ZWsService }
import org.bitlap.zim.server.cache.redisCacheService.{ RedisCacheService, ZRedisCacheService }
import org.bitlap.zim.server.configuration.AkkaActorSystemConfiguration.ZAkkaActorSystemConfiguration
import org.bitlap.zim.server.configuration.AkkaHttpConfiguration.{ ZAkkaHttpConfiguration, ZMaterializer }
import org.bitlap.zim.server.configuration.ApiConfiguration.ZApiConfiguration
import org.bitlap.zim.server.configuration.ApplicationConfiguration.ZApplicationConfiguration
import zio.redis.RedisError
import zio.{ Layer, TaskLayer, ULayer }
import org.bitlap.zim.server.configuration.ZioActorSystemConfiguration.ZZioActorSystemConfiguration

/**
 * 全局的服务依赖管理
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
  protected lazy val redisLayer: Layer[RedisError.IOError, ZRedisCacheService] =
    RedisCacheConfiguration.live >>> RedisCacheService.live

  protected lazy val redisTestLayer: Layer[RedisError.IOError, ZRedisCacheService] =
    RedisCacheConfiguration.testLive >>> RedisCacheService.live

  protected lazy val wsLayer: ULayer[ZWsService] =
    applicationConfigurationLayer >>> WsService.live

  protected lazy val zioActorSystemLayer: TaskLayer[ZZioActorSystemConfiguration] =
    InfrastructureConfiguration.live >>> ZioActorSystemConfiguration.live

}
