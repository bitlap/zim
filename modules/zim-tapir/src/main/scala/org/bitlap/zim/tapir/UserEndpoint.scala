package org.bitlap.zim.tapir

import akka.stream.scaladsl.Source
import akka.util.ByteString
import org.bitlap.zim.domain
import org.bitlap.zim.domain._
import org.bitlap.zim.domain.input.{ ExistEmailInput, UserInput, UserSecurity }
import org.bitlap.zim.domain.model.{ FriendGroup, GroupList, User }
import org.bitlap.zim.domain.ZimError._
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir._
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe._
import sttp.tapir.server.PartialServerEndpoint
import sttp.model.headers.CookieValueWithMeta

import scala.concurrent.Future
import org.bitlap.zim.domain.input.UserSecurity.UserSecurityInfo

/**
 * 用户接口的端点
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait UserEndpoint extends ApiErrorMapping {

  lazy val Authorization: String = "Authorization"

  // API 最前缀path
  private[tapir] lazy val userResource: EndpointInput[Unit] = "user"
  // API  资源描述
  private[tapir] lazy val userResourceDescription: String = "User Endpoint"

  val secureEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, Unit, Unauthorized, Unit, Any, Future]

  //================================================用户API定义（这是用于测试的接口）===============================================================
  lazy val userGetOneEndpoint: Endpoint[Unit, Long, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.get
      .in(userResource / "getOne" / query[Long]("id").example(1L).description("query parameter"))
      .name("查询一个用户")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[User].schemaType), CodecFormat.Json()))
      .errorOut(errorOut)
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  //================================================用户API定义（正式接口）===============================================================
  lazy val leaveOutGroupEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, (Int, Int), ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.post
      .in(userResource / "leaveOutGroup" / query[Int]("groupId") / query[Int]("uid"))
      .name("退出群")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(schemaType = SchemaType.SInteger()), CodecFormat.Json()))
      .errorOutVariants(errorOutVar.head, errorOutVar.tail: _*)

  lazy val removeFriendEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, Int, ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.post
      .in(userResource / "removeFriend" / query[Int]("removeFriend"))
      .name("删除好友")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean()), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val changeGroupEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, (Int, Int), ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.post
      .in(userResource / "changeGroup" / query[Int]("groupId") / query[Int]("userId"))
      .name("移动好友分组")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean()), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val refuseFriendEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, (Int, Int), ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.post
      .in(userResource / "refuseFriend" / query[Int]("messageBoxId") / query[Int]("to"))
      .name("拒绝添加好友")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean()), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val agreeFriendEndpoint
    : PartialServerEndpoint[UserSecurity, UserSecurityInfo, (Int, Int, Int, Int), ZimError, Source[
      ByteString,
      Any
    ], Any with AkkaStreams, Future] =
    secureEndpoint.post
      .in(
        userResource / "agreeFriend" / query[Int]("uid") / query[Int]("from_group") / query[Int]("group") / query[Int](
          "messageBoxId"
        )
      )
      .name("同意添加好友")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean()), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val findAddInfoEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, (Int, Int), ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.get
      .in(userResource / "findAddInfo" / query[Int]("uid") / query[Int]("page"))
      .name("查询消息盒子信息")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derivedSchema[AddInfo].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val findUsersEndpoint
    : PartialServerEndpoint[UserSecurity, UserSecurityInfo, (Int, Option[Boolean], Option[Int]), ZimError, Source[
      ByteString,
      Any
    ], Any with AkkaStreams, Future] =
    secureEndpoint.get
      .in(
        userResource / "findUsers" / query[Int]("page")
          .default(1) / query[Option[Boolean]]("name") / query[Option[Int]]("sex")
      )
      .name("分页查找好友")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[User].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val findGroupsEndpoint
    : PartialServerEndpoint[UserSecurity, UserSecurityInfo, (Int, Option[Boolean]), ZimError, Source[
      ByteString,
      Any
    ], Any with AkkaStreams, Future] =
    secureEndpoint.get
      .in(userResource / "findGroups" / query[Int]("page").default(1) / query[Option[Boolean]]("name"))
      .name("分页查找群组")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[GroupList].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val findMyGroupsEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, (Int, Int), ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.get
      .in(userResource / "findMyGroups" / query[Int]("page").default(1) / query[Int]("createId"))
      .name("分页查询我的创建的群组")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[GroupList].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val chatLogEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, (Int, String, Int), ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.post
      .in(userResource / "chatLog" / query[Int]("id") / query[String]("type") / query[Int]("page").default(1))
      .name("获取聊天记录")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[domain.ChatHistory].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  // TODO 返回页面且携带数据
  lazy val chatLogIndexEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, (Int, String), ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.get
      .in(userResource / "chatLogIndex" / query[Int]("id") / query[String]("type"))
      .name("弹出聊天记录页面")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[domain.ChatHistory].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val getOffLineMessageEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, Unit, ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.post
      .in(userResource / "getOffLineMessage")
      .name("获取离线消息")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[domain.ChatHistory].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val updateSignEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, String, ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.post
      .in(userResource / "updateSign" / query[String]("sign"))
      .name("更新签名")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean()), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val initEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, Int, ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.post
      .in(userResource / "init" / path[Int]("userId"))
      .name("初始化主界面数据")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derivedSchema[FriendAndGroupInfo].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val getMembersEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, Unit, ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.get
      .in(userResource / "getMembers")
      .name("获取群成员")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[FriendList].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  // TODO 上传文件 file
  lazy val uploadImageEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, Unit, ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.post
      .in(userResource / "upload" / "image")
      .name("客户端上传图片")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[UploadResult].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  // TODO 上传文件 file
  lazy val uploadFileEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, Unit, ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.post
      .in(userResource / "upload" / "file")
      .name("客户端上传文件")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[UploadResult].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  // TODO 上传文件 file
  lazy val uploadGroupAvatarEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, Unit, ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.post
      .in(userResource / "upload" / "groupAvatar")
      .name("上传群组头像")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[UploadResult].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  // TODO 上传文件 avatar
  lazy val updateAvatarEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, Unit, ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.post
      .in(userResource / "updateAvatar")
      .name("用户更新头像")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[UploadResult].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val createGroupEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, GroupList, ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.post
      .in(userResource / "createGroup")
      .in(jsonBody[GroupList])
      .name("用户创建群组")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SInteger()), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val createUserGroupEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, FriendGroup, ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.post
      .in(userResource / "createUserGroup")
      .in(jsonBody[domain.model.FriendGroup])
      .name("用户创建好友分组")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SInteger()), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val updateInfoEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, UserInput, ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.post
      .in(userResource / "updateInfo")
      .in(
        jsonBody[UserInput].example(UserInput(1, "userName", "pwd", "oldpwd", "sign", "nan")).description("user info")
      )
      .name("更新信息个人信息")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean()), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  // TODO 跳转主页
  lazy val indexEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, Unit, ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.get
      .in(userResource / "index")
      .name("跳转主页")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SString()), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val findUserEndpoint: PartialServerEndpoint[UserSecurity, UserSecurityInfo, Int, ZimError, Source[
    ByteString,
    Any
  ], Any with AkkaStreams, Future] =
    secureEndpoint.get
      .in(userResource / "findUser" / query[Int]("id").example(1).description("user id"))
      .name("根据id查找用户信息")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[User].schemaType), CodecFormat.Json()))
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  // =======================================不需要鉴权=====================================
  lazy val existEmailEndpoint
    : PublicEndpoint[ExistEmailInput, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.post
      .in(userResource / "existEmail")
      .in(jsonBody[ExistEmailInput].example(ExistEmailInput("dreamylost@outlook.com")).description("user mail"))
      .name("判断邮件是否存在")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean()), CodecFormat.Json()))
      .errorOut(errorOut)
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  // TODO 返回的是redirect
  lazy val activeUserEndpoint: PublicEndpoint[String, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.get
      .in(userResource / "active" / path[String]("activeCode"))
      .name("激活")
      .description(userResourceDescription)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SString()), CodecFormat.Json()))
      .errorOut(errorOut)
      .errorOutVariants[ZimError](errorOutVar.head, errorOutVar.tail: _*)

  lazy val registerEndpoint: PublicEndpoint[User, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.post
      .in(userResource / "register")
      .in(jsonBody[User])
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

  // 由于schema是严格的，domain 的case class可能必须改成Option

}
