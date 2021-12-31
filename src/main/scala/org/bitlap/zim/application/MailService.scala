package org.bitlap.zim.application

import org.bitlap.zim.configuration.properties.MailConfigurationProperties
import zio.Has
import org.simplejavamail.config.ConfigLoader
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder
import org.bitlap.zim.util.ImplicitUtil._
import zio.Task
import zio.ZIO

/**
 * 邮件发送服务
 *
 * @author 梦境迷离
 * @version 1.0,2021/12/30
 */
final class MailService(mailConfigurationProperties: MailConfigurationProperties) {

  ConfigLoader.loadProperties(mailConfigurationProperties.toProperties, true)

  private lazy val mailer = MailerBuilder
    .withDebugLogging(mailConfigurationProperties.debug)
    .withThreadPoolSize(mailConfigurationProperties.threadPoolSize)
    .withConnectionPoolCoreSize(mailConfigurationProperties.connectionPoolCoreSize)
    .buildMailer()

  def sendHtmlMail(to: String, subject: String, content: String): Task[Any] = {
    val email = EmailBuilder
      .startingBlank()
      .to(to)
      .withSubject(subject)
      .appendTextHTML(content)
      .buildEmail()
    val ret = mailer.sendMail(email)
    ZIO.fromFuture(make => ret.getFuture.asScala()(make))
  }
}

object MailService {

  type ZMailService = Has[MailService]

  def apply(mailConfigurationProperties: MailConfigurationProperties): MailService = new MailService(
    mailConfigurationProperties
  )

}
