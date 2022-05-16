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

package org.bitlap.zim.server.service.impl

import org.bitlap.zim.infrastructure.properties.MailConfigurationProperties
import org.simplejavamail.api.mailer.Mailer
import org.simplejavamail.config.ConfigLoader
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder
import zio.{ Has, UIO, ULayer, URIO, URLayer, ZIO, ZLayer }
import org.bitlap.zim.infrastructure.properties.MailConfigurationProperties.ZMailConfigurationProperties

import scala.concurrent.duration._
import scala.jdk.FutureConverters.CompletionStageOps

/** 邮件发送服务
 *
 *  @author
 *    梦境迷离
 *  @version 1.0,2021/12/30
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

    val future = mailer.sendMail(email, true).asScala

    ZIO
      .fromFuture(_ => future)
      .catchAll(_ => ZIO.unit)
    // catch all exception
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
