package org.bitlap.zim.cache

import zio.{ Chunk, Has, IO, Layer, ZIO }
import zio.redis.RedisError

/**
 * Redis缓存服务
 *
 * @author 梦境迷离
 * @version 1.0,2022/1/10
 */

object zioRedisService {

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
  }

  implicit val zioRedisLayer: Layer[RedisError.IOError, ZRedisCacheService] =
    ZioRedisConfiguration.live >>> ZioRedisLive.live

  // use it by redisCacheService.xxx()
  def getSets(k: String)(implicit layer: Layer[RedisError.IOError, ZRedisCacheService]): IO[RedisError, Chunk[String]] =
    ZIO.serviceWith[RedisCacheService.Service](_.getSets(k)).provideLayer(layer)

  def removeSetValue(k: String, v: String)(implicit
    layer: Layer[RedisError.IOError, ZRedisCacheService]
  ): IO[RedisError, Long] =
    ZIO.serviceWith[RedisCacheService.Service](_.removeSetValue(k, v)).provideLayer(layer)

  def setSet(k: String, v: String)(implicit
    layer: Layer[RedisError.IOError, ZRedisCacheService]
  ): IO[RedisError, Long] =
    ZIO.serviceWith[RedisCacheService.Service](_.setSet(k, v)).provideLayer(layer)
}
