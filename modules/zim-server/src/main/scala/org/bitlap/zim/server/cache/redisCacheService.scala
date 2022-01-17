package org.bitlap.zim.server.cache

import org.bitlap.zim.server.configuration.ZimServiceConfiguration
import zio.redis.{ RedisError, RedisExecutor }
import zio.{ redis, Chunk, Has, IO, URLayer, ZIO, ZLayer }

/**
 * Redis缓存服务
 *
 * @author 梦境迷离
 * @version 1.0,2022/1/10
 */

object redisCacheService extends ZimServiceConfiguration {

  type ZRedisCacheService = Has[RedisCacheService.Service]

  object RedisCacheService {

    trait Service {

      /**
       * 获取Set集合数据
       *
       * @param k
       * @return Chunk[String]
       */
      def getSets(k: String): IO[RedisError, Chunk[String]]

      /**
       * 移除Set集合中的value
       *
       * @param k
       * @param v
       * @return Long
       */
      def removeSetValue(k: String, v: String): IO[RedisError, Long]

      /**
       * 保存到Set集合中
       *
       * @param k
       * @param v
       * @return Long
       */
      def setSet(k: String, v: String): IO[RedisError, Long]
    }

    lazy val live: URLayer[RedisExecutor, ZRedisCacheService] =
      ZLayer.fromFunction { env =>
        new Service {
          override def getSets(k: String): IO[RedisError, Chunk[String]] =
            redis.sMembers(k).returning[String].orDie.provide(env)

          override def removeSetValue(k: String, v: String): IO[RedisError, Long] =
            redis.sRem(k, v).orDie.provide(env)

          override def setSet(k: String, v: String): IO[RedisError, Long] =
            redis.sAdd(k, v).orDie.provide(env)
        }
      }
  }

  // use it by redisCacheService.xxx()
  def getSets(k: String): IO[RedisError, Chunk[String]] =
    ZIO.serviceWith[RedisCacheService.Service](_.getSets(k)).provideLayer(redisLayer)

  def removeSetValue(k: String, v: String): IO[RedisError, Long] =
    ZIO.serviceWith[RedisCacheService.Service](_.removeSetValue(k, v)).provideLayer(redisLayer)

  def setSet(k: String, v: String): IO[RedisError, Long] =
    ZIO.serviceWith[RedisCacheService.Service](_.setSet(k, v)).provideLayer(redisLayer)
}
