package org.bitlap.zim.server.configuration

import com.typesafe.config.{ Config, ConfigFactory }
import zio._
import zio.clock.Clock
import zio.logging.Logging
import zio.random.Random
import zio.redis.codec.StringUtf8Codec
import zio.redis.{ RedisConfig, RedisError, RedisExecutor }
import zio.schema.codec.Codec

/**
 * redis configuration
 *
 * @author 梦境迷离
 * @since 2022/1/10
 * @version 1.0
 */
object RedisCacheConfiguration {

  type ZRedisCacheConfiguration = Has[RedisConfig]

  private lazy val conf: Config = ConfigFactory.load().getConfig("application");

  private lazy val redisConf: RedisConfig =
    if (conf.isEmpty) RedisConfig.Default
    else RedisConfig(conf.getString("redis.host"), conf.getInt("redis.port"))

  private lazy val codec: ULayer[Has[Codec]] = ZLayer.succeed[Codec](StringUtf8Codec)

  // local redis layer
  val live: Layer[RedisError.IOError, RedisExecutor] = (Logging.ignore ++ ZLayer.succeed(redisConf)
    ++ codec) >>> RedisExecutor.local // not use socket

  // test redis layer
  val testLive: ULayer[RedisExecutor] = (Clock.live ++ Random.live) >>> RedisExecutor.test
}
