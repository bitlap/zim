package org.bitlap.zim.server.api

import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.bitlap.zim.server.api.ZimUserApi.ZZimUserApi
import org.bitlap.zim.server.application.TestApplication
import org.bitlap.zim.server.application.impl.ApiService
import org.bitlap.zim.server.configuration.ZimServiceConfiguration
import org.bitlap.zim.server.repository.TangibleUserRepository
import zio.{ TaskLayer, ZIO }
import akka.util.Timeout
import scala.concurrent.duration._

/**
 * 测试akka-http route
 *
 * @author 梦境迷离
 * @since 2022/2/11
 * @version 1.0
 */
class ZimUserApiSpec extends TestApplication with ZimServiceConfiguration with ScalatestRouteTest {

  implicit val timeout = Timeout(10000.milliseconds)

  val api: TaskLayer[ZZimUserApi] = ZimUserApi.make(ApiService.make(userApplicationLayer), materializerLayer)

  def getRoute(zapi: ZimUserApi => Route): Route = {
    val s = ZIO.serviceWith[ZimUserApi](api => ZIO.effect(zapi(api))).provideLayer(api)
    unsafeRun(s)
  }

  "The ZimUserApi service" should "return a json for GET empty data" in {
    Get("/user/getOne?id=1") ~> getRoute(_.userGetRoute) ~> check {
      responseAs[String] shouldEqual """{"data":null,"msg":"操作成功","code":0}"""
    }
  }

  "The ZimUserApi service" should "return a json for GET" in {
    unsafeRun(TangibleUserRepository.saveUser(mockUser).provideLayer(userLayer).runHead)
    Get("/user/getOne?id=1") ~> getRoute(_.userGetRoute) ~> check {
      responseAs[String] shouldEqual """{"data":{"id":1,"username":"zhangsan","password":"123456","sign":"","avatar":"/static/image/avatar/avatar(3).jpg","email":"dreamylost@outlook.com","createDate":"2022-02-11 00:00:00","sex":1,"status":"nonactivated","active":"1ade893a1b1940a5bb8dc8447538a6a6a18ad80bcf84437a8cfb67213337202d"},"msg":"操作成功","code":0}"""
    }
  }
}
