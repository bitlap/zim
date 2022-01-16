package org.bitlap.zim.server

import org.bitlap.zim.server.configuration.ZimServiceConfiguration
import zio._
import zio.clock.Clock
import zio.console.Console
import zio.logging.{ log, LogFormat, LogLevel, Logging }
import org.bitlap.zim.server.configuration.{ AkkaHttpConfiguration, ApiConfiguration }

/**
 * main方法
 *
 * @author 梦境迷离
 * @version 1.0,2021/12/24
 */
object ZimServer extends ZimServiceConfiguration with zio.App {

  private lazy val loggingLayer: URLayer[Console with Clock, Logging] =
    Logging.console(
      logLevel = LogLevel.Debug,
      format = LogFormat.ColoredLogFormat()
    ) >>> Logging.withRootLoggerName("ZimApplication")

  private val program: URIO[Logging, ExitCode] =
    (for {
      routes <- ApiConfiguration.routes
      _ <- AkkaHttpConfiguration.httpServer(routes)
    } yield ())
      .provideLayer(ZimEnv)
      .foldM(
        e => log.throwable("", e) as ExitCode.failure,
        _ => UIO.effectTotal(ExitCode.success)
      )

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    program.provideLayer(loggingLayer)

}
