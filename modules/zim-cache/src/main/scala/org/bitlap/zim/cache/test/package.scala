package org.bitlap.zim.cache
import org.bitlap.zim.cache.zioRedisService.ZRedisCacheService
import zio.redis.RedisError
import zio.Layer

/**
 * @author 梦境迷离
 * @version 1.0,2022/1/17
 */
package object test {

  implicit val zioRedisTestLayer: Layer[RedisError.IOError, ZRedisCacheService] =
    ZioRedisConfiguration.testLive >>> ZioRedisLive.live

}
