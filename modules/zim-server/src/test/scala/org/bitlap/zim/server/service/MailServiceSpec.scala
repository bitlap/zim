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

package org.bitlap.zim.server.service
import org.bitlap.zim.infrastructure.properties.MailConfigurationProperties

import org.bitlap.zim.server.service.MailServiceSpec.env
import org.bitlap.zim.server.service.impl.MailService
import org.bitlap.zim.server.service.impl.MailService.ZMailService
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.{ BootstrapRuntime, ULayer }

/** @author
 *    梦境迷离
 *  @since 2022/1/9
 *  @version 1.0
 */
final class MailServiceSpec extends AnyFlatSpec with Matchers with BootstrapRuntime {

  "sendHtmlMail" should "send async ignore error" in {
    val task =
      MailService
        .sendHtmlMail("12222@qq.com", "hello world", """<a href="http://localhost:9000"/>""")
        .provideLayer(env)
    val ret = unsafeRunSync(task)
    assert(ret.succeeded)
  }

}

object MailServiceSpec {

  // 本地
//  lazy val mailConfigurationProperties: MailConfigurationProperties = MailConfigurationProperties(
//    config = ConfigFactory.load("application-test.conf").getConfig("application.javamail")
//  )

  lazy val mailConfigurationProperties: MailConfigurationProperties = MailConfigurationProperties()

  val env: ULayer[ZMailService] = MailService.make(mailConfigurationProperties)

}
