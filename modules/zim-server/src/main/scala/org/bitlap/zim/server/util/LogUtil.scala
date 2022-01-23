package org.bitlap.zim.server.util
import zio.clock.Clock
import zio.console.Console
import zio.logging.{ LogFormat, LogLevel, Logger, Logging }
import zio.{ UIO, ULayer, URLayer, ZIO }
import zio.stream.{ UStream, ZStream }

/**
 * @author 梦境迷离
 * @since 2022/1/20
 * @version 1.0
 */
object LogUtil {

  lazy val loggingLayer: URLayer[Console with Clock, Logging] =
    Logging.console(
      logLevel = LogLevel.Debug,
      format = LogFormat.ColoredLogFormat()
    ) >>> Logging.withRootLoggerName("ZimApplication")

  private val logLayer: ULayer[Logging] = (Console.live ++ Clock.live) >>> loggingLayer

  def info(msg: => String): UIO[Unit] =
    ZIO.serviceWith[Logger[String]](_.log(msg)).provideLayer(logLayer)

  def debug(msg: => String): UIO[Unit] =
    ZIO.serviceWith[Logger[String]](_.debug(msg)).provideLayer(logLayer)

  def error(msg: => String): UIO[Unit] =
    ZIO.serviceWith[Logger[String]](_.error(msg)).provideLayer(logLayer)

  // 后面要把非必要的stream去掉
  def infoS(msg: => String): UStream[Unit] =
    ZStream.fromEffect(info(msg))

  def debugS(msg: => String): UStream[Unit] =
    ZStream.fromEffect(debug(msg))

  def errorS(msg: => String): UStream[Unit] =
    ZStream.fromEffect(error(msg))
}
