package org.bitlap.zim.cache
import org.bitlap.zim.cache.zioRedisService.RedisCacheService.Service
import org.bitlap.zim.cache.zioRedisService.ZRedisCacheService
import zio.redis.{ RedisError, RedisExecutor }
import zio.{ redis, Chunk, IO, URLayer, ZLayer }

/**
 * @author 梦境迷离
 * @version 1.0,2022/1/17
 */
object ZioRedisLive {

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
