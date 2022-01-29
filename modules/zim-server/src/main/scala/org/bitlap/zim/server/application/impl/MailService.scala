package org.bitlap.zim.server.application.impl

import org.bitlap.zim.server.configuration.properties.MailConfigurationProperties
import org.bitlap.zim.server.configuration.properties.MailConfigurationProperties.ZMailConfigurationProperties
import org.bitlap.zim.server.util.ImplicitUtil._
import org.bitlap.zim.server.util.LogUtil
import org.simplejavamail.api.mailer.Mailer
import org.simplejavamail.config.ConfigLoader
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder
import zio.{ Has, UIO, ULayer, URIO, URLayer, ZIO, ZLayer }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

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
    .withSessionTimeout(3000.millis._1.toInt)
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

    val future = mailer.sendMail(email, true).getFuture.asScala()

    ZIO
      .fromFuture(_ => future)
      .catchAllCause { e =>
        LogUtil.error(e.map(_.getLocalizedMessage).prettyPrint)
      } // catch all exception
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
