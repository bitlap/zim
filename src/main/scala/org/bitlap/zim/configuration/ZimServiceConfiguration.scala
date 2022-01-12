package org.bitlap.zim.configuration

import org.bitlap.zim.application.ws.{ WsService, WsServiceLive }
import org.bitlap.zim.cache.RedisCache
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

  private val akkaSystemLayer: TaskLayer[ZActorSystemConfiguration] =
    InfrastructureConfiguration.live >>>
      ActorSystemConfiguration.live

  private val akkaHttpConfigurationLayer: TaskLayer[ZAkkaHttpConfiguration] =
    (InfrastructureConfiguration.live ++
      akkaSystemLayer) >>>
      AkkaHttpConfiguration.live

  private val materializerLayer: TaskLayer[ZMaterializer] =
    akkaSystemLayer >>>
      AkkaHttpConfiguration.materializerLive

  private val applicationConfigurationLayer: ULayer[ZApplicationConfiguration] =
    InfrastructureConfiguration.live >>>
      ApplicationConfiguration.live

  private val apiConfigurationLayer: TaskLayer[ZApiConfiguration] =
    (applicationConfigurationLayer ++
      akkaHttpConfigurationLayer ++
      materializerLayer) >>>
      ApiConfiguration.live

  private val redisLayer: ZLayer[Any, RedisError.IOError, RedisCache] =
    RedisCacheConfiguration.live >>> RedisCache.live

  private val wsLayer: ZLayer[Any, Nothing, WsService] =
    applicationConfigurationLayer >>> WsServiceLive.live

  val ZimEnv: ZLayer[
    Any,
    Throwable,
    ZApiConfiguration with ZActorSystemConfiguration with ZAkkaHttpConfiguration with RedisCache with WsService
  ] =
    apiConfigurationLayer ++ akkaSystemLayer ++ akkaHttpConfigurationLayer ++ redisLayer ++ wsLayer

}
