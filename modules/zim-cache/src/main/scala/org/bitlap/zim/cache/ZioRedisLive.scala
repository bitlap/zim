/*
 * Copyright 2021 bitlap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
