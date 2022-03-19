/*
 * Copyright 2022 bitlap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
