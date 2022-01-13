package org.bitlap.zim.cache

import org.bitlap.zim.configuration.ZimServiceConfiguration
import zio.redis.RedisExecutor
import zio.{ redis, Chunk, Has, IO, ZIO, ZLayer }
import zio.redis.RedisError

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
      def getSets(k: String): IO[Nothing, Chunk[String]]

      /**
       * 移除Set集合中的value
       *
       * @param k
       * @param v
       * @return Long
       */
      def removeSetValue(k: String, v: String): IO[Nothing, Long]

      /**
       * 保存到Set集合中
       *
       * @param k
       * @param v
       * @return Long
       */
      def setSet(k: String, v: String): IO[Nothing, Long]
    }

    lazy val live: ZLayer[RedisExecutor, Nothing, ZRedisCacheService] =
      ZLayer.fromFunction { env =>
        new Service {
          override def getSets(k: String): IO[Nothing, Chunk[String]] =
            redis.sMembers(k).returning[String].orDie.provide(env)

          override def removeSetValue(k: String, v: String): IO[Nothing, Long] =
            redis.sRem(k, v).orDie.provide(env)

          override def setSet(k: String, v: String): IO[Nothing, Long] =
            redis.sAdd(k, v).orDie.provide(env)
        }
      }
  }

  // 非最佳实践，为了使用unsafeRun，不能把environment传递到最外层，这里直接provideLayer
  def getSets(k: String): ZIO[Any, RedisError, Chunk[String]] =
    ZIO.serviceWith[RedisCacheService.Service](_.getSets(k)).provideLayer(redisLayer)

  def removeSetValue(k: String, v: String): ZIO[Any, RedisError, Long] =
    ZIO.serviceWith[RedisCacheService.Service](_.removeSetValue(k, v)).provideLayer(redisLayer)

  def setSet(k: String, v: String): ZIO[Any, RedisError, Long] =
    ZIO.serviceWith[RedisCacheService.Service](_.setSet(k, v)).provideLayer(redisLayer)
}
