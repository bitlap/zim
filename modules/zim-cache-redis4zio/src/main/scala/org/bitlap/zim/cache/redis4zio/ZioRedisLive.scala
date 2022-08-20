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

package org.bitlap.zim.cache.redis4zio

import io.circe.{ Decoder, Encoder }
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import org.bitlap.zim.cache.{ JavaDuration, RedisService }
import zio.{ redis, Has, Task, ULayer, ZLayer }
import zio.redis.Redis
import zio.schema.DeriveSchema.gen

/** @author
 *    梦境迷离
 *  @see
 *    https://zio.dev/version-1.x/datatypes/contextual/#module-pattern-20
 *  @version 3.0,2022/1/17
 */
case class ZioRedisLive(private val rs: Redis) extends RedisService[Task] {

  private lazy val redisLayer: ULayer[Has[Redis]] = ZLayer.succeed(rs)

  override def getSets(k: String): Task[List[String]] =
    redis
      .sMembers(k)
      .returning[String]
      .orDie
      .provideLayer(redisLayer)
      .map(_.toList)

  override def removeSetValue(k: String, m: String): Task[Long] =
    redis.sRem(k, m).orDie.provideLayer(redisLayer)

  override def setSet(k: String, m: String): Task[Long] =
    redis.sAdd(k, m).orDie.provideLayer(redisLayer)

  override def set[T](k: String, v: T, expireTime: JavaDuration = java.time.Duration.ofMinutes(30))(implicit
    encoder: Encoder[T]
  ): Task[Boolean] =
    redis
      .set[String, String](k, v.asJson.noSpaces, expireTime = Some(expireTime))
      .provideLayer(redisLayer)

  // we didn't use zio-schema here to serialize objects to store into redis
  override def get[T](key: String)(implicit decoder: Decoder[T]): Task[Option[T]] =
    redis
      .get(key)
      .returning[String]
      .provideLayer(redisLayer)
      .map {
        case Some(value) => decode(value).toOption
        case None        => None
      }

  override def exists(key: String): Task[Boolean] =
    redis.exists(key).provideLayer(redisLayer).map(_ > 0)

}
