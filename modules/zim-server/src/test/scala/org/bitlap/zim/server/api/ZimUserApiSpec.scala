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

package org.bitlap.zim.server.api

import akka.http.scaladsl.marshalling.{ Marshaller, ToEntityMarshaller }
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.{ RouteTestTimeout, ScalatestRouteTest }
import akka.testkit.TestDuration
import io.circe.syntax.EncoderOps
import org.bitlap.zim.domain.input.{ ExistEmailInput, GroupInput, RegisterUserInput, UpdateSignInput, UpdateUserInput }
import org.bitlap.zim.server.api.ZimUserApi.ZZimUserApi
import org.bitlap.zim.server.application.TestApplication
import org.bitlap.zim.server.application.impl.ApiService
import org.bitlap.zim.server.configuration.ZimServiceConfiguration
import org.bitlap.zim.server.repository.TangibleUserRepository
import zio.{ TaskLayer, ZIO }
import akka.http.scaladsl.model.headers.Cookie
import org.bitlap.zim.server.repository._
import org.bitlap.zim.domain.model.GroupMember

import scala.concurrent.duration._
import org.bitlap.zim.server.application.UserApplication
import org.bitlap.zim.domain.model.User
import org.bitlap.zim.domain.model.GroupList

/**
 * 测试akka-http route
 *
 * @author 梦境迷离
 * @since 2022/2/11
 * @version 1.0
 */
class ZimUserApiSpec extends TestApplication with ZimServiceConfiguration with ScalatestRouteTest {

  implicit val timeout = RouteTestTimeout(15.seconds.dilated)
  val authorityHeaders = Seq(Cookie("Authorization", "ZHJlYW15bG9zdEBvdXRsb29rLmNvbToxMjM0NTY="))
  val pwdUser = mockUser.copy(password = "jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=")

  val api: TaskLayer[ZZimUserApi] = ZimUserApi.make(ApiService.make(userApplicationLayer), materializerLayer)

  def getRoute(zapi: ZimUserApi => Route): Route = {
    val s = ZIO.serviceWith[ZimUserApi](api => ZIO.effect(zapi(api))).provideLayer(api)
    unsafeRun(s)
  }

  def createRegisterUser(user: User = mockUser): Option[Boolean] =
    unsafeRun(ZIO.serviceWith[UserApplication](_.saveUser(user).runHead).provideLayer(userApplicationLayer))

  def createGroup(uid: Int = 1, gid: Int = 1): Option[Boolean] = {
    unsafeRun(
      ZIO
        .serviceWith[UserApplication](_.createGroup(GroupList(0, "梦境迷离", "", uid)).runHead)
        .provideLayer(userApplicationLayer)
    )
    unsafeRun(ZIO.serviceWith[UserApplication](_.addGroupMember(gid, uid).runHead).provideLayer(userApplicationLayer))
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

  "register failed" should "OK for POST" in {
    implicit val m: ToEntityMarshaller[RegisterUserInput] = Marshaller.withFixedContentType(`application/json`) { f =>
      HttpEntity(`application/json`, f.asJson.noSpaces)
    }

    Post("/user/register", RegisterUserInput("username", "", "")) ~> getRoute(
      _.registerRoute
    ) ~> check {
      responseAs[String] shouldEqual """{"data":null,"msg":"参数错误","code":1}"""
    }

    Post("/user/register", RegisterUserInput("username", "password", "")) ~> getRoute(
      _.registerRoute
    ) ~> check {
      responseAs[String] shouldEqual """{"data":null,"msg":"邮箱格式不正确","code":1}"""
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
    // 使用repository直接创建一个用户记录
    val user = unsafeRun(TangibleUserRepository.saveUser(pwdUser).provideLayer(userLayer).runHead)
    println(s"user => $user")
    Get(s"/user/active/1ade893a1b1940a5bb8dc8447538a6a6a18ad80bcf84437a8cfb67213337202d") ~> getRoute(
      _.activeRoute
    ) ~> check {
      println(s"result => ${responseAs[String]}")
      responseAs[String] shouldEqual """{"data":1,"msg":"操作成功","code":0}"""
    }
  }

  "active user failed" should "OK" in {
    // 使用repository直接创建一个用户记录
    val user = unsafeRun(TangibleUserRepository.saveUser(pwdUser).provideLayer(userLayer).runHead)
    println(s"user => $user")
    Get(s"/user/active/11") ~> getRoute(
      _.activeRoute
    ) ~> check {
      println(s"result => ${responseAs[String]}")
      responseAs[String] shouldEqual """{"data":0,"msg":"操作失败","code":1}"""
    }
  }

  "updateInfo user" should "OK" in {
    implicit val m: ToEntityMarshaller[UpdateUserInput] = Marshaller.withFixedContentType(`application/json`) { f =>
      HttpEntity(`application/json`, f.asJson.noSpaces)
    }
    // 使用service模拟注册创建一个完成的用户记录
    val user = createRegisterUser()
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
    val user = createRegisterUser()
    println(s"user => $user")
    Post(s"/user/updateSign", UpdateSignInput("梦境迷离")).withHeaders(authorityHeaders) ~> getRoute(
      _.updateSignRoute
    ) ~> check {
      println(s"result => ${responseAs[String]}")
      responseAs[String] shouldEqual """{"data":true,"msg":"操作成功","code":0}"""
    }
  }

  "createGroup and find groupMember" should "OK" in {
    implicit val m: ToEntityMarshaller[GroupInput] = Marshaller.withFixedContentType(`application/json`) { f =>
      HttpEntity(`application/json`, f.asJson.noSpaces)
    }
    val user = unsafeRun(TangibleUserRepository.saveUser(pwdUser).provideLayer(userLayer).runHead)
    println(s"user => $user")
    Post(s"/user/createGroup", GroupInput("梦境迷离", "", user.getOrElse(1L).toInt))
      .withHeaders(authorityHeaders) ~> getRoute(
      _.createGroupRoute
    ) ~> check {
      println(s"result => ${responseAs[String]}")
      responseAs[String] shouldEqual """{"data":1,"msg":"操作成功","code":0}"""

      val groupMember =
        unsafeRun(TangibleGroupMemberRepository.findGroupMembers(1).provideLayer(groupMemberLayer).runHead)
      groupMember.getOrElse(0) shouldEqual user.getOrElse(1L).toInt

      val leave = unsafeRun(
        TangibleGroupMemberRepository
          .leaveOutGroup(GroupMember(1, user.getOrElse(1L).toInt))
          .provideLayer(groupMemberLayer)
          .runHead
      )
      leave shouldEqual Some(1)
    }
  }

  "getMembers is nonEmpty" should "OK" in {
    // 使用service的saveUser会导致激活码每次都是随机的无法用于测试校验，所以直接用repository创建
    val user = unsafeRun(TangibleUserRepository.saveUser(pwdUser).provideLayer(userLayer).runHead)
    println(s"user => $user")
    createGroup()
    Get(s"/user/getMembers?id=1")
      .withHeaders(authorityHeaders) ~> getRoute(
      _.getMembersRoute
    ) ~> check {
      println(s"result => ${responseAs[String]}")
      responseAs[String] shouldEqual """{"data":{"id":0,"groupname":"","list":[{"id":1,"username":"zhangsan","password":"jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=","sign":"","avatar":"/static/image/avatar/avatar(3).jpg","email":"dreamylost@outlook.com","createDate":"2022-02-11 00:00:00","sex":1,"status":"nonactivated","active":"1ade893a1b1940a5bb8dc8447538a6a6a18ad80bcf84437a8cfb67213337202d"}]},"msg":"操作成功","code":0}"""
    }
  }

  "getOffLineMessage is empty" should "OK" in {
    val user = createRegisterUser()
    println(s"user => $user")
    Get(s"/user/getOffLineMessage").withHeaders(authorityHeaders) ~> getRoute(
      _.getOffLineMessageRoute
    ) ~> check {
      println(s"result => ${responseAs[String]}")
      responseAs[String] shouldEqual """{"data":[],"msg":"操作成功","code":0}"""
    }
  }

  "getMembers is empty" should "OK" in {
    val user = createRegisterUser()
    println(s"user => $user")
    Get(s"/user/getMembers?id=1").withHeaders(authorityHeaders) ~> getRoute(
      _.getMembersRoute
    ) ~> check {
      println(s"result => ${responseAs[String]}")
      responseAs[String] shouldEqual """{"data":{"id":0,"groupname":"","list":[]},"msg":"操作成功","code":0}"""
    }
  }

  "findMyGroups is empty" should "OK" in {
    val user = createRegisterUser()
    println(s"user => $user")
    Get(s"/user/findMyGroups?createId=1").withHeaders(authorityHeaders) ~> getRoute(
      _.findMyGroupsRoute
    ) ~> check {
      println(s"result => ${responseAs[String]}")
      responseAs[String] shouldEqual """{"data":[],"msg":"操作成功","code":0,"pages":1}"""
    }
  }

  "existEmail exist" should "OK" in {
    implicit val m: ToEntityMarshaller[ExistEmailInput] = Marshaller.withFixedContentType(`application/json`) { f =>
      HttpEntity(`application/json`, f.asJson.noSpaces)
    }
    val user = unsafeRun(TangibleUserRepository.saveUser(pwdUser).provideLayer(userLayer).runHead)
    println(s"user => $user")
    Post(s"/user/existEmail", ExistEmailInput("dreamylost@outlook.com")) ~> getRoute(_.existEmailRoute) ~> check {
      println(s"result => ${responseAs[String]}")
      responseAs[String] shouldEqual
        """{"data":true,"msg":"操作成功","code":0}"""
    }
  }

  "existEmail not exist" should "OK" in {
    implicit val m: ToEntityMarshaller[ExistEmailInput] = Marshaller.withFixedContentType(`application/json`) { f =>
      HttpEntity(`application/json`, f.asJson.noSpaces)
    }
    val user = unsafeRun(TangibleUserRepository.saveUser(pwdUser).provideLayer(userLayer).runHead)
    println(s"user => $user")
    Post(s"/user/existEmail", ExistEmailInput("2233@outlook.com")) ~> getRoute(_.existEmailRoute) ~> check {
      println(s"result => ${responseAs[String]}")
      responseAs[String] shouldEqual
        """{"data":false,"msg":"操作失败","code":1}"""
    }

    Post(s"/user/existEmail", ExistEmailInput("")) ~> getRoute(_.existEmailRoute) ~> check {
      println(s"result => ${responseAs[String]}")
      responseAs[String] shouldEqual
        """{"data":null,"msg":"参数错误","code":1}"""
    }
  }

  "updateInfo user oldPwd failed" should "OK" in {
    implicit val m: ToEntityMarshaller[UpdateUserInput] = Marshaller.withFixedContentType(`application/json`) { f =>
      HttpEntity(`application/json`, f.asJson.noSpaces)
    }
    // 使用service模拟注册创建一个完成的用户记录
    val user = createRegisterUser()
    println(s"user => $user")
    Post(s"/user/updateInfo", UpdateUserInput(1, "lisi", Some(""), Some(""), "", ""))
      .withHeaders(authorityHeaders) ~> getRoute(
      _.updateInfoRoute
    ) ~> check {
      println(s"result => ${responseAs[String]}")
      responseAs[String] shouldEqual """{"data":null,"msg":"旧密码不正确","code":1}"""
    }

    Post(s"/user/updateInfo", UpdateUserInput(1, "lisi", None, Some(""), "", ""))
      .withHeaders(authorityHeaders) ~> getRoute(
      _.updateInfoRoute
    ) ~> check {
      println(s"result => ${responseAs[String]}")
      responseAs[String] shouldEqual """{"data":true,"msg":"操作成功","code":0}"""

      val userNew =
        unsafeRun(TangibleUserRepository.findUsers(Some("lisi"), None).provideLayer(userLayer).runHead)

      userNew.isDefined shouldBe true
    }
  }

}
