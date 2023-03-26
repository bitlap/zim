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

package org.bitlap.zim.cache.redis4zio

import io.circe._
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import org.bitlap.zim.cache._
import zio._
import zio.redis.Redis

/** @author
 *    梦境迷离
 *  @see
 *    https://zio.dev/version-1.x/datatypes/contextual/#module-pattern-20
 *  @version 3.0,2022/1/17
 */
object ZioRedisLive {
  lazy val layer: URLayer[Redis, ZRedis] = ZLayer.fromFunction(ZioRedisLive.apply _)
}
final case class ZioRedisLive(private val rs: Redis) extends RedisService[Task] {

  override def getSets(k: String): Task[List[String]] =
    rs.sMembers(k)
      .returning[String]
      .orDie
      .map(_.toList)

  override def removeSetValue(k: String, m: String): Task[Long] =
    rs.sRem(k, m).orDie

  override def setSet(k: String, m: String): Task[Long] =
    rs.sAdd(k, m).orDie

  override def set[T](k: String, v: T, expireTime: JavaDuration = java.time.Duration.ofMinutes(30))(implicit
    encoder: Encoder[T]
  ): Task[Boolean] =
    rs.set[String, String](k, v.asJson.noSpaces, expireTime = Some(expireTime))

  // we didn't use zio-schema here to serialize objects to store into redis
  override def get[T](key: String)(implicit decoder: Decoder[T]): Task[Option[T]] =
    rs.get(key)
      .returning[String]
      .map {
        case Some(value) => decode(value).toOption
        case None        => None
      }

  override def exists(key: String): Task[Boolean] =
    rs.exists(key).map(_ > 0)

  override def del(key: String): Task[Boolean] =
    rs.del[String](key).map(_ > 0)
}
