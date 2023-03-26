package org.bitlap.zim.cache

import cats.effect.IO

/** @author
 *    梦境迷离
 *  @version 1.0,2023/3/26
 */
package object redis4cats {
  type CRedis = RedisService[IO]
}
