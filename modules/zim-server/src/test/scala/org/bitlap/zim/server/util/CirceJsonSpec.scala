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

package org.bitlap.zim.server.util

import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import org.bitlap.zim.domain.input.UserSecurity.UserSecurityInfo
import org.bitlap.zim.domain.model.{ GroupList, User }
import org.bitlap.zim.domain.{ FriendAndGroupInfo, FriendList, Message, Mine, ResultSet }
import org.bitlap.zim.api.ApiJsonCodec
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** @author
 *    梦境迷离
 *  @since 2022/1/16
 *  @version 1.0
 */
class CirceJsonSpec extends AnyFlatSpec with Matchers with ApiJsonCodec {

  "Message fromJson" should "ok" in {
    val msg    = """{"type":"changOnline","mine":"null","to":"null","msg":"online"}"""
    val msgObj = decode[Message](msg).getOrElse(null)
    msgObj.`type` shouldBe "changOnline"

    val objStr = msgObj.asJson.noSpaces

    // obj default is null
    objStr shouldBe """{"type":"changOnline","mine":"null","to":"null","msg":"online"}"""

  }

  "Mine fromJson" should "ok" in {
    val msg =
      """{"id": 1,"username": null,"mine": false,"avatar": "aaa","content": ""}""".stripMargin
    val msgObj = decode[Mine](msg).getOrElse(null)
    msgObj.id shouldBe 1

    // String default is `""`
    val objStr = msgObj.asJson.noSpaces
    objStr shouldBe """{"id":1,"username":"","mine":false,"avatar":"aaa","content":""}"""

  }

  "FriendAndGroupInfo" should "ok" in {
    val fagi = FriendAndGroupInfo(
      mine = User(0, "", ""),
      friend = List(FriendList(0, "", Nil)),
      group = List(GroupList(0, "", "", 0))
    )
    val ret = ResultSet(data = fagi)
    ret.asJson != null shouldBe true
  }

  "userSecurityInfo" should "ok" in {
    val userSecurity = UserSecurityInfo(
      0,
      "12@qqcom.c",
      "123",
      "u"
    )
    val json = userSecurity.asJson(UserSecurityInfo.encoder).noSpaces
    println(json)

    val obj = decode[UserSecurityInfo](json).getOrElse(null)
    println(obj)

    json != null shouldBe true
  }

  "Message detail" should "ok" in {
    val json =
      """
        |{
        |    "type": "addFriend",
        |    "mine": {
        |        "id": 107,
        |        "username": "梦境迷离",
        |        "password": "jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=",
        |        "sign": "梦境迷离啊",
        |        "avatar": "/static/image/avatar/5fa385431b7f492987e97d94b6d1b34a.JPG",
        |        "email": "15605832957@sina.com",
        |        "createDate": "2002-10-20 00:00:00",
        |        "sex": 1,
        |        "status": "online",
        |        "active": "123"
        |    },
        |    "to": {
        |        "id": "107"
        |    },
        |    "msg": "{\"groupId\":\"14\",\"remark\":\"\",\"type\":\"0\"}"
        |}
        |""".stripMargin
    val obj = decode[Message](json).getOrElse(null)
    println(s"obj1=$obj")
    assert(obj != null)

    val json2 =
      """{"type":"addFriend","mine":{"id":107,"username":"梦境迷离","password":"jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=","sign":"梦境迷离啊","avatar":"/static/image/avatar/5fa385431b7f492987e97d94b6d1b34a.JPG","email":"15605832957@sina.com","createDate":"2002-10-20 00:00:00","sex":1,"status":"online","active":"123"},"to":{"id":"107"},"msg":"{\"groupId\":\"14\",\"remark\":\"\",\"type\":\"0\"}"}"""
    val obj2 = decode[Message](json2).getOrElse(null)
    println(s"obj2=$obj2")
    assert(obj2.mine != null)

    val mineJson =
      """
        |{
        |    "id": 107,
        |    "username": "梦境迷离",
        |    "password": "jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=",
        |    "sign": "梦境迷离啊",
        |    "avatar": "/static/image/avatar/5fa385431b7f492987e97d94b6d1b34a.JPG",
        |    "email": "15605832957@sina.com",
        |    "createDate": "2002-10-20 00:00:00",
        |    "sex": 1,
        |    "status": "online",
        |    "active": "123"
        |}
        |""".stripMargin
    val mineObj = decode[Mine](mineJson)
    println(s"mineObj=$mineObj")
    assert(mineObj != null)
  }
}
