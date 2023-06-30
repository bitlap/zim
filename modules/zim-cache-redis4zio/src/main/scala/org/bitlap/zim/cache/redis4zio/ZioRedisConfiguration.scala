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

import com.typesafe.config.{Config, _}

import zio._
import zio.redis._

/** redis configuration
 *
 *  @author
 *    梦境迷离
 *  @since 2022/1/10
 *  @version 2.1
 */
object ZioRedisConfiguration {

  private val conf: Config = ConfigFactory.load().getConfig("cache.redis")

  lazy val redisConf: ULayer[RedisConfig] = ZLayer.succeed {
    if (conf.isEmpty) {
      RedisConfig.Default
    } else {
      RedisConfig(conf.getString("host"), conf.getInt("port"))
    }
  }
}
