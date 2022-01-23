package org.bitlap.zim.cache
import org.bitlap.zim.cache.zioRedisService.RedisCacheService.Service
import org.bitlap.zim.cache.zioRedisService.ZRedisCacheService
import zio.duration.Duration
import zio.redis.{ RedisError, RedisExecutor }
import zio.schema.Schema
import zio.{ redis, Chunk, IO, URLayer, ZLayer }

import java.util.concurrent.TimeUnit

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

        override def set[T: Schema](key: String, value: T): IO[RedisError, Boolean] =
          redis.set[String, T](key, value, expireTime = Some(Duration(30, TimeUnit.MINUTES))).provide(env)

        override def get[T: Schema](key: String): IO[RedisError, Option[T]] = redis.get(key).returning[T].provide(env)

        override def exists(key: String): IO[RedisError, Long] = redis.exists(key).provide(env)
      }
    }

}
