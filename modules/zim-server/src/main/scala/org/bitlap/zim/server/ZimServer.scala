package org.bitlap.zim.server

import org.bitlap.zim.server.configuration.{ AkkaHttpConfiguration, ApiConfiguration, ZimServiceConfiguration }
import org.bitlap.zim.server.util.LogUtil
import zio._
import zio.logging.Logging

/**
 * main方法
 *
 * @author 梦境迷离
 * @version 1.0,2021/12/24
 */
object ZimServer extends ZimServiceConfiguration with zio.App {

  private val program: URIO[Logging, ExitCode] =
    (for {
      routes <- ApiConfiguration.routes
      _ <- AkkaHttpConfiguration.httpServer(routes)
    } yield ())
      .provideLayer(ZimEnv)
      .foldM(
        e => LogUtil.error(s"error => $e") as ExitCode.failure,
        _ => UIO.effectTotal(ExitCode.success)
      )

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    program.provideLayer(LogUtil.loggingLayer)

}
