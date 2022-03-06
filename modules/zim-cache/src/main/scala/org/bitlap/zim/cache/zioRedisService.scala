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

import zio.redis.RedisError
import zio.schema.Schema
import zio.{ Chunk, Has, IO, Layer, ZIO }

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

      /**
       * 存储key-value
       *
       * @param key
       * @param value 目前仅支持 primitives type
       * @return Object
       */
      def set[T: Schema](key: String, value: T): IO[RedisError, Boolean]

      /**
       * 根据key获取value
       *
       * @param key
       * @return Object
       */
      def get[T: Schema](key: String): IO[RedisError, Option[T]]

      /**
       * 判断key是否存在
       *
       * @param key
       * @return Boolean
       */
      def exists(key: String): IO[RedisError, Long]
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

  def set[T: Schema](key: String, value: T)(implicit
    layer: Layer[RedisError.IOError, ZRedisCacheService]
  ): IO[RedisError, Boolean] =
    ZIO.serviceWith[RedisCacheService.Service](_.set[T](key, value)).provideLayer(layer)

  def get[T: Schema](key: String)(implicit
    layer: Layer[RedisError.IOError, ZRedisCacheService]
  ): IO[RedisError, Option[T]] =
    ZIO.serviceWith[RedisCacheService.Service](_.get(key)).provideLayer(layer)

  def exists(key: String)(implicit
    layer: Layer[RedisError.IOError, ZRedisCacheService]
  ): IO[RedisError, Long] =
    ZIO.serviceWith[RedisCacheService.Service](_.exists(key)).provideLayer(layer)

}
