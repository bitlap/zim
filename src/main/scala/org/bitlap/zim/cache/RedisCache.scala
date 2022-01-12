package org.bitlap.zim.cache

import zio.redis.RedisExecutor
import zio.{ redis, Chunk, IO, ZLayer }

/**
 * Redis缓存服务
 *
 * @author 梦境迷离
 * @version 1.0,2022/1/10
 */
object RedisCache {

  trait Service {

    /**
     * 获取Set集合数据
     *
     * @param k
     * @return Chunk[String]
     */
    def getSets(k: String): IO[Nothing, Chunk[String]]

    /**
     * 移除Set集合中的value
     *
     * @param k
     * @param v
     * @return Long
     */
    def removeSetValue(k: String, v: String): IO[Nothing, Long]

    /**
     * 保存到Set集合中
     *
     * @param k
     * @param v
     * @return Long
     */
    def setSet(k: String, v: String): IO[Nothing, Long]
  }

  // ZIO service managed，instead of construction
  lazy val live: ZLayer[RedisExecutor, Nothing, RedisCache] =
    ZLayer.fromFunction { env =>
      new Service {
        override def getSets(k: String): IO[Nothing, Chunk[String]] =
          redis.sMembers(k).returning[String].orDie.provide(env)

        override def removeSetValue(k: String, v: String): IO[Nothing, Long] =
          redis.sRem(k, v).orDie.provide(env)

        override def setSet(k: String, v: String): IO[Nothing, Long] =
          redis.sAdd(k, v).orDie.provide(env)
      }
    }

  //How to use it?
  //val s = ZIO.serviceWith[RedisCache.Service](_.getSets("1")))
}
