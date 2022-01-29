package org.bitlap.zim.server.application

import org.bitlap.zim.server.application.MailServiceSpec.env
import org.bitlap.zim.server.application.impl.MailService
import org.bitlap.zim.server.application.impl.MailService.ZMailService
import org.bitlap.zim.server.configuration.properties.MailConfigurationProperties
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.{ BootstrapRuntime, ULayer }

/**
 * @author 梦境迷离
 * @since 2022/1/9
 * @version 1.0
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
