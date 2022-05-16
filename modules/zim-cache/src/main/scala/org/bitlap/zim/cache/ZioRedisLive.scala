/*
 * Copyright 2022 bitlap
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
import zio.duration.Duration
import zio.redis.{ Redis, RedisError }
import zio.schema.Schema
import zio.{ redis, Chunk, Has, ULayer, ZIO, ZLayer }

import java.util.concurrent.TimeUnit

/** @author
 *    梦境迷离
 *  @see
 *    https://zio.dev/version-1.x/datatypes/contextual/#module-pattern-20
 *  @version 2.0,2022/1/17
 */
case class ZioRedisLive(private val rs: Redis) extends ZioRedisService {

  private lazy val redisLayer: ULayer[Has[Redis]] = ZLayer.succeed(rs)

  override def getSets(k: String): ZIO[ZRedisCacheService, RedisError, Chunk[String]] =
    redis.sMembers(k).returning[String].orDie.provideLayer(redisLayer)

  override def removeSetValue(k: String, m: String): ZIO[ZRedisCacheService, RedisError, Long] =
    redis.sRem(k, m).orDie.provideLayer(redisLayer)

  override def setSet(k: String, m: String): ZIO[ZRedisCacheService, RedisError, Long] =
    redis.sAdd(k, m).orDie.provideLayer(redisLayer)

  override def set[T: Schema](key: String, value: T): ZIO[ZRedisCacheService, RedisError, Boolean] =
    redis.set[String, T](key, value, expireTime = Some(Duration(30, TimeUnit.MINUTES))).provideLayer(redisLayer)

  override def get[T: Schema](key: String): ZIO[ZRedisCacheService, RedisError, Option[T]] =
    redis.get(key).returning[T].provideLayer(redisLayer)

  override def exists(key: String): ZIO[ZRedisCacheService, RedisError, Long] =
    redis.exists(key).provideLayer(redisLayer)

}
