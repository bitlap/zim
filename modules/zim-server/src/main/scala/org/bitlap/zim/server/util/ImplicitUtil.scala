package org.bitlap.zim.server.util

import java.util.concurrent.{ Future => JFuture }
import scala.concurrent.{ ExecutionContext, Future }

/**
 * Convert Java future to Scala future
 *
 * @author 梦境迷离
 * @version 1.0,2021/12/30
 */
object ImplicitUtil {

  implicit class JFutureWrapper[T](f: JFuture[T]) {

    def asScala()(implicit ex: ExecutionContext): Future[T] = Future {
      Thread.sleep(100)
      f
    }.flatMap(f =>
      if (f.isDone) Future {
        f.get
      }
      else asScala()
    )
  }

}
