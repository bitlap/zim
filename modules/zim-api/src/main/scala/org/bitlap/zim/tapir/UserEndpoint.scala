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

package org.bitlap.zim.tapir

import akka.stream.scaladsl.Source
import akka.util.ByteString
import org.bitlap.zim.domain
import org.bitlap.zim.domain.ZimError._
import org.bitlap.zim.domain._
import org.bitlap.zim.domain.input.UserSecurity.UserSecurityInfo
import org.bitlap.zim.domain.input._
import org.bitlap.zim.domain.model.{ GroupList, User }
import sttp.capabilities.akka.AkkaStreams
import sttp.model.HeaderNames.Authorization
import sttp.model.headers.CookieValueWithMeta
import sttp.model.{ HeaderNames, Uri }
import sttp.tapir._
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe._
import sttp.tapir.server.PartialServerEndpoint

import scala.concurrent.Future

/** 用户接口的端点
 *
 *  @author
 *    梦境迷离
 *  @since 2021/12/25
 *  @version 1.0
 */
trait UserEndpoint extends ApiErrorMapping {

  // API 最前缀path
  private[tapir] lazy val userResource: EndpointInput[Unit] = "user"
  // API  资源描述
  private[tapir] lazy val userResourceDescription: String = "User Endpoint"

  type ZimSecurityOut[In] = PartialServerEndpoint[
    UserSecurity,
    UserSecurityInfo,
    In,
    ZimError,
    Source[ByteString, Any],
    Any with AkkaStreams,
    Future
  ]
  type ZimOut[In] = PublicEndpoint[In, ZimError, Source[ByteString, Any], Any with AkkaStreams]

  type ZimFileOut = PartialServerEndpoint[UserSecurity, UserSecurityInfo, MultipartInput, ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future]

  val secureEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, Unit, Unauthorized, Unit, Any, Future]

  // ================================================用户API定义（这是用于测试的接口）===============================================================
  lazy val userGetOneEndpoint: ZimOut[Long] =
    endpoint.get
      .in(userResource / "getOne" / query[Long]("id").example(1L).description("query parameter"))
      .name("查询一个用户")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[User].schemaType), CodecFormat.Json()))
      .errorOut(errorOut)
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  // ================================================用户API定义（正式接口）这些API目前不是标准的restful api===============================================================
  lazy val leaveOutGroupEndpoint: ZimSecurityOut[LeaveOutGroupInput] =
    secureEndpoint.post
      .in(userResource / "leaveOutGroup")
      .in(jsonBody[LeaveOutGroupInput])
      .name("退出群")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(schemaType = SchemaType.SInteger()), CodecFormat.Json()))
      .errorOutVariants(errorOutVar.head, errorOutVar.tail: _*)

  lazy val removeFriendEndpoint: ZimSecurityOut[RemoveFriendInput] =
    secureEndpoint.post
      .in(userResource / "removeFriend")
      .in(jsonBody[RemoveFriendInput])
      .name("删除好友")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean()), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val changeGroupEndpoint: ZimSecurityOut[ChangeGroupInput] =
    secureEndpoint.post
      .in(userResource / "changeGroup")
      .in(jsonBody[ChangeGroupInput])
      .name("移动好友分组")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean()), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val refuseFriendEndpoint: ZimSecurityOut[RefuseFriendInput] =
    secureEndpoint.post
      .in(userResource / "refuseFriend")
      .in(jsonBody[RefuseFriendInput])
      .name("拒绝添加好友")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean()), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val agreeFriendEndpoint: ZimSecurityOut[AgreeFriendInput] =
    secureEndpoint.post
      .in(userResource / "agreeFriend")
      .in(jsonBody[AgreeFriendInput])
      .name("同意添加好友")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean()), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val findAddInfoEndpoint: ZimSecurityOut[(Int, Int)] =
    secureEndpoint.get
      .in(userResource / "findAddInfo" / query[Int]("uid") / query[Int]("page"))
      .name("查询消息盒子信息")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derivedSchema[AddInfo].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val findUsersEndpoint: ZimSecurityOut[(Int, Option[String], String)] =
    secureEndpoint.get
      .in(
        userResource / "findUsers" / query[Int]("page")
          .default(1) / query[Option[String]]("name")
          .default(None) / query[String]("sex").default("")
      )
      .name("分页查找好友")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[User].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val findGroupsEndpoint: ZimSecurityOut[(Int, Option[String])] =
    secureEndpoint.get
      .in(userResource / "findGroups" / query[Int]("page").default(1) / query[Option[String]]("name").default(None))
      .name("分页查找群组")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[GroupList].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val findMyGroupsEndpoint: ZimSecurityOut[(Int, Int)] =
    secureEndpoint.get
      .in(userResource / "findMyGroups" / query[Int]("page").default(1) / query[Int]("createId"))
      .name("分页查询我的创建的群组")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[GroupList].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val chatLogEndpoint: ZimSecurityOut[(Int, String, Int)] =
    secureEndpoint.get
      .in(userResource / "chatLog" / query[Int]("id") / query[String]("type") / query[Int]("page").default(1))
      .name("获取聊天记录")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[domain.ChatHistory].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val getOffLineMessageEndpoint: ZimSecurityOut[Unit] =
    secureEndpoint.get
      .in(userResource / "getOffLineMessage")
      .name("获取离线消息")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[domain.ChatHistory].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val updateSignEndpoint: ZimSecurityOut[UpdateSignInput] =
    secureEndpoint.post
      .in(userResource / "updateSign")
      .in(jsonBody[UpdateSignInput])
      .name("更新签名")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean()), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val initEndpoint: ZimSecurityOut[Int] =
    secureEndpoint.post
      .in(userResource / "init" / path[Int]("userId"))
      .name("初始化主界面数据")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derivedSchema[FriendAndGroupInfo].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val getMembersEndpoint: ZimSecurityOut[Int] =
    secureEndpoint.get
      .in(userResource / "getMembers" / query[Int]("id"))
      .name("获取群成员")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[FriendList].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val uploadImageEndpoint: ZimFileOut =
    secureEndpoint.post
      .in(userResource / "upload" / "image")
      .in(multipartBody[MultipartInput])
      .name("客户端上传图片")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[UploadResult].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val uploadFileEndpoint: ZimFileOut =
    secureEndpoint.post
      .in(userResource / "upload" / "file")
      .in(multipartBody[MultipartInput])
      .name("客户端上传文件")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[UploadResult].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val uploadGroupAvatarEndpoint: ZimFileOut =
    secureEndpoint.post
      .in(userResource / "upload" / "groupAvatar")
      .in(multipartBody[MultipartInput])
      .name("上传群组头像")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[UploadResult].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val updateAvatarEndpoint: ZimFileOut =
    secureEndpoint.post
      .in(userResource / "updateAvatar")
      .in(multipartBody[MultipartInput])
      .name("更新用户头像")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[UploadResult].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val createGroupEndpoint: ZimSecurityOut[GroupInput] =
    secureEndpoint.post
      .in(userResource / "createGroup")
      .in(jsonBody[GroupInput])
      .name("用户创建群组")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SInteger()), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val createUserGroupEndpoint: ZimSecurityOut[FriendGroupInput] =
    secureEndpoint.post
      .in(userResource / "createUserGroup")
      .in(jsonBody[FriendGroupInput])
      .name("用户创建好友分组")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SInteger()), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val updateInfoEndpoint: ZimSecurityOut[UpdateUserInput] =
    secureEndpoint.post
      .in(userResource / "updateInfo")
      .in(
        jsonBody[UpdateUserInput]
          .example(UpdateUserInput(1, "userName", Some("pwd"), Some("oldpwd"), "sign", "nan"))
          .description("user info")
      )
      .name("更新信息个人信息")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean()), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val findUserEndpoint: ZimSecurityOut[Int] =
    secureEndpoint.get
      .in(userResource / "findUser" / query[Int]("id").example(1).description("user id"))
      .name("根据id查找用户信息")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[User].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  // =======================================不需要鉴权=====================================
  lazy val activeUserEndpoint: PublicEndpoint[String, String, (Uri, Source[ByteString, Any]), Any with AkkaStreams] =
    endpoint.get
      .in(userResource / "active" / path[String]("activeCode"))
      .name("激活")
      .description(userResourceDescription)
      .out(statusCode(sttp.model.StatusCode.PermanentRedirect))
      .out(header[Uri](HeaderNames.Location))
      .out(streamBody(AkkaStreams)(Schema(Schema.schemaForInt.schemaType), CodecFormat.Json()))
      .errorOut(customJsonBody[String].description("Redirect Error"))

  lazy val existEmailEndpoint: ZimOut[ExistEmailInput] =
    endpoint.post
      .in(userResource / "existEmail")
      .in(jsonBody[ExistEmailInput].example(ExistEmailInput("dreamylost@outlook.com")).description("user mail"))
      .name("判断邮件是否存在")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean()), CodecFormat.Json()))
      .errorOut(errorOut)
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val registerEndpoint: ZimOut[RegisterUserInput] =
    endpoint.post
      .in(userResource / "register")
      .in(jsonBody[RegisterUserInput])
      .name("注册")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean()), CodecFormat.Json()))
      .errorOut(errorOut)
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val loginEndpoint
    : Endpoint[Unit, UserSecurityInfo, ZimError, (CookieValueWithMeta, Source[ByteString, Any]), Any with AkkaStreams] =
    endpoint.post
      .in(userResource / "login")
      .in(jsonBody[UserSecurityInfo])
      .name("登录")
      .description(userResourceDescription)
      .out(setCookie(Authorization))
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[User].schemaType), CodecFormat.Json()))
      .errorOut(errorOut)
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)
}
