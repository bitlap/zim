package org.bitlap.zim.server.application

import org.bitlap.zim.server.configuration.properties.MailConfigurationProperties.ZMailConfigurationProperties
import org.bitlap.zim.server.configuration.properties.MailConfigurationProperties
import org.bitlap.zim.server.util.ImplicitUtil._
import org.simplejavamail.api.mailer.Mailer
import org.simplejavamail.config.ConfigLoader
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder
import zio.{ Has, UIO, ZIO, ZLayer }
import zio.URIO
import zio.ULayer
import zio.URLayer

/**
 * 邮件发送服务
 *
 * @author 梦境迷离
 * @version 1.0,2021/12/30
 */
final class MailService(mailConfigurationProperties: MailConfigurationProperties) {

  ConfigLoader.loadProperties(mailConfigurationProperties.toProperties, true)

  private lazy val mailer: Mailer = MailerBuilder
    .withDebugLogging(mailConfigurationProperties.debug)
    .withThreadPoolSize(mailConfigurationProperties.threadPoolSize)
    .withConnectionPoolCoreSize(mailConfigurationProperties.connectionPoolCoreSize)
    .buildMailer()

  def sendHtmlMail(to: String, subject: String, content: String): UIO[Any] = {
    val email = EmailBuilder
      .startingBlank()
      .from(mailConfigurationProperties.sender)
      .to(to)
      .withSubject(subject)
      .appendTextHTML(content)
      .buildEmail()
    ZIO
      .fromFuture(make => mailer.sendMail(email).getFuture.asScala()(make))
      .catchAllCause(_ => ZIO.unit) // 捕获所有异常
  }
}

object MailService {

  type ZMailService = Has[MailService]

  def apply(mailConfigurationProperties: MailConfigurationProperties): MailService = new MailService(
    mailConfigurationProperties
  )

  def sendHtmlMail(to: String, subject: String, content: String): URIO[ZMailService, Any] =
    ZIO.access(_.get.sendHtmlMail(to, subject, content))

  val live: URLayer[ZMailConfigurationProperties, ZMailService] =
    ZLayer.fromService[MailConfigurationProperties, MailService](MailService(_))

  def make(mailConfigurationProperties: MailConfigurationProperties): ULayer[ZMailService] =
    ZLayer.succeed(mailConfigurationProperties) >>> MailService.live
}
