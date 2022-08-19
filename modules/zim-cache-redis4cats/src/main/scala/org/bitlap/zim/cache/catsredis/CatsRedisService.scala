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

package org.bitlap.zim.cache.catsredis

import cats.effect.IO
import io.circe.{ Decoder, Encoder }
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import org.bitlap.zim.cache.RedisService
import dev.profunktor.redis4cats.effects.{ SetArg, SetArgs }

import scala.concurrent.duration._
import org.bitlap.zim.cache.JavaDuration
import org.typelevel.log4cats.Logger

/** @author
 *    梦境迷离
 *  @version 1.0,2022/8/18
 */
case class CatsRedisService()(implicit logger: Logger[IO]) extends RedisService[IO] {

  override def getSets(k: String): IO[List[String]] =
    logger.info(s"Redis sMembers command: $k") *> CatsRedisConfiguration.api.use { redis =>
      redis.sMembers(k).map(_.toList)
    }

  override def removeSetValue(k: String, m: String): IO[Long] =
    logger.info(s"Redis sRem command: $k, $m") *> CatsRedisConfiguration.api.use { redis =>
      redis.sRem(k, m).map(_ => 1)
    }

  override def setSet(k: String, m: String): IO[Long] =
    logger.info(s"Redis sAdd command: $k, $m") *> CatsRedisConfiguration.api.use { redis =>
      redis.sAdd(k, m)
    }

  override def set[T](k: String, v: T, expireTime: JavaDuration = java.time.Duration.ofMinutes(30))(implicit
    encoder: Encoder[T]
  ): IO[Boolean] =
    logger.info(s"Redis set command: $k, $v, $expireTime") *> CatsRedisConfiguration.api.use { redis =>
      redis
        .set(k, v.asJson.noSpaces, SetArgs(SetArg.Existence.Nx, SetArg.Ttl.Ex(expireTime.getSeconds.seconds)))
        .map(_ => true)
    }

  override def get[T](key: String)(implicit decoder: Decoder[T]): IO[Option[T]] =
    logger.info(s"Redis get command: $key") *> CatsRedisConfiguration.api.use { redis =>
      redis
        .get(key)
        .map {
          case Some(value) => decode(value).toOption
          case None        => None
        }
    }

  override def exists(key: String): IO[Boolean] =
    logger.info(s"Redis exists command: $key") *> CatsRedisConfiguration.api.use { redis =>
      redis.exists(key)
    }
}
