package org.bitlap.zim
import akka.stream.Materializer
import zio.Has

/**
 * @author 梦境迷离
 * @version 1.0,2022/2/11
 */
package object server {

  type ZMaterializer = Has[Materializer]

  lazy val zioRuntime: zio.Runtime[zio.ZEnv] = zio.Runtime.default

}
