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

package org.bitlap.zim.cache.redis4cats
import java.time.Duration

import cats.effect._
import com.typesafe.config._
import dev.profunktor.redis4cats._
import dev.profunktor.redis4cats.connection._
import dev.profunktor.redis4cats.data.RedisCodec
import dev.profunktor.redis4cats.log4cats._
import io.lettuce.core.{ClientOptions, TimeoutOptions}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

/** redis configuration
 *
 *  @author
 *    梦境迷离
 *  @since 2022/8/18
 *  @version 1.0
 */
object CatsRedisConfiguration {

  private val conf: Config = ConfigFactory.load().getConfig("cache.redis")

  private lazy val redisHost = conf.getString("host")
  private lazy val redisPort = conf.getInt("port")

  private val stringCodec: RedisCodec[String, String] = RedisCodec.Utf8

  private val mkOpts: IO[ClientOptions] =
    IO {
      ClientOptions
        .builder()
        .autoReconnect(false)
        .pingBeforeActivateConnection(false)
        .timeoutOptions(
          TimeoutOptions
            .builder()
            .fixedTimeout(Duration.ofSeconds(10))
            .build()
        )
        .build()
    }

  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  val api: Resource[IO, RedisCommands[IO, String, String]] =
    for {
      uri    <- Resource.eval(RedisURI.make[IO](s"redis://$redisHost:$redisPort"))
      opts   <- Resource.eval(mkOpts)
      client <- RedisClient[IO].custom(uri, opts)
      redis  <- Redis[IO].fromClient(client, stringCodec)
    } yield redis

  lazy val instance: CatsRedisService = CatsRedisService()

}
