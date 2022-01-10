package org.bitlap.zim.configuration

import com.typesafe.config.ConfigFactory
import zio._
import zio.logging.Logging
import zio.redis.{ RedisConfig, RedisExecutor }
import zio.redis.codec.StringUtf8Codec
import zio.schema.codec.Codec
import zio.redis.RedisError
import com.typesafe.config.Config

/**
 * redis配置
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

  private lazy val codec = ZLayer.succeed[Codec](StringUtf8Codec)

  val live: ZLayer[Any, RedisError.IOError, RedisExecutor] = (Logging.ignore ++ ZLayer.succeed(redisConf)
    ++ codec) >>> RedisExecutor.live
}
