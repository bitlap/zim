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

import zio.redis.RedisError
import zio.schema.Schema
import zio.{ Chunk, IO, Layer, ZIO }

/**
 * Redis缓存服务
 *
 * @author 梦境迷离
 * @version 2.0,2022/1/10
 */

trait ZioRedisService {

  /**
   * 获取Set集合数据
   *
   * @param k
   * @return Chunk[String]
   */
  def getSets(k: String): ZIO[ZRedisCacheService, RedisError, Chunk[String]]

  /**
   * 移除Set集合中的value
   *
   * @param k
   * @param m
   * @return Long
   */
  def removeSetValue(k: String, m: String): ZIO[ZRedisCacheService, RedisError, Long]

  /**
   * 保存到Set集合中
   *
   * @param k
   * @param m
   * @return Long
   */
  def setSet(k: String, m: String): ZIO[ZRedisCacheService, RedisError, Long]

  /**
   * 存储key-value
   *
   * @param key
   * @param value 目前仅支持 primitives type
   * @return Object
   */
  def set[T: Schema](key: String, value: T): ZIO[ZRedisCacheService, RedisError, Boolean]

  /**
   * 根据key获取value
   *
   * @param key
   * @return Object
   */
  def get[T: Schema](key: String): ZIO[ZRedisCacheService, RedisError, Option[T]]

  /**
   * 判断key是否存在
   *
   * @param key
   * @return Boolean
   */
  def exists(key: String): ZIO[ZRedisCacheService, RedisError, Long]

}

object ZioRedisService {

  val zioRedisLayer: Layer[RedisError.IOError, ZRedisCacheService] = ZioRedisConfiguration.redisLayer

  // use it by ZioRedisService.xxx()
  def getSets(k: String)(implicit
    layer: Layer[RedisError.IOError, ZRedisCacheService] = zioRedisLayer
  ): IO[RedisError, Chunk[String]] =
    ZIO.serviceWith[ZioRedisService](_.getSets(k)).provideLayer(layer)

  def removeSetValue(k: String, m: String)(implicit
    layer: Layer[RedisError.IOError, ZRedisCacheService] = zioRedisLayer
  ): IO[RedisError, Long] =
    ZIO.serviceWith[ZioRedisService](_.removeSetValue(k, m)).provideLayer(layer)

  def setSet(k: String, m: String)(implicit
    layer: Layer[RedisError.IOError, ZRedisCacheService] = zioRedisLayer
  ): IO[RedisError, Long] =
    ZIO.serviceWith[ZioRedisService](_.setSet(k, m)).provideLayer(layer)

  def set[T: Schema](key: String, value: T)(implicit
    layer: Layer[RedisError.IOError, ZRedisCacheService] = zioRedisLayer
  ): IO[RedisError, Boolean] =
    ZIO.serviceWith[ZioRedisService](_.set[T](key, value)).provideLayer(layer)

  def get[T: Schema](key: String)(implicit
    layer: Layer[RedisError.IOError, ZRedisCacheService] = zioRedisLayer
  ): IO[RedisError, Option[T]] =
    ZIO.serviceWith[ZioRedisService](_.get[T](key)).provideLayer(layer)

  def exists(key: String)(implicit
    layer: Layer[RedisError.IOError, ZRedisCacheService] = zioRedisLayer
  ): IO[RedisError, Long] =
    ZIO.serviceWith[ZioRedisService](_.exists(key)).provideLayer(layer)
}
