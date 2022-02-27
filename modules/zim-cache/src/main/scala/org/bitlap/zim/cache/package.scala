package org.bitlap.zim
import zio.Has

/**
 * @author 梦境迷离
 * @since 2022/2/27
 * @version 1.0
 */
package object cache {

  type ZRedisCacheService = Has[ZioRedisService]

}
