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

import scala.jdk.FutureConverters.CompletionStageOps

import org.bitlap.zim.infrastructure.properties._
import org.simplejavamail.api.mailer._
import org.simplejavamail.config._
import org.simplejavamail.email._
import org.simplejavamail.mailer._
import zio._

/** 邮件发送服务
 *
 *  @author
 *    梦境迷离
 *  @version 1.0,2021/12/30
 */
final class MailServiceImpl(mailConfigurationProperties: MailConfigurationProperties) {

  ConfigLoader.loadProperties(mailConfigurationProperties.toProperties, true)

  private lazy val mailer: Mailer = MailerBuilder
    .withDebugLogging(mailConfigurationProperties.debug)
    .withSessionTimeout(3000.millis.toMillis.toInt)
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
      .ignore
    // catch all exception
  }
}

object MailServiceImpl {

  def apply(mailConfigurationProperties: MailConfigurationProperties): MailServiceImpl = new MailServiceImpl(
    mailConfigurationProperties
  )
  def sendHtmlMail(to: String, subject: String, content: String): URIO[MailServiceImpl, Any] =
    ZIO.environmentWithZIO(_.get.sendHtmlMail(to, subject, content))

  lazy val live: URLayer[MailConfigurationProperties, MailServiceImpl] = ZLayer(
    ZIO.service[MailConfigurationProperties].map(MailServiceImpl.apply)
  )
}
