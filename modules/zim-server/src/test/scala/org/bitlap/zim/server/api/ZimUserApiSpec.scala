/*
 * Copyright 2021 bitlap
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

package org.bitlap.zim.server.api

import akka.http.scaladsl.marshalling.{ Marshaller, ToEntityMarshaller }
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.{ RouteTestTimeout, ScalatestRouteTest }
import akka.testkit.TestDuration
import io.circe.syntax.EncoderOps
import org.bitlap.zim.domain.input.RegisterUserInput
import org.bitlap.zim.server.api.ZimUserApi.ZZimUserApi
import org.bitlap.zim.server.application.TestApplication
import org.bitlap.zim.server.application.impl.ApiService
import org.bitlap.zim.server.configuration.ZimServiceConfiguration
import org.bitlap.zim.server.repository.TangibleUserRepository
import zio.{ TaskLayer, ZIO }
import akka.http.scaladsl.model.headers.Cookie
import org.bitlap.zim.domain.input.UpdateUserInput
import org.bitlap.zim.domain.input.UpdateSignInput
import org.bitlap.zim.domain.input.GroupInput
import org.bitlap.zim.server.repository._

import scala.concurrent.duration._

/**
 * 测试akka-http route
 *
 * @author 梦境迷离
 * @since 2022/2/11
 * @version 1.0
 */
class ZimUserApiSpec extends TestApplication with ZimServiceConfiguration with ScalatestRouteTest {

  implicit val timeout = RouteTestTimeout(5.seconds.dilated)
  val authorityHeaders = Seq(Cookie("Authorization", "ZHJlYW15bG9zdEBvdXRsb29rLmNvbToxMjM0NTY="))
  val pwdUser = mockUser.copy(password = "jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=")

  val api: TaskLayer[ZZimUserApi] = ZimUserApi.make(ApiService.make(userApplicationLayer), materializerLayer)

  def getRoute(zapi: ZimUserApi => Route): Route = {
    val s = ZIO.serviceWith[ZimUserApi](api => ZIO.effect(zapi(api))).provideLayer(api)
    unsafeRun(s)
  }

  "getOne" should "OK for GET empty data" in {
    Get("/user/getOne?id=1") ~> getRoute(_.userGetRoute) ~> check {
      responseAs[String] shouldEqual """{"data":null,"msg":"操作成功","code":0}"""
    }
  }

  "getOne" should "OK for GET" in {
    val user = unsafeRun(TangibleUserRepository.saveUser(pwdUser).provideLayer(userLayer).runHead)
    println(s"user => $user")
    Get(s"/user/getOne?id=${user.getOrElse(1L)}") ~> getRoute(_.userGetRoute) ~> check {
      responseAs[String] shouldEqual """{"data":{"id":1,"username":"zhangsan","password":"jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=","sign":"","avatar":"/static/image/avatar/avatar(3).jpg","email":"dreamylost@outlook.com","createDate":"2022-02-11 00:00:00","sex":1,"status":"nonactivated","active":"1ade893a1b1940a5bb8dc8447538a6a6a18ad80bcf84437a8cfb67213337202d"},"msg":"操作成功","code":0}"""
    }
  }

  "register" should "OK for POST" in {
    implicit val m: ToEntityMarshaller[RegisterUserInput] = Marshaller.withFixedContentType(`application/json`) { f =>
      HttpEntity(`application/json`, f.asJson.noSpaces)
    }

    Post("/user/register", RegisterUserInput("username", "password", "dreamylost@outlook.com")) ~> getRoute(
      _.registerRoute
    ) ~> check {
      responseAs[String] shouldEqual """{"data":true,"msg":"操作成功","code":0}"""
    }
  }

  "init user" should "OK" in {
    val user = unsafeRun(TangibleUserRepository.saveUser(pwdUser).provideLayer(userLayer).runHead)
    Post(s"/user/init/${user.getOrElse(1L)}").withHeaders(authorityHeaders) ~> getRoute(_.initRoute) ~> check {
      println(s"result => ${responseAs[String]}")
      responseAs[String] shouldEqual
        """{"data":{"mine":{"id":1,"username":"zhangsan","password":"jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=","sign":"","avatar":"/static/image/avatar/avatar(3).jpg","email":"dreamylost@outlook.com","createDate":"2022-02-11 00:00:00","sex":1,"status":"online","active":"1ade893a1b1940a5bb8dc8447538a6a6a18ad80bcf84437a8cfb67213337202d"},"friend":[],"group":[]},"msg":"操作成功","code":0}"""
    }
  }

  "active user" should "OK" in {
    val user = unsafeRun(TangibleUserRepository.saveUser(pwdUser).provideLayer(userLayer).runHead)
    println(s"user => $user")
    Get(s"/user/active/1ade893a1b1940a5bb8dc8447538a6a6a18ad80bcf84437a8cfb67213337202d") ~> getRoute(
      _.activeRoute
    ) ~> check {
      println(s"result => ${responseAs[String]}")
      responseAs[String] shouldEqual """{"data":1,"msg":"操作成功","code":0}"""
    }
  }

  "updateInfo user" should "OK" in {
    implicit val m: ToEntityMarshaller[UpdateUserInput] = Marshaller.withFixedContentType(`application/json`) { f =>
      HttpEntity(`application/json`, f.asJson.noSpaces)
    }
    val user = unsafeRun(TangibleUserRepository.saveUser(pwdUser).provideLayer(userLayer).runHead)
    println(s"user => $user")
    Post(s"/user/updateInfo", UpdateUserInput(1, "lisi", None, None, "", "")).withHeaders(authorityHeaders) ~> getRoute(
      _.updateInfoRoute
    ) ~> check {
      println(s"result => ${responseAs[String]}")
      responseAs[String] shouldEqual """{"data":true,"msg":"操作成功","code":0}"""
    }
  }

  "updateSign user" should "OK" in {
    implicit val m: ToEntityMarshaller[UpdateSignInput] = Marshaller.withFixedContentType(`application/json`) { f =>
      HttpEntity(`application/json`, f.asJson.noSpaces)
    }
    val user = unsafeRun(TangibleUserRepository.saveUser(pwdUser).provideLayer(userLayer).runHead)
    println(s"user => $user")
    Post(s"/user/updateSign", UpdateSignInput("梦境迷离")).withHeaders(authorityHeaders) ~> getRoute(
      _.updateSignRoute
    ) ~> check {
      println(s"result => ${responseAs[String]}")
      responseAs[String] shouldEqual """{"data":true,"msg":"操作成功","code":0}"""
    }
  }

  "createGroup user and find groupMember" should "OK" in {
    implicit val m: ToEntityMarshaller[GroupInput] = Marshaller.withFixedContentType(`application/json`) { f =>
      HttpEntity(`application/json`, f.asJson.noSpaces)
    }
    val user = unsafeRun(TangibleUserRepository.saveUser(pwdUser).provideLayer(userLayer).runHead)
    println(s"user => $user")
    Post(s"/user/createGroup", GroupInput("梦境迷离", "", 1)).withHeaders(authorityHeaders) ~> getRoute(
      _.createGroupRoute
    ) ~> check {
      println(s"result => ${responseAs[String]}")
      responseAs[String] shouldEqual """{"data":1,"msg":"操作成功","code":0}"""

      val groupMember =
        unsafeRun(TangibleGroupMemberRepository.findGroupMembers(1).provideLayer(groupMemberLayer).runHead)
      groupMember.getOrElse(0) shouldEqual 1
    }
  }
}
