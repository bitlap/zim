/*
 * Copyright 2023 bitlap
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

package org.bitlap.zim.server.service

import org.bitlap.zim.infrastructure.properties._
import org.bitlap.zim.server.CommonTestSupport
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

import com.typesafe.config.ConfigFactory

import zio._

/** @author
 *    梦境迷离
 *  @since 2022/1/9
 *  @version 1.0
 */
final class MailServiceSpec extends AnyFlatSpec with Matchers with CommonTestSupport {

  // 本地
  lazy val live: ULayer[MailConfigurationProperties] = ZLayer.succeed(
    MailConfigurationProperties(ConfigFactory.load("application-test.conf").getConfig("infrastructure.javamail"))
  )

  "sendHtmlMail" should "send async ignore error" in {
    val task =
      MailServiceImpl
        .sendHtmlMail("12222@qq.com", "hello world", """<a href="http://localhost:9000"/>""")
        .provide(MailConfigurationProperties.live, MailServiceImpl.live)
    val ret = unsafeRun(task)
    assert(ret != null)
  }

}
