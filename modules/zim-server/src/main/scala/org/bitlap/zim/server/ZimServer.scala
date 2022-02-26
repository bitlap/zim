package org.bitlap.zim.server

import org.bitlap.zim.server.configuration.{ AkkaHttpConfiguration, ApiConfiguration, ZimServiceConfiguration }
import org.bitlap.zim.server.util.LogUtil
import zio._

/**
 * main方法
 *
 * @author 梦境迷离
 * @version 1.0,2021/12/24
 */
object ZimServer extends ZimServiceConfiguration with zio.App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    (for {
      routes <- ApiConfiguration.routes
      _ <- AkkaHttpConfiguration.httpServer(routes)
    } yield ())
      .provideLayer(ZimEnv)
      .foldM(
        e => LogUtil.error(s"error => $e").exitCode,
        _ => UIO.effectTotal(ExitCode.success)
      )

}
