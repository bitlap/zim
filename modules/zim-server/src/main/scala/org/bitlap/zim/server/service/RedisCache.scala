/*
 * Copyright 2023 bitlap
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

package org.bitlap.zim.server.service

import org.bitlap.zim.cache.redis4cats.{CatsRedisServiceLive, Redis}
import org.bitlap.zim.cache.redis4zio._
import org.bitlap.zim.server.CacheType
import org.bitlap.zim.server.CacheType._

import io.circe._

import cats.effect._
import cats.effect.unsafe.implicits.global
import zio._
import zio.interop.catz._
import zio.schema.Schema

object RedisCache {

  def getSets(k: String)(implicit cacheType: CacheType): Task[List[String]] = cacheType match {
    case ZioCache => ZIO.serviceWithZIO[ZRedis](_.getSets(k)).provideLayer(ZioRedisServiceLive.live)
    case CatsCache =>
      ZIO.serviceWithZIO[Redis](c => LiftIO.liftK[Task].apply(c.getSets(k))).provideLayer(CatsRedisServiceLive.live)
  }

  def removeSetValue(k: String, m: String)(implicit cacheType: CacheType): Task[Long] = cacheType match {
    case ZioCache =>
      ZIO.serviceWithZIO[ZRedis](_.removeSetValue(k, m)).provideLayer(ZioRedisServiceLive.live)
    case CatsCache =>
      ZIO
        .serviceWithZIO[Redis](c => LiftIO.liftK[Task].apply(c.removeSetValue(k, m)))
        .provideLayer(CatsRedisServiceLive.live)
  }

  def setSet(k: String, m: String)(implicit cacheType: CacheType): Task[Long] =
    cacheType match {
      case ZioCache => ZIO.serviceWithZIO[ZRedis](_.setSet(k, m)).provideLayer(ZioRedisServiceLive.live)
      case CatsCache =>
        ZIO
          .serviceWithZIO[Redis](c => LiftIO.liftK[Task].apply(c.setSet(k, m)))
          .provideLayer(CatsRedisServiceLive.live)
    }

  def set[T: Schema](key: String, value: T)(implicit
    cacheType: CacheType,
    encoder: Encoder[T]
  ): Task[Boolean] =
    cacheType match {
      case ZioCache =>
        ZIO.serviceWithZIO[ZRedis](_.set[T](key, value)).provideLayer(ZioRedisServiceLive.live)
      case CatsCache =>
        ZIO
          .serviceWithZIO[Redis](c => LiftIO.liftK[Task].apply(c.set(key, value)))
          .provideLayer(CatsRedisServiceLive.live)
    }

  // zio-redis没有真正使用Schema，因为存在cats redis
  def get[T: Schema](key: String)(implicit
    cacheType: CacheType,
    decoder: Decoder[T]
  ): zio.Task[Option[T]] = cacheType match {
    case ZioCache =>
      ZIO.serviceWithZIO[ZRedis](_.get[T](key)).provideLayer(ZioRedisServiceLive.live)
    case CatsCache =>
      ZIO.serviceWithZIO[Redis](c => LiftIO.liftK[Task].apply(c.get(key))).provideLayer(CatsRedisServiceLive.live)
  }

  def exists(key: String)(implicit cacheType: CacheType): Task[Boolean] = cacheType match {
    case ZioCache =>
      ZIO.serviceWithZIO[ZRedis](_.exists(key)).provideLayer(ZioRedisServiceLive.live)
    case CatsCache =>
      ZIO.serviceWithZIO[Redis](c => LiftIO.liftK[Task].apply(c.exists(key))).provideLayer(CatsRedisServiceLive.live)
  }

  def del(key: String)(implicit cacheType: CacheType): Task[Boolean] = cacheType match {
    case ZioCache =>
      ZIO.serviceWithZIO[ZRedis](_.del(key)).provideLayer(ZioRedisServiceLive.live)
    case CatsCache =>
      ZIO.serviceWithZIO[Redis](c => LiftIO.liftK[Task].apply(c.del(key))).provideLayer(CatsRedisServiceLive.live)
  }
}
