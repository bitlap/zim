package org.bitlap.zim.configuration

import org.bitlap.zim.application.ws.wsService.{ WsService, ZWsService }
import org.bitlap.zim.cache.redisCacheService.{ RedisCacheService, ZRedisCacheService }
import org.bitlap.zim.configuration.ActorSystemConfiguration.ZActorSystemConfiguration
import org.bitlap.zim.configuration.AkkaHttpConfiguration.{ ZAkkaHttpConfiguration, ZMaterializer }
import org.bitlap.zim.configuration.ApiConfiguration.ZApiConfiguration
import org.bitlap.zim.configuration.ApplicationConfiguration.ZApplicationConfiguration
import zio.{ TaskLayer, ULayer, ZLayer }
import zio.redis.RedisError

/**
 * 全局的服务依赖管理
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait ZimServiceConfiguration {

  private lazy val akkaSystemLayer: TaskLayer[ZActorSystemConfiguration] =
    InfrastructureConfiguration.live >>>
      ActorSystemConfiguration.live

  private lazy val akkaHttpConfigurationLayer: TaskLayer[ZAkkaHttpConfiguration] =
    (InfrastructureConfiguration.live ++
      akkaSystemLayer) >>>
      AkkaHttpConfiguration.live

  private lazy val materializerLayer: TaskLayer[ZMaterializer] =
    akkaSystemLayer >>>
      AkkaHttpConfiguration.materializerLive

  protected lazy val applicationConfigurationLayer: ULayer[ZApplicationConfiguration] =
    InfrastructureConfiguration.live >>>
      ApplicationConfiguration.live

  private lazy val apiConfigurationLayer: TaskLayer[ZApiConfiguration] =
    (applicationConfigurationLayer ++
      akkaHttpConfigurationLayer ++
      materializerLayer) >>>
      ApiConfiguration.live

  val ZimEnv: ZLayer[Any, Throwable, ZApiConfiguration with ZActorSystemConfiguration with ZAkkaHttpConfiguration] =
    apiConfigurationLayer ++ akkaSystemLayer ++ akkaHttpConfigurationLayer

  // 非最佳实践
  protected lazy val redisLayer: ZLayer[Any, RedisError.IOError, ZRedisCacheService] =
    RedisCacheConfiguration.live >>> RedisCacheService.live

  protected lazy val redisTestLayer: ZLayer[Any, RedisError.IOError, ZRedisCacheService] =
    RedisCacheConfiguration.live >>> RedisCacheService.live

  protected lazy val wsLayer: ZLayer[Any, Nothing, ZWsService] =
    applicationConfigurationLayer >>> WsService.live

}
