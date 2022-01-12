package org.bitlap.zim

import zio.Has

/**
 * @author 梦境迷离
 * @version 1.0,2022/1/10
 */
package object cache {

  type RedisCache = Has[RedisCache.Service]

}
