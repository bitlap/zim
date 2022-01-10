package org.bitlap.zim.cache

import zio.{ redis, URIO }
import zio.duration.durationInt
import zio.redis.RedisExecutor
import zio.schema.DeriveSchema._

import zio.ZLayer
import zio.IO

/**
 * Redis缓存服务
 *
 * @author 梦境迷离
 * @version 1.0,2022/1/10
 */
object RedisCache {

  trait Service {
    def getByString(key: String): IO[Nothing, Option[String]]
  }

  // ZIO service 管理，这将不需要构造函数传参
  lazy val live: ZLayer[RedisExecutor, Nothing, RedisCache] =
    ZLayer.fromFunction { env =>
      (key: String) => get[String](key).provide(env)
    }

  // redis方法
  def set(str: String, value: String): URIO[RedisExecutor, Any] = redis.set[String, String](str, value, Some(1.minute)).orDie


  def get[K](str: K): URIO[RedisExecutor, Option[String]] = redis.get[K](str).returning[String].orDie

  //使用
  //val s = ZIO.serviceWith[RedisCache.Service](_.getByString("").map(_.getOrElse("")))
}