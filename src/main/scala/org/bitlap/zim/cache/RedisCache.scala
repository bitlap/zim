package org.bitlap.zim.cache

import zio.duration.durationInt
import zio.redis.RedisExecutor
import zio.{ redis, IO, ZLayer }

/**
 * Redis缓存服务
 *
 * @author 梦境迷离
 * @version 1.0,2022/1/10
 */
object RedisCache {

  trait Service {

    def get(key: String): IO[Nothing, Option[String]]

    def set(key: String, value: String): IO[Nothing, Boolean]

  }

  // ZIO service 管理，这将不需要构造函数传参
  lazy val live: ZLayer[RedisExecutor, Nothing, RedisCache] =
    ZLayer.fromFunction { env =>
      new Service {
        override def get(key: String): IO[Nothing, Option[String]] =
          redis.get[String](key).returning[String].orDie.provide(env)

        override def set(key: String, value: String): IO[Nothing, Boolean] =
          redis.set[String, String](key, value, Some(1.minute)).orDie.provide(env)
      }
    }

  //使用
  //val s = ZIO.serviceWith[RedisCache.Service](_.getByKey("").map(_.getOrElse("")))

  // 咋测试？
}
