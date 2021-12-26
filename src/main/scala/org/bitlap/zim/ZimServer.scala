package org.bitlap.zim

import org.bitlap.zim.configuration.{ AkkaHttpConfiguration, ApiConfiguration, ZimServiceConfiguration }
import zio._
import zio.clock.Clock
import zio.console.Console
import zio.logging.{ log, LogFormat, LogLevel, Logging }

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

  private val program: ZIO[Logging, Nothing, ExitCode] =
    (for {
      routes <- ApiConfiguration.routes
      _ <- AkkaHttpConfiguration.httpServer(routes)
    } yield ())
      .provideLayer(ZimEnv)
      .foldM(
        e => log.throwable("", e) as ExitCode.failure,
        _ => UIO.effectTotal(ExitCode.success)
      )

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] =
    program.provideLayer(loggingLayer)

}
