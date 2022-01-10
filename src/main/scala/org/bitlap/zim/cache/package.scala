package org.bitlap.zim

import zio.Has

/**
 *
 * @author li.guobin@immomo.com
 * @version 1.0,2022/1/10
 */
package object cache {

  type RedisCache = Has[RedisCache.Service]

}